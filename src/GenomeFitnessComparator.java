public class GenomeFitnessComparator implements java.util.Comparator<Genome>{
    public int compare(Genome g1, Genome g2){
        if (g1.fitness == g2.fitness) {
            return 0;
        }
        else {
            return g1.fitness < g2.fitness ? 1 : -1;
        }
    }
}
