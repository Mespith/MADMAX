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
    
    public Integer id = 0;
    public Type type = Type.Hidden;
    
    public ConnectionGene[] connections;
    
    NodeGene(NodeGene g){
        this.type = g.type;
        this.id = g.id;
        ConnectionGene[] = new ConnectionGene[g.connections.size];
        for (int i = 0; i < g.connections.size; i++){
            connections[i] = g.connections[i];
        }
    }

    NodeGene(Integer id, Type type, ConnectionGene[] connections) {
        if (type != Type.None) {
            this.type = type;
        }

        if (type == Type.Input || type == Type.Output) {
            this.id = id;
        }
        
        this.connections = connections;
    }
}