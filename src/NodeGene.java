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

    public String name;
    public Type type = Type.Hidden;

    NodeGene(String name, Type type) {
        this.name = name;
        if (type != Type.None) {
            this.type = type;
        }
    }
}
