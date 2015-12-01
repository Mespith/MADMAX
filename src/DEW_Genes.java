/**
 * Created by Jaimy on 30/11/2015.
 */
public class DEW_Genes {
    private Integer E;
    private Integer D;
    private Double W;

    DEW_Genes(Genome g1, Genome g2) {
        E = 0;
        Integer N = 0;
        W = 0.;

        Integer i1 = g1.getGenome()[0].getInnovation_nr();
        Integer i2 = g2.getGenome()[0].getInnovation_nr();

        while (i1 == i2 && N < g1.N() && N < g2.N()) {
            W += Math.abs(g1.getGenome()[N].getWeight() - g2.getGenome()[N].getWeight());
            N++;
            if (N < g1.N() && N < g2.N()) {
                i1 = g1.getGenome()[N].getInnovation_nr();
                i2 = g2.getGenome()[N].getInnovation_nr();
            }
        }

        W /= N;

        i1 = g1.getGenome()[g1.N() - 1].getInnovation_nr();
        i2 = g2.getGenome()[g2.N() - 1].getInnovation_nr();

        if (i1 < i2) {
            while (i1 < i2) {
                E++;
                i2 = g2.getGenome()[g2.N() - 1 - E].getInnovation_nr();
            }
        } else if (i2 < i1) {
            while (i2 < i1) {
                E++;
                i1 = g1.getGenome()[g1.N() - 1 - E].getInnovation_nr();
            }
        }

        D = g1.N() + g2.N() - 2 * (N + E);
    }

    public Integer getE(){ return E; }
    public Integer getD(){ return D; }
    public Double getW(){ return W; }

}
