import java.util.Random;

/**
 * Created by Jaimy on 02/12/2015.
 * This class represents the loop of the evolution process.
 */
public class Evolution {
    private static long SEED = 54;
    private static Population population;
    private static Random rng;

    public static void main() {
        rng = new Random(SEED);
        population = initialize_population();
        evolve(1);
    }

    private static Population initialize_population() {
        // I took these values from the paper.
        double c1 = 1.0;
        double c2 = 1.0;
        double c3 = 3.0;
        int nr_of_inputs = 19;
        int nr_of_outputs = 7;
        int population_size = 100;
        double p_new_node = 0.03;
        double p_new_connection = 0.05;
        double p_mutate_weight = 0.8;
        double p_random_weight = 0.1;
        double comp_threshold = 3.0;

        // This one I guessed
        double permutation = 0.2;

        return new Population(c1, c2, c3, nr_of_inputs, nr_of_outputs, p_new_node, p_new_connection,
                p_mutate_weight, p_random_weight, permutation, comp_threshold, population_size, rng);
    }

    private static void evolve(int nr_of_generations) {
        for (int generation = 1; generation <= nr_of_generations; generation++) {
            population.Spieciefy();
            population.TestGeneration();
            population.NewGeneration();
        }
        population.Spieciefy();
        population.TestGeneration();

        Parser p = new Parser();
        p.WriteForESN(population.BestIndividual());
    }
}
