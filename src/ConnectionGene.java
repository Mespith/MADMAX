import java.util.Comparator;
public class ConnectionGene {
    public int in_node;
    public int out_node;
    public double weight;
    public boolean expressed;
    private int innovation_nr;
    ConnectionGene(int in_node, int out_node, int innovation_nr){
        this.weight = Math.random();
        this.in_node = in_node;
        this.out_node = out_node;
        this.expressed = true;
        this.innovation_nr = innovation_nr;
    }
    ConnectionGene(int in_node, int out_node, double weight, int innovation_nr){
        this.weight = weight;
        this.in_node = in_node;
        this.out_node = out_node;
        this.expressed = true;
        this.innovation_nr = innovation_nr;
    }
    ConnectionGene(ConnectionGene g){ //boolean sets expressed to false if disable is true
        this.weight = g.getWeight();
        this.innovation_nr = g.getInnovation_nr();
        this.in_node = g.in_node;
        this.out_node = g.out_node;
        this.expressed = g.expressed;
    }
    public int getInnovation_nr(){
        return innovation_nr;
    }
}