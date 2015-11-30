/**
 * Created by Jaimy on 26/11/2015.
 */
public class Population {

    private Double c1, c2, c3;

    // (Re)Assign species

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
