import java.util.Comparator;

/**
 * Created by Jaimy on 03/12/2015.
 */
public class GenomeComparator implements Comparator<Genome> {
    public int compare(Genome g1, Genome g2){
        return (int)(g1.fitness - g2.fitness);
    }
}
