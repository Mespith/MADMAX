import org.ejml.simple.SimpleMatrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class Genome {

    public Integer nr_of_nodes;
    private ConnectionGene[] genes;

    public void mutate(){
        // Mutate the weights of the connections.

        // Add a connection

        // Add a node
    }

    public Integer N() {
        return genes.length;
    }

    public NeuralNetwork Parse(Integer nr_of_inputs, Integer nr_of_outputs) {
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
