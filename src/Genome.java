import java.io.Serializable;
import java.util.*;

public class Genome implements Serializable{

    public int speciesHint;
    private transient Population parentPopulation;
    public double fitness;
    public List<List<Integer>> potentials;
    private List<ConnectionGene> genes;
    public List<Integer> nodes;


    Genome(Genome g) {
        this.speciesHint = g.speciesHint;
        this.parentPopulation = g.parentPopulation;
        this.fitness = g.fitness;
        this.genes = new ArrayList<>();
        for (ConnectionGene gene : g.genes) {
            this.genes.add(new ConnectionGene(gene));
        }
        this.nodes = new ArrayList<>();
        for (int node : g.nodes) {
            this.nodes.add(node);
        }
        this.potentials = new ArrayList<List<Integer>>();
        for (List<Integer> l : g.potentials) {
            List<Integer> k = new ArrayList<Integer>(l);
            this.potentials.add(k);
        }
    }

    Genome(Population parentPopulation) {
        this.parentPopulation = parentPopulation;
        this.speciesHint = -1;
        this.genes = new ArrayList<>(parentPopulation.inNodes * parentPopulation.outNodes);
        this.nodes = new ArrayList<>(parentPopulation.inNodes + parentPopulation.outNodes);
        this.potentials = new ArrayList<>(parentPopulation.inNodes);
        this.fitness = 0;
        // Create all the input and output nodes
        int innovationCounter = 0;
        for (int i = 0; i < parentPopulation.inNodes + parentPopulation.outNodes; i++) {
            this.nodes.add(i);
            if (i < parentPopulation.inNodes) {
                potentials.add(new ArrayList<>());
                for (int j = parentPopulation.inNodes; j < parentPopulation.inNodes + parentPopulation.outNodes; j++) {
                    this.genes.add( new ConnectionGene(i, j, innovationCounter++));
                }
            }
        }
    }

    //incomplete
    public void mutate() {

        // Mutate a weight.
        if (randomUniform() < parentPopulation.P_mutateWeights) {
            for (int i = 0; i < genes.size(); i++) {
                if (randomUniform() < parentPopulation.P_permuteWeight) {
                    genes.get(i).weight = genes.get(i).weight + parentPopulation.permutation*(2*Math.random() - 1);
                } else if (randomUniform() < parentPopulation.P_randomizeWeight)
                {
                    genes.get(i).weight = randomGaussian();
                }
            }
        }

        // Adding a weight.
        int potential_connections = (getNrNodes() - parentPopulation.outNodes) *
                                    (getNrNodes() - parentPopulation.inNodes) - getN();
        if (randomUniform() < parentPopulation.P_addWeight && potential_connections > 0) {

            int random_nr = (int) (randomUniform() * potential_connections);

            // Select a node that has potential nodes to connect to.
            // Select a random node from the potential connections of you source node.
            int source = 0, target, targetIdx;
            while (true) {
                random_nr -= potentials.get(source).size();
                if (random_nr < 0) {
                    targetIdx = random_nr + potentials.get(source).size();
                    target = potentials.get(source).get(targetIdx);
                    break;
                } else {
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
        if (randomUniform() < parentPopulation.P_addNode) {
            // Select a connection where you will put a new node in between.
            int placement = (int) (randomUniform() * genes.size());
            // The old connection will be disabled.
            genes.get(placement).weight = 0;
            int newNodeId = ++parentPopulation.nodeId;
            nodes.add(newNodeId);
            List<Integer> p = new ArrayList<Integer>();
            for (int i = parentPopulation.inNodes; i < nodes.size(); i++) {
                if (nodes.get(i) != genes.get(placement).out_node) {
                    p.add(nodes.get(i));
                }
            }
            potentials.add(p);
            for (int i = 0; i < nodes.size() - parentPopulation.outNodes; i++) {
                int idx = i < parentPopulation.inNodes ? i : i + parentPopulation.outNodes;
                if (nodes.get(idx) != genes.get(placement).in_node) {
                    potentials.get(i).add(newNodeId);
                }
            }

            // Create the new connections.
            ConnectionGene g_in = new ConnectionGene(genes.get(placement).in_node, newNodeId, 1, parentPopulation.innovation_nr++);
            ConnectionGene g_out = new ConnectionGene(newNodeId, genes.get(placement).out_node, genes.get(placement).weight, parentPopulation.innovation_nr++);
            // Add the new connections and nodes to the genome.
            genes.add(g_in);
            genes.add(g_out);
        }
    }

    public List<ConnectionGene> getConnections() {
        return genes;
    }

    public List<Integer> getNodes() {
        return nodes;
    }

    public Population getParentPopulation() {
        return parentPopulation;
    }

    public int getN(){ return genes.size(); }

    public int getNrNodes(){ return nodes.size(); }

    private double randomUniform(){ return parentPopulation.rng.nextDouble(); }

    private double randomGaussian(){ return parentPopulation.rng.nextGaussian(); }

    public void setParentPopulation(Population p){ this.parentPopulation = p; }
}