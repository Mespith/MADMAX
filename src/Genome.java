import org.ejml.simple.SimpleMatrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class Genome{
    
    private Population parentPopulation;
    public double fitness;
    public int N;
    public Integer nr_of_nodes;
    private List<ConnectionGene> genes;
    private List<NodeGene> nodeGenes;

    Genome(Genome g){
        this.parentPopulation = g.parentPopulation;
        this.fitness = g.fitness;
        this.N = g.N;
        this.nr_of_nodes = g.nr_of_nodes;
        this.genes = new ArrayList<>();
        this.genes.addAll(g.genes);
        this.nodeGenes = new ArrayList<>();
        this.nodeGenes.addAll(g.nodeGenes);
    }
    Genome(List<ConnectionGene> genes, List<NodeGene> nodeGenes, Population parentPopulation){
        this.parentPopulation = parentPopulation;
        this.genes = genes; //genes called by reference?
        this.nodeGenes = nodeGenes;
        this.fitness = 0;
        this.N = genes.size();
    }

    Genome(Population parentPopulation, int in_nodes, int out_nodes){
        this.parentPopulation = parentPopulation;
        this.genes = new ArrayList<>();
        this.nodeGenes = new ArrayList<>();
        this.fitness = 0;

        // Create all the input and output nodes
        for (int i = 0; i < in_nodes + out_nodes; i++) {
            if (i < in_nodes) {
                this.nodeGenes.add(new NodeGene(NodeGene.Type.Input));
            }
            else {
                this.nodeGenes.add(new NodeGene(NodeGene.Type.Output));
            }
        }
        // Create all the connections between the input and output nodes.
        for (int in = 0; in < in_nodes; in++) {
            for (int out = in_nodes; out < out_nodes; out++) {
                ConnectionGene connection = new ConnectionGene(nodeGenes.get(in), nodeGenes.get(out), parentPopulation.Innovation_nr++);
                genes.add(connection);
                nodeGenes.get(in).connections.add(connection);
            }
        }
        this.N = genes.size();
    }

    //incomplete
    public void mutate(double P_addNode, double P_addWeight, double P_mutateWeights,
                       double P_permuteWeight, double permutation){
        // Mutate a weight.
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

        // Adding a weight.
        if (Math.random() < P_addWeight) {
            parentPopulation.Innovation_nr++;
            int source;

            // Select a node that has potential nodes to connect to.
            do {
                source = ((int) (Math.random() * (nodeGenes.size() - parentPopulation.outNodes) + parentPopulation.inNodes + parentPopulation.outNodes) % nodeGenes.size());
            } while (nodeGenes.get(source).potentials.isEmpty());
            // Select a random node from the potential connections of you source node.
            int sink = (int) (Math.random() * nodeGenes.get(source).potentials.size());
            // Create the new connection
            ConnectionGene g = new ConnectionGene(nodeGenes.get(source), nodeGenes.get(source).potentials.get(sink), parentPopulation.Innovation_nr);

            // Remove the newly connected node from the potential nodes.
            nodeGenes.get(source).potentials.remove(sink);
            // Add the connection to the connections of the source node.
            nodeGenes.get(source).connections.add(g);
            // Add the connection to the genes.
            genes.add(g);
        }

        // Adding a node.
        if (Math.random() < P_addNode){
            // Select a connection where you will put a new node in between.
            int placement = (int)(Math.random() * parentPopulation.Innovation_nr++);
            // The old connection will be disabled.
            genes.get(placement).expressed = false;

            // Create the connection list for the new node.
            List<ConnectionGene> connections = new ArrayList<>(1);
            List<NodeGene> potentials = new ArrayList<>(nodeGenes.size());
            for (int i = 0; i < nodeGenes.size(); i++) {
                potentials.set(i, nodeGenes.get(i));
            }

            // The source node is the in_node of the original connection and the sink is the output_node.
            NodeGene source = genes.get(placement).in_node, sink = genes.get(placement).out_node;
            // Remove the out_node because the new node will be connected to it.
            potentials.remove(sink);

            // Create the new node.
            NodeGene new_node = new NodeGene(NodeGene.Type.Hidden, connections, potentials);
            // Create the new connections.
            ConnectionGene g_in = new ConnectionGene(source, new_node, parentPopulation.Innovation_nr++);
            ConnectionGene g_out = new ConnectionGene(new_node, sink, genes.get(placement).weight, parentPopulation.Innovation_nr);
            // Add the new connection to the source node and the new node.
            source.connections.add(g_in);
            new_node.connections.set(0, g_out);
            // Add the new connections and nodes to the genome.
            genes.add(g_in);
            genes.add(g_out);
            nodeGenes.add(new_node);
        }
    }
    
    public double getFitness(){return fitness;}

    public List<ConnectionGene> getConnections() { return genes; }

    public List<NodeGene> getNodes() { return nodeGenes; }

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
