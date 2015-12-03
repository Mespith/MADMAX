import java.util.Comparator;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class ConnectionGene {

    public NodeGene in_node;
    public NodeGene out_node;
    public Double weight;
    public Boolean expressed;
    private Integer innovation_nr;
    
    ConnectionGene(NodeGene in_node, NodeGene out_node, int innovation_nr){
        this.weight = Math.random();
        this.in_node = in_node;
        this.out_node = out_node;
        this.expressed = true;
        this.innovation_nr = innovation_nr;
    }

    ConnectionGene(ConnectionGene g, boolean disable){ //boolean sets expressed to false if disable is true
        this.weight = g.getWeight();
        this.innovation_nr = g.getInnovation_nr();
        this.in_node = new NodeGene(g.getIn_node());
        this.out_node = new NodeGene(g.getOut_node());
        if (disable){
            this.expressed = false;
        }

    }

    public NodeGene getIn_node(){return in_node;}
    public NodeGene getOut_node(){return out_node;}

    public boolean getExpressed(){return expressed;}

    public double getWeight(){
        return weight;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public int getInnovation_nr(){
        return innovation_nr;
    }

}
