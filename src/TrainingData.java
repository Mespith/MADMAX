import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jaimy on 20/11/2015.
 */
public class TrainingData {

    public List<Vector<Float>> X;
    public List<Vector<Float>> Y;

    private BufferedReader reader;

    TrainingData(String dataPath){
        try {
            this.reader = new BufferedReader(new FileReader(dataPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        X = new ArrayList<Vector<Float>>();
        Y = new ArrayList<Vector<Float>>();

        ParseFile();
    }

    private void ParseFile() {
        String line = null;
        Vector<Float> x = new Vector<Float>();
        Vector<Float> y = new Vector<Float>();

        // Read the first line
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read the whole file line by line
        while(line != null) {
            String[] data = line.split(";");
            String[] input = data[0].split(",");
            String[] output = data[1].split(",");

            for (int i = 0; i < input.length; i++) {
                x.add(Float.valueOf(input[i]));
            }
            for (int j = 0; j < output.length; j++) {
                y.add(Float.valueOf(output[j]));
            }

            X.add(x);
            Y.add(y);
            x.clear();
            y.clear();

            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
