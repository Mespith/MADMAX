import org.ejml.simple.SimpleMatrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class Genome {
    
    private Population parentPopulation;
    private double fitness;
    public int N;
    public int nr_of_nodes;
    private ConnectionGene[] genes;
    
    Genome(ConnectionGene[] genes, Population parentPopulation){
        this.parentPopulation = parentPopulation;
        this.genes = genes; //genes called by reference?
        this.fitness = 0;
        this.N = genes.length;
        this.nr_of_nodes = 0;//numberOfNodes;
    }
    
    //incomplete
    public void mutate(double P_addNode, double P_addWeight, double P_mutateWeights,
                       double P_permuteWeight, double permutation){

        if (Math.random() < P_mutateWeights) {
            for (int i = 0; i < N; i++) {
                if (Math.random() < P_permuteWeight) {
                    genes[i].setWeight(permutation*genes[i].getWeight());
                }
                else{
                    genes[i].setWeight(Math.random());
                }
            }
        }

        if (Math.random() < P_addWeight){
            int inNode = (int)(Math.random() * this.nr_of_nodes);
            parentPopulation.Innovation_nr++;
            //if ()
            //while ()


        }

        if (Math.random() < P_addNode){


        }

        // Add a connection

        // Add a node
    }
    
    public double getFitness(){return fitness;}

    public ConnectionGene[] Genes() { return genes; }

    public EchoStateNet Parse(Integer nr_of_inputs, Integer nr_of_outputs) {
        SimpleMatrix Win = new SimpleMatrix(nr_of_nodes, 1 + nr_of_inputs);
        SimpleMatrix W = new SimpleMatrix(nr_of_nodes, nr_of_nodes);
        SimpleMatrix Wout = new SimpleMatrix(nr_of_outputs, 1 + nr_of_nodes + nr_of_inputs);

        List<ConnectionGene> in_out = new ArrayList<>();
        List<ConnectionGene> in_hidden = new ArrayList<>();
        List<ConnectionGene> hidden_out = new ArrayList<>();
        List<ConnectionGene> hidden_hidden = new ArrayList<>();

        // Categorize the genes.
        for (int i = 0; i < genes.length; i++) {
            ConnectionGene gene = genes[i];
            if (gene.in_node.type == NodeGene.Type.Input) {
                if (gene.out_node.type == NodeGene.Type.Output) {
                    Wout.set(gene.out_node.id - nr_of_inputs - 1);
                    in_out.add(gene);
                }
                else {
                    in_hidden.add(gene);
                }
            }
            else if (gene.in_node.type == NodeGene.Type.Hidden) {
                if (gene.out_node.type == NodeGene.Type.Hidden){
                    hidden_hidden.add(gene);
                }
                else {
                    hidden_out.add(gene);
                }
            }
        }
         return null;

    }
}
