import org.ejml.simple.SimpleMatrix;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jaimy on 26/11/2015.
 */
public class Parser {

    Parser() {}

    public void WriteForESN(EchoStateNet esn) {
        File f;
        OutputStream stream = null;
        BufferedWriter writer;
        String line = "";

        f = new File("train_data.txt");
        try {
            stream = new FileOutputStream(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = new BufferedWriter(new OutputStreamWriter(stream));

        // Write the line with the column dimensions.
        try {
            line = esn.resW.numCols() + "," + esn.inW.numCols() + "," + esn.outW.numCols() + "," + esn.leaking_rate ;
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the matrices down.
        WriteMatrixESN(esn.resW, writer);
        WriteMatrixESN(esn.inW, writer);
        WriteMatrixESN(esn.outW, writer);
    }

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

    private void WriteMatrixESN(SimpleMatrix M, BufferedWriter writer) {
        String line = "";
        // Write the rows of the resW matrix
        for (int row = 0; row < M.numRows(); row++) {
            try {
                line = M.extractVector(true, row).toString();
                writer.write(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
