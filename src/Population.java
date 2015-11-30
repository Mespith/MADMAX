import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class Population {

    private Double c1, c2, c3;
    private Double compatibility_threshold;
    private List<Genome> Species;
    private List<Genome> Generation;

    // (Re)Assign species
    public ArrayList<ArrayList<Genome>> Spieciefy() {
        ArrayList<ArrayList<Genome>> generation_species = new ArrayList<>();
        Boolean added = false;
        // Loop through all the individuals of this generation.
        for (int i = 0; i < Generation.size(); i++) {
            Genome individual = Generation.get(i);
            // Loop through all the known species.
            for (int j = 0; j < Species.size(); j++) {
                Double comp = compatibility(individual, Species.get(j));
                // If the individual is compatible with the species, it is assigned to it.
                if (comp < compatibility_threshold) {
                    generation_species.get(j).add(individual);
                    added = true;
                    break;
                }
            }
            // If the individual was nog assigned to any species, it is a new species.
            if (!added) {
                Species.add(individual);
            }
        }
        return generation_species;
    }

    // Kill the worst performing individuals of each species

    // Create offspring to replace the whole population

    private Double compatibility(Genome g1, Genome g2)
    {
        Integer E = excess_genes(g1, g2);
        Integer D = disjoint_genes(g1, g2);
        Double W = matching_genes(g1, g2);
        Integer N = Math.max(g1.N(), g2.N());

        return (c1 * E) / N + (c2 * D) / N + c3 * W;
    }

    private Integer excess_genes(Genome g1, Genome g2) { return 0; }

    private Integer disjoint_genes(Genome g1, Genome g2) { return 0; }

    private  Double matching_genes(Genome g1, Genome g2) { return 0.; }

}
