import org.ejml.simple.SimpleMatrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Genome{

    private Population parentPopulation;
    public double fitness;
    public int nr_of_nodes;
    public List<List<Integer>> potentials;
    private List<ConnectionGene> genes;
    private List<Integer> nodes;

    Genome(Genome g){
        this.parentPopulation = g.parentPopulation;
        this.fitness = g.fitness;
        this.nr_of_nodes = g.nr_of_nodes;
        this.genes = new ArrayList<ConnectionGene>();
        for (ConnectionGene gene : g.genes)
        {
            this.genes.add(new ConnectionGene(gene));
        }
        this.nodes = new ArrayList<>();
        for (int node : g.nodes)
        {
            this.nodes.add(node);
        }
        this.potentials = new ArrayList<List<Integer>>();
        for (List<Integer> l : g.potentials){
            List<Integer> k = new ArrayList<Integer>(l);
            this.potentials.add(k);
        }
    }

    Genome(List<ConnectionGene> genes, List<Integer> nodeGenes, List<List<Integer>> potentials, Population parentPopulation){
        this.parentPopulation = parentPopulation;
        this.genes = genes;
        this.nodes = nodeGenes;
        this.potentials = potentials;
        this.fitness = 0;
    }

    Genome(Population parentPopulation, int in_nodes, int out_nodes){

        this.parentPopulation = parentPopulation;
        this.genes = new ArrayList<ConnectionGene>(in_nodes*out_nodes);
        this.nodes = new ArrayList<Integer>(in_nodes + out_nodes);
        this.potentials = new ArrayList<List<Integer>>(in_nodes);
        this.fitness = 0;

        // Create all the input and output nodes
        for (int i = 0; i < in_nodes + out_nodes; i++) {
            this.nodes.set(i, i);
            if (i < in_nodes){
                potentials.set(i, new ArrayList<Integer>());
                for (int j = in_nodes; j < out_nodes; j++) {
                    this.genes.set(parentPopulation.innovation_nr, new ConnectionGene(i, j, parentPopulation.innovation_nr));
                    parentPopulation.innovation_nr++;
                }
            }
        }

    }

    //incomplete
    public void mutate(double P_addNode, double P_addWeight, double P_mutateWeights,
                       double P_permuteWeight, double permutation){

        // Mutate a weight.
        if (Math.random() < P_mutateWeights) {
            for (int i = 0; i < genes.size(); i++) {
                if (Math.random() < P_permuteWeight) {
                    genes.get(i).weight = permutation*genes.get(i).weight;
                }
                else{
                    genes.get(i).weight = Math.random().nextGaussian();
                }
            }
        }

        // Adding a weight.
        if (Math.random() < P_addWeight) {

            int random_nr = (int)Math.random()*((nr_of_nodes - parentPopulation.outNodes)*(nr_of_nodes-parentPopulation.inNodes) - genes.size());

            // Select a node that has potential nodes to connect to.
            // Select a random node from the potential connections of you source node.
            int source = 0, target = 0, targetIdx = 0;
            while (random_nr > 0){
                random_nr -= potentials.get(source).size();
                if (random_nr <= 0){
                    targetIdx = random_nr+potentials.get(source).size();
                    target = potentials.get(source).get(targetIdx);
                }
                else {
                    source++;
                }
            }

            // Create the new connection
            ConnectionGene g = new ConnectionGene(source, target, parentPopulation.innovation_nr++);

            // Remove the newly connected node from the potential nodes.
            potentials.get(source).remove(targetIdx);
            // Add the connection to the genes.
            genes.add(g);
        }

        // Adding a node.
        if (Math.random() < P_addNode){
            // Select a connection where you will put a new node in between.
            int placement = (int)(Math.random() * genes.size());
            // The old connection will be disabled.
            genes.get(placement).weight = 0;
            nr_of_nodes++;
            int newNodeId = parentPopulation.nodeID++;
            nodes.add(newNodeId);
            List<Integer> p = new ArrayList<Integer>();
            for (int i = parentPopulation.inNodes; i < nodes.size(); i++){
                if (nodes.get(i) != genes.get(placement).out_node){
                    p.add(nodes.get(i));
                }
            }
            for (int i = 0; i < nodes.size() - parentPopulation.outNodes; i++){
                int idx = i < parentPopulation.inNodes ? i : i + parentPopulation.outNodes ;
                if(nodes.get(idx) != genes.get(placement).in_node){
                    potentials.get(idx).add(newNodeId);
                }
            }
            potentials.add(p);

            // Create the new connections.
            ConnectionGene g_in = new ConnectionGene(genes.get(placement).in_node, newNodeId, 1, parentPopulation.innovation_nr++);
            ConnectionGene g_out = new ConnectionGene(newNodeId, genes.get(placement).out_node, genes.get(placement).weight, parentPopulation.innovation_nr++);
            // Add the new connections and nodes to the genome.
            genes.add(g_in);
            genes.add(g_out);
        }
    }

    public List<ConnectionGene> getConnections() { return genes; }

    public List<Integer> getNodes() { return nodes; }
