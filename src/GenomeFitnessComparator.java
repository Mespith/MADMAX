public class GenomeFitnessComparator implements java.util.Comparator<Genome>{
    public int compare(Genome g1, Genome g2){
        return g1.fitness < g2.fitness ? -1 : 1;
    }
}
