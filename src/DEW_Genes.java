public class DEW_Genes {
    public int E;
    public int D;
    public double W;
    public int N;

    DEW_Genes(Genome g1, Genome g2) {
        E = 0;
        N = 0; //# of shared genes
        W = 0.;

        int i1 = g1.getConnections().get(0).getInnovation_nr();
        int i2 = g2.getConnections().get(0).getInnovation_nr();

        while (i1 == i2 && N < g1.getN() && N < g2.getN()) {
            W += Math.abs(g1.getConnections().get(N).weight - g2.getConnections().get(N).weight);
            N++;
            if (N < g1.getN() && N < g2.getN()) {
                i1 = g1.getConnections().get(N).getInnovation_nr();
                i2 = g2.getConnections().get(N).getInnovation_nr();
            }
        }

        W /= N;

        i1 = g1.getConnections().get(g1.getN() - 1).getInnovation_nr();
        i2 = g2.getConnections().get(g2.getN() - 1).getInnovation_nr();

        if (i1 < i2) {
            while (i1 < i2) {
                E++;
                i2 = g2.getConnections().get(g2.getN() - 1 - E).getInnovation_nr();
            }
        } else if (i2 < i1) {
            while (i2 < i1) {
                E++;
                i1 = g1.getConnections().get(g1.getN() - 1 - E).getInnovation_nr();
            }
        }

        D = g1.getN() + g2.getN() - 2 * N - E;
    }
}
