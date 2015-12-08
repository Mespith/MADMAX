import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.client.Controller;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResults;
import race.TorcsConfiguration;

import java.io.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Population implements Serializable {
    
    //innovation number is the number of the latest innovation added to the population, inNodes # of input nodes, outNodes # of output nodes
    public int innovation_nr, nodeId, inNodes, outNodes;
    public double P_addNode, P_addWeight, P_mutateWeights, P_changeWeight, P_permuteWeight, permutation, kill_rate, mutation_rate;

    private double c1, c2, c3;
    private double compatibility_threshold;

    private transient List<Genome> species;
    private transient List<Genome> generation;
    private transient List<Genome> oldGeneration;
    private ArrayList<ArrayList<Genome>> generationSpecies;

    public Random rng;

    //temporary constructor:
    Population(double c1, double c2, double c3, int inNodes, int outNodes, double kill_rate, double mutation_rate, double P_addNode, double P_addWeight, double P_mutateWeights,
               double P_changeWeight, double permutation, double compatibility_threshold, int pop_size, Random rng) {
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

        //sort the individuals in every specie by fitness
        GenomeFitnessComparator compare = new GenomeFitnessComparator();
        for (int i = 0; i < generationSpecies.size(); i++){
            java.util.Collections.sort(generationSpecies.get(i), compare);
        }
    }

    // Parse every genome to a NN and use it to race.
    public void TestGeneration() {

        int racers = 1; // number of simultaneously tested genomes
        List<DefaultDriver> driverList = new ArrayList<>();

        for (int genomeIdx = 0; genomeIdx < generation.size();)
        {
            // set up race (just copied at this point)
            Race race = new Race();
            race.setTrack("road", "aalborg");
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
            race.run();

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
            int lastIdx = generationSpecies.get(idx).size() - 1;
            System.out.println("Best fitness of species " + idx + ": " + generationSpecies.get(idx).get(lastIdx).fitness);
        }

    }

    // Kill the worst performing individuals of each species.
    // Create offspring to replace the whole population.
    public void newGeneration() { //kill_rate should be around 0.6, mutation_rate around 0.25
        //store parent generation in OldGeneration variable

        oldGeneration = new ArrayList<>(generation.size());
        for (int i = 0; i < generationSpecies.size(); i++){
            for (int j = 0; j < generationSpecies.get(i).size(); j++){
                oldGeneration.add(new Genome(generationSpecies.get(i).get(j)));
            }
        }
        //Change the offspring generation in place
        int genomeCounter = 0;
        for (int i = 0; i < generationSpecies.size(); i++) {
            int individuals = generationSpecies.get(i).size();
            switch (individuals) {
                case 1: {
                    generationSpecies.get(i).get(0).mutate(P_addNode, P_addWeight, P_mutateWeights, P_permuteWeight, permutation);
                    break;
                }
                case 2: {
                    generationSpecies.get(i).set(1, crossover(generationSpecies.get(i).get(0), generationSpecies.get(i).get(1)));
                    generationSpecies.get(i).get(0).mutate(P_addNode, P_addWeight, P_mutateWeights, P_permuteWeight, permutation);
                    break;
                }
                default: {
                    int survive_index = (int) Math.round((1 - kill_rate) * individuals);
                    int mutation_index = (int)Math.round(mutation_rate*individuals);
                    for (int j = mutation_index; j < individuals; j++){
                        int mom = (int)(Math.random()*survive_index), dad = (int)(Math.random()*survive_index);
                        generationSpecies.get(i).set(j, crossover(oldGeneration.get(genomeCounter + mom), oldGeneration.get(genomeCounter + dad)));
                    }
                    for (int j = 0; j < mutation_index; j++){
                        int mutant = (int)(Math.random()*survive_index);
                        generationSpecies.get(i).set(j, new Genome(oldGeneration.get(genomeCounter + mutant)));
                        generationSpecies.get(i).get(j).mutate(P_addNode, P_addWeight, P_mutateWeights, P_permuteWeight, permutation);
                    }
                    break;
                }
            }
            genomeCounter += individuals;
        }
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
                species.set(i, generationSpecies.get(i).get(0));
            }
            else {
                species.add(generationSpecies.get(i).get(0));
            }
        }
        generationSpecies.clear();
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
        }catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        // now that pop is retrieved, reset fields: fill transient lists, set parentpopulations.

        pop.species = new ArrayList<>();
        pop.generation = new ArrayList<>();
        pop.oldGeneration = new ArrayList<>();

        for (List<Genome> spec : pop.generationSpecies)
        {
            pop.species.add(spec.get(0));
            pop.generation.addAll(spec);
        }
        for (Genome gene: pop.species)
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
}
