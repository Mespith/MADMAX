import cicontest.torcs.client.Controller;
import cicontest.torcs.race.Race;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Population implements Serializable {
    
    //innovation number is the number of the latest innovation added to the population, inNodes # of input nodes, outNodes # of output nodes
    public int innovation_nr, nodeId, inNodes, outNodes;
    public double P_addNode, P_addWeight, P_mutateWeights, P_changeWeight, P_randomizeWeight, P_permuteWeight, permutation, kill_rate, mutation_rate;

    private double c1, c2, c3;
    private double compatibility_threshold;

    private transient List<Genome> species;
    private transient List<Genome> generation;
    private ArrayList<ArrayList<Genome>> generationSpecies;

    private static String[][] tracknames = {{"road" ,"aalborg" },    {"road" ,"alpine-1" },   {"road" ,"alpine-2" },
                                            {"road" ,"brondehach" }, {"road" ,"corkscrew" },  {"road" ,"eroad" },
                                            {"road" ,"e-track-1" },  {"road" ,"e-track-2" },  {"road" ,"e-track-3" },
                                            {"road" ,"e-track-4" },  {"road" ,"e-track-6" },  {"road" ,"forza" },
                                            {"road" ,"g-track-1" },  {"road" ,"g-track-2" },  {"road" ,"g-track-3" },
                                            {"road" ,"ole-road-1" }, {"road" ,"ruudskogen" }, {"road" ,"spring" },
                                            {"road" ,"street-1" },   {"road" ,"wheel-1" },    {"road" ,"wheel-2" },
                                            {"dirt" ,"dirt-1" },     {"dirt" ,"dirt-2" },     {"dirt" ,"dirt-3" },
                                            {"dirt" ,"dirt-4" },     {"dirt" ,"dirt-5" },     {"dirt" ,"dirt-6" },
                                            {"dirt" ,"mixed-1" },    {"dirt" ,"mixed-2" },    {"oval" ,"a-speedway" },
                                            {"oval" ,"b-speedway" }, {"oval" ,"c-speedway" }, {"oval" ,"d-speedway" },
                                            {"oval" ,"e-speedway" }, {"oval" ,"f-speedway" }, {"oval" ,"g-speedway" },
                                            {"oval" ,"e-track-5" },  {"oval" ,"michigan" }};
    public Random rng;
    public int trackIdx;
    //temporary constructor:
    Population(double c1, double c2, double c3, int inNodes, int outNodes, double kill_rate, double mutation_rate, double P_addNode, double P_addWeight, double P_mutateWeights,
               double P_changeWeight, double P_randomizeWeight, double permutation, double compatibility_threshold, int pop_size, Random rng) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.inNodes = inNodes;
        this.outNodes = outNodes;
        this.nodeId = inNodes + outNodes - 1;
        this.P_addNode = P_addNode;
        this.P_addWeight = P_addWeight;
        this.P_mutateWeights = P_mutateWeights;
        this.P_changeWeight = P_changeWeight;
        this.P_randomizeWeight = P_randomizeWeight;
        this.permutation = permutation;
        this.compatibility_threshold = compatibility_threshold;
        this.innovation_nr = inNodes*outNodes;
        this.rng = rng;
        this.kill_rate = kill_rate;
        this.mutation_rate = mutation_rate;
        this.generation = new ArrayList<>(pop_size);
        this.species = new ArrayList<>();
        // Fill the population with new individuals.
        for (int i = 0; i < pop_size; i++) {
            Genome gen = new Genome(this);
            generation.add(gen);
        }
    }

    public List<Genome> getGeneration(){return generation;}

    // (Re)Assign species
    public void Speciefy() {
        //make a list of lists of genomes for every specie, making sure the ordering of the lists is the same as in the class variable species
        this.generationSpecies = new ArrayList<>(species.size());
        for (int i = 0; i < species.size(); i++){
            generationSpecies.add(new ArrayList<>());
        }
        // Loop through all the individuals of this generation.
        for (int i = 0; i < generation.size(); i++) {
            boolean added = false; //keeps track of whether individual i has been added to a specie or not
            Genome individual = generation.get(i);
            double compatibility = compatibility_threshold;
            //first check the speciesHint variable to see if the old species still applies to the individual
            if (individual.speciesHint != -1){
                DEW_Genes comp_analysis = new DEW_Genes(individual, species.get(individual.speciesHint));
                compatibility = compatibility(Math.max(individual.getN(), species.get(individual.speciesHint).getN()), comp_analysis);
            }
            // and add it to the list generationSpecies if it's still compatible.
            if (compatibility < compatibility_threshold) {
                generationSpecies.get(individual.speciesHint).add(individual);
                added = true;
            }
            // Or loop through all the known species if not.
            else {
                for (int j = 0; j < species.size(); j++) {
                    Genome speciesPrototype = species.get(j);
                    DEW_Genes comp_analysis = new DEW_Genes(individual, speciesPrototype);
                    compatibility = compatibility(Math.max(individual.getN(), speciesPrototype.getN()), comp_analysis);
                    // If the individual is compatible with the species, it is assigned to it.
                    if (compatibility < compatibility_threshold) {
                        individual.speciesHint = j;
                        generationSpecies.get(j).add(individual);
                        added = true;
                        break;
                    }
                }
            }
            // If the individual was not assigned to any species, it is a new species.
            if (!added) {
                individual.speciesHint = species.size();
                ArrayList<Genome> newSpecie = new ArrayList<>(1);
                newSpecie.add(individual);
                species.add(individual);
                generationSpecies.add(newSpecie);
            }
        }

        shrink_species();
    }

    // Parse every genome to a NN and use it to race.
    public void TestGeneration() {

        int racers = 1; // number of simultaneously tested genomes
        List<DefaultDriver> driverList = new ArrayList<>();

        //select track
        trackIdx = pickTrack();
        //System.out.println("Selected track: " + tracknames[trackIdx][1] + " from " + tracknames[trackIdx][0] + "s");

        for (int genomeIdx = 0; genomeIdx < generation.size();)
        {
            Race race = new Race();
            race.setTrack(tracknames[trackIdx][0], tracknames[trackIdx][1]);
            race.setTermination(Race.Termination.LAPS, 1);
            race.setStage(Controller.Stage.RACE);

            // add drivers
            for (int racerIdx = 0; racerIdx < racers && genomeIdx < generation.size(); racerIdx++)
            {           // pick out max of n racers at a time
                DefaultDriver driver = new DefaultDriver(new EchoStateNet(generation.get(genomeIdx)));
                driverList.add(driver);
                race.addCompetitor(driver);

                genomeIdx++;
            }
            // start race
            race.runWithGUI();

            int offset = driverList.size() - racers;
            for (int competitors = 0; competitors < racers; competitors++)
            {
                //System.out.println("Fitness of competitor"+(competitors+1)+": "+driverList.get(offset+competitors).tracker.getFitness());
            }
        }
        // calculate fitness for all genomes.
        for (int idx = 0; idx < generation.size(); idx++)
        {
            generation.get(idx).fitness = driverList.get(idx).tracker.getFitness();
        }

        // sort each species by fitness.
        for (List<Genome> s: generationSpecies)
        {
            s.sort(new GenomeFitnessComparator());
        }

        // print out best example of every species.
        for (int idx = 0; idx < generationSpecies.size(); idx++)
        {
            System.out.println("Best fitness of species " + idx + ": " + generationSpecies.get(idx).get(0).fitness);
        }

    }

    // Kill the worst performing individuals of each species.
    // Create offspring to replace the whole population.
    public void newGeneration() { //kill_rate should be around 0.6, mutation_rate around 0.25
        //store parent generation in OldGeneration variable

//        List<Genome> oldGeneration = new ArrayList<>(generation.size());
//        for (int i = 0; i < generationSpecies.size(); i++){
//            for (int j = 0; j < generationSpecies.get(i).size(); j++){
//                oldGeneration.add(new Genome(generationSpecies.get(i).get(j)));
//            }
//        }
        generation.clear();
        //Change the offspring generation in place
//        int genomeCounter = 0;
        for (int i = 0; i < generationSpecies.size(); i++) {
            ArrayList<Genome> s = generationSpecies.get(i);
            int individuals = s.size();
            switch (individuals) {
                case 1: {
                    Genome child = new Genome(s.get(0));
                    child.mutate();
                    generation.add(child);
                    break;
                }
                case 2: {
                    Genome child = new Genome(s.get(0));
                    child.mutate();
                    generation.add(child);
                    // If the best individual is way better than the other one, just kill the other.
                    if (s.get(0).fitness - s.get(1).fitness > 5) {
                        Genome child2 = new Genome(s.get(0));
                        child2.mutate();
                        generation.add(child2);
                    }
                    else {
                        generation.add(crossover(s.get(0), s.get(1)));
                    }
                    break;
                }
                default: {
                    int survive_index = (int) Math.round((1 - kill_rate) * individuals);
//                    int mutation_index = (int) Math.round(mutation_rate * individuals);
                    // If there are more than 5 individuals in the species, the best one should be left untouched
//                    int start_index = individuals > 5 ? 1 : 0;
                    for (int j = survive_index; j < individuals; j++) {
                        if (rng.nextDouble() < mutation_rate) {
                            int parent = (int) (Math.random() * survive_index);
                            Genome child = new Genome(s.get(parent));
                            child.mutate();
                            generation.add(child);
                        }
                        else {
                            int mom = (int) (Math.random() * survive_index), dad = (int) (Math.random() * survive_index);
                            generation.add(crossover(s.get(mom), s.get(dad)));
                        }
                    }
                    for (int j = 0; j < survive_index; j++) {
                        generation.add(new Genome(s.get(j)));
                    }
                    break;
                }
            }
//            genomeCounter += individuals;
        }
        generationSpecies.clear();
    }

    // Return the best performing individual
    public EchoStateNet BestIndividual() {
        return new EchoStateNet(generation.get(0));
    }

    private Genome crossover(Genome g1, Genome g2) {

        //find the # of shared genes
        int nSharedGenes = 0;
        int i1 = g1.getConnections().get(0).getInnovation_nr();
        int i2 = g2.getConnections().get(0).getInnovation_nr();

        while (i1 == i2 && nSharedGenes < g1.getN() && nSharedGenes < g2.getN()) {
            nSharedGenes++;
            if (nSharedGenes < g1.getN() && nSharedGenes < g2.getN()) {
                i1 = g1.getConnections().get(nSharedGenes).getInnovation_nr();
                i2 = g2.getConnections().get(nSharedGenes).getInnovation_nr();
            }
        }

        //create offspring as a copy of most fit parent
        Genome offspring = null;
        if (g1.fitness < g2.fitness) {
            offspring = new Genome(g1);
        } else {
            offspring = new Genome(g2);
        }

        //alter the shared genes, 50/50 chance of inheriting from either parent
        for (int i = 0; i < nSharedGenes; i++) {
            if(Math.random() < 0.5) {
                offspring.getConnections().set(i, new ConnectionGene(g1.getConnections().get(i)));
            }
            else{
                offspring.getConnections().set(i, new ConnectionGene(g2.getConnections().get(i)));
            }
        }

        return offspring;
    }
    
    private double compatibility(int N, DEW_Genes DEW) //N should be passed as Math.max(g1.N(), g2.N()), DEW as DEW_Genes(g1, g2)
    {
        return (c1 * DEW.E + c2 * DEW.D) / N + c3 * DEW.W;
    }


    // Method to get one genome to represent the species for the next generation.
    private void shrink_species() {
        for (int i = 0; i < generationSpecies.size(); i++) {
            if (i < species.size()) {
                species.set(i, new Genome(generationSpecies.get(i).get(0)));
            }
            else {
                species.add(new Genome(generationSpecies.get(i).get(0)));
            }
        }
    }

    public static Population loadPopulation(String path)
    {
        Population pop = null;
        try
        {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fis);
            pop = (Population) in.readObject();
            in.close();
            fis.close();
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        // now that pop is retrieved, reset fields: fill transient lists, set parentpopulations.

        pop.species = new ArrayList<>();
        pop.generation = new ArrayList<>();

        for (List<Genome> spec : pop.generationSpecies)
        {
            pop.species.add(spec.get(0));
            pop.generation.addAll(spec);
        }
        for (Genome gene: pop.generation)
        {
            gene.setParentPopulation(pop);
        }
        return pop;
    }

    public static int storePopulation(Population pop, String path)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(pop);
            out.close();
            fos.close();
        }catch (IOException e)
        {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    private int pickTrack()
    {
        if (trackIdx < 21) //last was road, pick dirt
        {
            return 21 + rng.nextInt(8);
        }else if (trackIdx < 29) // last was dirt, pick oval
        {
            return 29 + rng.nextInt(9);
        }else // last was oval, pick road
        {
            return  rng.nextInt(21);
        }
    }
}
