import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class Parser {

    Parser() {}

    public NeuralNetwork ParseNetworkFile(String filePath){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] dimensions = line.split(",");

        Vector<Vector<Double>> W = ParseMatrixRow(reader, Integer.parseInt(dimensions[0]));
        Vector<Vector<Double>> Win = ParseMatrixRow(reader, Integer.parseInt(dimensions[1]));
        Vector<Vector<Double>> Wout = ParseMatrixRow(reader, Integer.parseInt(dimensions[2]));

        return new NeuralNetwork(W, Win, Wout, Double.parseDouble(dimensions[3]));
    }

    private Vector<Vector<Double>> ParseMatrixRow(BufferedReader reader, Integer length) {
        Vector<Vector<Double>> M = new Vector<>();
        String line = "";
        for (int i = 0; i < length; i++) {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String data = line.replace("[", "");
            data = data.replace("]", "");
            String[] values = data.split(" ");
            Vector<Double> m = new Vector<>();
            for (int n = 0; n < values.length; n++) {
                String value = values[n];
                if (value.length() > 0) {
                    m.add(Double.parseDouble(value));
                }
            }
            M.add(m);
        }
        return M;
    }

    public TrainingData ParseTrainingFile(String dataPath) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(dataPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = null;
        List<Vector<Double>> X = new ArrayList<Vector<Double>>();
        List<Vector<Double>> Y = new ArrayList<Vector<Double>>();
        Vector<Double> x = new Vector<Double>();
        Vector<Double> y = new Vector<Double>();

        // Read the first line
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read the whole file line by line
        while (line != null) {
            String[] data = line.split(";");
            String[] input = data[0].split(",");
            String[] output = data[1].split(",");

            for (int i = 0; i < input.length; i++) {
                x.add(Double.valueOf(input[i]));
            }
            for (int j = 0; j < output.length; j++) {
                y.add(Double.valueOf(output[j]));
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

        return new TrainingData(X, Y);
    }
}
