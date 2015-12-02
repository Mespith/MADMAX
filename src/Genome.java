import org.ejml.simple.SimpleMatrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class Genome {
    
    private Population parentPopulation;
    public double fitness;
    public int N;
    public Integer nr_of_nodes;
    private List<ConnectionGene> genes;
    private List<NodeGene> nodeGenes;

    Genome(List<ConnectionGene> genes, List<NodeGene> nodeGenes, Population parentPopulation){
        this.parentPopulation = parentPopulation;
        this.genes = genes; //genes called by reference?
        this.nodeGenes = nodeGenes;
        this.fitness = 0;
        this.N = genes.size;
        this.numberOfNodes = numberOfNodes;
    }

    //incomplete
    public void mutate(double P_addNode, double P_addWeight, double P_mutateWeights,
                       double P_permuteWeight, double permutation){

        if (Math.random() < P_mutateWeights) {
            for (int i = 0; i < N; i++) {
                if (Math.random() < P_permuteWeight) {
                    genes.get(i).setWeight(permutation*genes.get(i).getWeight());
                }
                else{
                    genes.get(i).setWeight(Math.random());
                }
            }
        }

        if (Math.random() < P_addWeight) {

            parentPopulation.innovationNumber++;

            do {
                int source = ((int) Math.random() * (nodeGenes.size - parentPopulation.outNodes) + parentPopulation.inNodes + parentPopulation.outNodes) % nodeGenes.size;
            } while (nodeGenes.get(source).potentials.isEmpty());

            int sink = (int) Math.random() * nodeGenes.get(source).potentials.size;

            ConnectionGene g = new ConnectionGene(nodeGenes.get(source), nodeGenes.get(source).potentials.get(sink), Math.random()*permutation, true, parentPopulation.innovation_nr);
            nodeGenes.get(source).potentials.remove(sink);
            genes.add(g);

        }

        if (Math.random() < P_addNode){

            int placement = (int)Math.random()*parentPopulation.innovation_nr++;
            genes.get(placement).expressed = false;
            List<ConnectionGene> conGen = new ListArray<ConnectionGene>(2);
            List<NodeGene> potentials = new ListArray<NodeGene>(nodeGenes.size-2);
            NodeGene source = genes.get(placement).in_node, sink = genes.get(placement).out_node;
            for (int i = 0; i < nodeGenes.size; i++){
                if (nodeGenes.get(i) != genes.get(placement).in_node && nodeGenes.get(i) != genes.get(placement).out_node){
                    potentials.set(i, nodeGenes.get(i));
                }
            }
            NodeGene new_node = new NodeGene(2, conGen, potentials);
            ConnectionGene g_in = new ConnectionGene(source, new_node, 1, true, parentPopulation.innovatoin_nr++);
            ConnectionGene g_out = new ConnectionGene(new_node, sink, genes.get(placement).weight, true, parentPopulation.innovation_nr);
            new_node.connections.set(0, g_in);
            new_node.connections.set(1, g_out);
            genes.add(g_in);
            genes.add(g_out);
        }
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
