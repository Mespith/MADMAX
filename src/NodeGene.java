/**
 * Created by Jaimy on 26/11/2015.
 */
public class NodeGene {

    public enum Type {
        None,
        Input,
        Hidden,
        Output
    }
    
    public Type type = Type.Hidden;
    
    public List<ConnectionGene> connections;
    public List<NodeGene> potentials;
    
    NodeGene(NodeGene g){
        this.type = g.type;
        
        potentials = new ArrayList<NodeGene>(g.potentials.size);
        potentials.addAll(0, g.potentials);
        
        connections = new ListArray<ConnectionGene>(g.connections.size);
        connections.addAll(0, g.connections);
    }

    NodeGene(Type type, List<ConnectionGene> connections, List<NodeGene> potentials) {
        if (type != Type.None) {
            this.type = type;
        }
        
        this.connections = connections;
        this.potentials = potentials;
    }
}
