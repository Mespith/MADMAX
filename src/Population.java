import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.client.Controller;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResults;
import race.TorcsConfiguration;

import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jaimy on 26/11/2015.
 */

public class Population {
    
    //innovation number is the number of the latest innovation added to the population, inNodes # of input nodes, outNodes # of output nodes
    public int innovation_nr, nodeId, inNodes, outNodes;
    private double P_addNode, P_addWeight, P_mutateWeights, P_changeWeight, P_permuteWeight, permutation;

    private double c1, c2, c3;
    private double compatibility_threshold;
    
    private List<Genome> species;
    private List<Genome> generation;
    private List<Genome> oldGeneration;
    private ArrayList<ArrayList<Genome>> generationSpecies;

    public Random rng;

    //temporary constructor:
    Population(double c1, double c2, double c3, int inNodes, int outNodes, double P_addNode, double P_addWeight, double P_mutateWeights,
               double P_changeWeight, double permutation, double compatibility_threshold, int pop_size, Random rng) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.inNodes = inNodes;
        this.outNodes = outNodes;
        this.P_addNode = P_addNode;
        this.P_addWeight = P_addWeight;
        this.P_mutateWeights = P_mutateWeights;
        this.P_changeWeight = P_changeWeight;
        this.permutation = permutation;
        this.compatibility_threshold = compatibility_threshold;
        this.innovation_nr = inNodes*outNodes;
        this.nodeId = 0;
        this.rng = rng;
        // Fill the population with new individuals.
        for (int i = 0; i < pop_size; i++) {
            generation.add(new Genome(this));
        }
    }
    // (Re)Assign species
    public void Spieciefy() {
        generationSpecies = new ArrayList<>();
        Boolean added = false;
        // Loop through all the individuals of this generation.
        for (int i = 0; i < generation.size(); i++) {
            Genome individual = generation.get(i);
            // Loop through all the known species.
            for (int j = 0; j < species.size(); j++) {
                Genome species = species.get(j);
                DEW_Genes comp_analysis = new DEW_Genes(individual, species);
                Double comp = compatibility(Math.max(individual.getN(), species.getN()), comp_analysis);
                // If the individual is compatible with the species, it is assigned to it.
                if (comp < compatibility_threshold) {
                    generationSpecies.get(j).add(individual);
                    added = true;
                    break;
                }
            }
            // If the individual was nog assigned to any species, it is a new species.
            if (!added) {
                ArrayList<Genome> new_species = new ArrayList<>();
                new_species.add(individual);
                generationSpecies.add(new_species);
            }
        }
    }

    // Parse every genome to a NN and use it to race.
    public void TestGeneration() {
        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));
        DefaultDriverAlgorithm algorithm = new DefaultDriverAlgorithm();
        DriversUtils.registerMemory(algorithm.getDriverClass());

        // Create a driver for each genome
        for (int species = 0; species < generationSpecies.size(); species++ ) {
            List<DefaultDriver> drivers = new ArrayList<>();
            for (int i = 0; i < generationSpecies.get(species).size(); i++) {
                DefaultDriver driver = new DefaultDriver(new EchoStateNet(generationSpecies.get(species).get(i)));
                drivers.add(driver);
            }

            //Set-up race
            Race race = new Race();
            race.setTrack("road", "aalborg");
            race.setTermination(Race.Termination.LAPS, 1);
            race.setStage(Controller.Stage.RACE);
            for (int j = 0; j < drivers.size(); j++) {
                race.addCompetitor(drivers.get(j));
            }
            race.run();
            // Set the fitness for each genome.
            for (int j = 0; j < drivers.size(); j++) {
                generationSpecies.get(species).get(j).fitness = drivers.get(j).position;
            }
            generationSpecies.get(species).sort(new GenomeComparator());
        }
    }

    // Kill the worst performing individuals of each species.
    // Create offspring to replace the whole population.
    public void NewGeneration(double kill_rate, double mutation_rate) { //kill_rate should be around 0.6, mutation_rate around 0.25
        //store parent generation in OldGeneration variable
        int genomeCounter = 0;
        oldGeneration = new ArrayList<Genome>(generation.size());
        for (int i = 0; i < generationSpecies.size(); i++){
            for (int j = 0; j < generationSpecies.get(i).size(); j++){
                oldGeneration.set(genomeCounter++, new Genome(generationSpecies.get(i).get(j)));
            }
        }
        //Change the offspring generation in place
        genomeCounter = 0;
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
                species.set(i, generationSpecies.get(i).get((int)(Math.random() * 10)));
            }
            else {
                species.add(generationSpecies.get(i).get((int)(Math.random() * 10)));
            }
        }
        generationSpecies.clear();
    }
}
