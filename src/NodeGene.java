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
    
    public ConnectionGene[] connections;
    public List<NodeGene> potentials;
    
    NodeGene(NodeGene g){
        this.type = g.type;
        
        potentials = new ArrayList<NodeGene>(g.potentials.size);
        potentials.addAll(0, g.potentials);
        
        ConnectionGene[] = new ConnectionGene[g.connections.size];
        for (int i = 0; i < g.connections.size; i++){
            connections[i] = g.connections[i];
        }
    }

    NodeGene(Type type, ConnectionGene[] connections, List<NodeGene> potentials) {
        if (type != Type.None) {
            this.type = type;
        }
        
        this.connections = connections;
        this.potentials = potentials;
    }
}
