import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jaimy on 20/11/2015.
 */
public class TrainingData {

    public List<Vector<Double>> X;
    public List<Vector<Double>> Y;

    TrainingData(List<Vector<Double>> X, List<Vector<Double>> Y){

        this.X = new ArrayList<Vector<Double>>();
        this.Y = new ArrayList<Vector<Double>>();
    }
}
