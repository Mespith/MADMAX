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

    NodeGene(Integer id, Type type) {
        if (type != Type.None) {
            this.type = type;
        }

        if (type == Type.Input || type == Type.Output) {
            this.id = id;
        }
    }
}
