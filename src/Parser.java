import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class Parser {

    Parser() {}

    public EchoStateNet ParseForESN(String filePath){
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

        double[][] resW = ParseMatrixRowESN(reader, Integer.parseInt(dimensions[0]));
        double[][] inW = ParseMatrixRowESN(reader, Integer.parseInt(dimensions[1]));
        double[][] outW = ParseMatrixRowESN(reader, Integer.parseInt(dimensions[2]));
        double leakAlpha = Float.valueOf(dimensions[3]);
        return new EchoStateNet(inW, resW, outW, leakAlpha);
    }

    private double[][] ParseMatrixRowESN(BufferedReader reader, Integer length) {

        double[][] M = new double[length][];
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
            Vector<Double> realVals = new Vector<>();
            for (String val: values
                 ) {
                if (!val.isEmpty())
                {
                    realVals.add(Double.parseDouble(val));
                }
            }

            double[] m = new double[realVals.size()];
            for (int idx = 0; idx < realVals.size(); idx++) {
                m[idx] = realVals.elementAt(idx);
            }

            M[i] = m;
        }
        return M;
    }
}
