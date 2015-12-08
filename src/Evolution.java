import cicontest.algorithm.abstracts.DriversUtils;
import race.TorcsConfiguration;

import java.io.*;
import java.util.List;
import java.util.Random;

/**
 * Created by Jaimy on 02/12/2015.
 * This class represents the loop of the evolution process.
 */
public class Evolution {
    private static long SEED = 54;
    private static Population population;
    private static Random rng;
    public static String popSource = "";
    public static String popTarget = "C:\\Users\\Frederik\\Desktop\\pop1.ser";

    public static void main() {
        rng = new Random(SEED);
        if (popSource.equals(""))
        {
            population = initialize_population();
        } else
        {
            population = Population.loadPopulation(popSource);
        }
        for (Genome g : population.getGeneration()){
            g.mutate(population.P_addNode, population.P_addWeight, population.P_mutateWeights, population.P_permuteWeight, population.permutation);
        }

        startTorcs();

        evolve(10);

        if (!popTarget.equals(""))
        {
            Population.storePopulation(population, popTarget);
        }
    }

    private static void startTorcs()
    {
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));
        DefaultDriverAlgorithm algorithm = new DefaultDriverAlgorithm();
        DriversUtils.registerMemory(algorithm.getDriverClass());

        //Set path to torcs.properties
        try {
            Runtime.getRuntime().exec("cmd /c start StartTORCS.bat exit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Population initialize_population() {
        // I took these values from the paper.
        double c1 = 1.0;
        double c2 = 1.0;
        double c3 = 3.0;
        int nr_of_inputs = 15;
        int nr_of_outputs = 3;
        int population_size = 100;
        double p_new_node = 0.03;
        double p_new_connection = 0.05;
        double p_mutate_weight = 0.8;
        double p_random_weight = 0.1;
        double comp_threshold = 5.0;
        double kill_rate = 0.6;
        double mutation_rate = 0.25;

        // This one I guessed
        double permutation = 0.2;

        return new Population(c1, c2, c3, nr_of_inputs, nr_of_outputs, kill_rate, mutation_rate, p_new_node, p_new_connection,
                p_mutate_weight, p_random_weight, permutation, comp_threshold, population_size, rng);
    }

    private static void evolve(int nr_of_generations) {
        for (int generation = 1; generation <= nr_of_generations; generation++) {
            population.Speciefy();
            population.TestGeneration();
            population.newGeneration();
        }
        population.Speciefy();
        population.TestGeneration();

        Parser p = new Parser();
        p.WriteForESN(population.BestIndividual());
    }
}
