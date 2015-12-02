/**
 * Created by Jaimy on 30/11/2015.
 */
public class DEW_Genes {
    private int E;
    private int D;
    private double W;
    private int N;

    DEW_Genes(Genome g1, Genome g2) {
        E = 0;
        N = 0; //# of shared genes
        W = 0.;

        int i1 = g1.Genes()[0].getInnovation_nr();
        int i2 = g2.Genes()[0].getInnovation_nr();

        while (i1 == i2 && N < g1.N && N < g2.N) {
            W += Math.abs(g1.Genes()[N].getWeight() - g2.Genes()[N].getWeight());
            N++;
            if (N < g1.N && N < g2.N) {
                i1 = g1.Genes()[N].getInnovation_nr();
                i2 = g2.Genes()[N].getInnovation_nr();
            }
        }

        W /= N;

        i1 = g1.Genes()[g1.N - 1].getInnovation_nr();
        i2 = g2.Genes()[g2.N - 1].getInnovation_nr();

        if (i1 < i2) {
            while (i1 < i2) {
                E++;
                i2 = g2.Genes()[g2.N - 1 - E].getInnovation_nr();
            }
        } else if (i2 < i1) {
            while (i2 < i1) {
                E++;
                i1 = g1.Genes()[g1.N - 1 - E].getInnovation_nr();
            }
        }

        D = g1.N + g2.N - 2 * N - E;
    }

    public int getE(){ return E; }
    public int getD(){ return D; }
    public int getN(){ return N;}
    public double getW(){ return W; }

}
