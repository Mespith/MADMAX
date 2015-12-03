import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.client.Controller;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResults;
import race.TorcsConfiguration;

import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class Population {
    
    //innovation number is the number of the latest innovation added to the population, inNodes # of input nodes, outNodes # of output nodes
    public int Innovation_nr, inNodes, outNodes;
    private double P_addNode, P_addWeight, P_mutateWeights, P_changeWeight, P_permuteWeight, permutation;

    private double c1, c2, c3;
    private double compatibility_threshold;
    private List<Genome> Species;
    private List<Genome> Generation;
    private ArrayList<ArrayList<Genome>> generation_species;
    
    //temporary constructor:
    Population(double c1, double c2, double c3, int inNodes, int outNodes, double P_addNode, double P_addWeight, double P_mutateWeights,
               double P_changeWeight, double permutation, double compatibility_threshold, int pop_size) {
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
        this.Innovation_nr = 0;

        // Fill the population with new individuals.
        for (int i = 0; i < pop_size; i++) {
            Generation.add(new Genome(this, inNodes, outNodes));
        }
    }
               
    // (Re)Assign species
    public void Spieciefy() {
        generation_species = new ArrayList<>();
        Boolean added = false;
        // Loop through all the individuals of this generation.
        for (int i = 0; i < Generation.size(); i++) {
            Genome individual = Generation.get(i);
            // Loop through all the known species.
            for (int j = 0; j < Species.size(); j++) {
                Genome species = Species.get(j);
                DEW_Genes comp_analysis = new DEW_Genes(individual, species);
                Double comp = compatibility(Math.max(individual.N, species.N), comp_analysis);
                // If the individual is compatible with the species, it is assigned to it.
                if (comp < compatibility_threshold) {
                    generation_species.get(j).add(individual);
                    added = true;
                    break;
                }
            }
            // If the individual was nog assigned to any species, it is a new species.
            if (!added) {
                ArrayList<Genome> new_species = new ArrayList<>();
                new_species.add(individual);
                generation_species.add(new_species);
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
        for (int species = 0; species < generation_species.size(); species++ ) {
            List<DefaultDriver> drivers = new ArrayList<>();
            for (int i = 0; i < generation_species.get(species).size(); i++) {
                DefaultDriver driver = new DefaultDriver(generation_species.get(species).get(i).Parse(inNodes, outNodes));
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
                generation_species.get(species).get(j).fitness = drivers.get(j).position;
            }
            generation_species.get(species).sort(new GenomeComparator());
        }
    }

    // Kill the worst performing individuals of each species.
    // Create offspring to replace the whole population.
    public void NewGeneration() {
        shrink_species();
    }

    // Return the best performing individual
    public EchoStateNet BestIndividual() {
        return Generation.get(0).Parse(inNodes, outNodes);
    }

//Still need to create potentials nested list of integers, and if we want to keep nodes as a hashset then it needs to be changed everywhere else
    // - N is put to the longest genome, corresponding to the genome length of the offspring
    private Genome crossover(Genome g1, Genome g2, DEW_Genes DEW) {
        int N;
        boolean P; //probability of offspring inheriting disjoint and excess genes from g1 or g2
        if (g1.fitness < g2.fitness) {
            N = g2.getConnections().size();
            P = false;
        } else {
            N = g1.getConnections().size();
            P = true;
        }

        List<ConnectionGene> genes = new ArrayList<ConnectionGene>(N);
        HashSet<Integer> nodes = new HashSet<Integer>();
        List<List<Integer>> potentials = new ArrayList<List<Integer>>();

        //start with the shared genes, 50/50 chance of inheriting from either parent
        for (int i = 0; i < DEW.N; i++) {
            if (Math.random() < 0.5) {
                genes.set(i, new ConnectionGene(g1.getConnections().get(i)));
                nodes.add(g1.getConnections().get(i).in_node);
                nodes.add(g1.getConnections().get(i).out_node);

            } else {
                genes.set(i, new ConnectionGene(g2.getConnections().get(i)));
                nodes.add(g2.getConnections().get(i).in_node);
                nodes.add(g2.getConnections().get(i).out_node);
            }
        }

        for (int i = DEW.N; i < N; i++){ //now copy the excess and disjoint genes from most fit parent
            if (P){
                genes.set(i, new ConnectionGene(g1.getConnections().get(i)));
                nodes.add(g1.getConnections().get(i).in_node);
                nodes.add(g1.getConnections().get(i).out_node);
            }
            else{
                genes.set(i, new ConnectionGene(g2.getConnections().get(i)));
                nodes.add(g2.getConnections().get(i).in_node);
                nodes.add(g2.getConnections().get(i).out_node);
            }
        }

        return new Genome(genes, nodes, potentials, this);
    }

    private double compatibility(int N, DEW_Genes DEW) //N should be passed as Math.max(g1.N(), g2.N()), DEW as DEW_Genes(g1, g2)
    {
        return (c1 * DEW.E + c2 * DEW.D) / N + c3 * DEW.W;
    }


    // Method to get one genome to represent the species for the next generation.
    private void shrink_species() {
        for (int i = 0; i < generation_species.size(); i++) {
            if (i < Species.size()) {
                Species.set(i, generation_species.get(i).get((int)(Math.random() * 10)));
            }
            else {
                Species.add(generation_species.get(i).get((int)(Math.random() * 10)));
            }
        }
        generation_species.clear();
    }
}
