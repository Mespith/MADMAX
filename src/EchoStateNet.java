import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Arrays;
import org.ejml.simple.SimpleMatrix;

public class EchoStateNet {

    SimpleMatrix inW;
    SimpleMatrix resW;
    SimpleMatrix outW;
    SimpleMatrix resState; //activation OUTPUT of last iteration
    double leakAlpha;

    public EchoStateNet(double[][] inW, double[][] resW, double[][] outW)
    {
        this.inW = new SimpleMatrix(inW);
        this.resW = new SimpleMatrix(resW);
        this.outW = new SimpleMatrix(outW);
        this.resState = new SimpleMatrix(resW.length, 1);
        this.leakAlpha = 0.5;
    }


    public EchoStateNet(String inFile)
    {
        String[] weightStrings = null;
        try {
            weightStrings = readFile(inFile);
        } catch(IOException ex)
        {
            ex.printStackTrace();
        }

        String[] firstLine = weightStrings[0].split(";");
        int inSize = Integer.valueOf(firstLine[0]);
        int resSize = Integer.valueOf(firstLine[1]);
        int outSize = Integer.valueOf(firstLine[2]);
        this.leakAlpha = Float.valueOf(firstLine[3]);

        double[][] inW = new double[inSize][resSize];
        double[][] resW = new double[resSize][resSize];
        double[][] outW = new double[resSize+inSize][outSize];

        // fill inW
        for (int idx = 0; idx < inSize; idx++) {
            int lindx = idx + 1;
            String[] line = weightStrings[lindx].split(";");
            for (int jdx = 0; jdx < resSize; jdx++) {
                inW[idx][jdx] = Float.valueOf(line[jdx]);
            }
        }

        // fill resW
        for (int idx = 0; idx < resSize; idx++) {
            int lindx = idx + 1 + inSize;
            String[] line = weightStrings[lindx].split(";");
            for (int jdx = 0; jdx < resSize; jdx++) {
                resW[idx][jdx] = Float.valueOf(line[jdx]);
            }
        }

        // fill outW
        for (int idx = 0; idx < resSize+inSize; idx++) {
            int lindx = idx + 1 + inSize + resSize;
            String[] line = weightStrings[lindx].split(";");
            for (int jdx = 0; jdx < outSize; jdx++) {
                outW[idx][jdx] = Float.valueOf(line[jdx]);
            }
        }
        this.inW = new SimpleMatrix(inW);
        this.resW = new SimpleMatrix(resW);
        this.outW = new SimpleMatrix(outW);
        this.resState = new SimpleMatrix(resSize, 1);
    }


    public SimpleMatrix doTimeStep(SimpleMatrix actIn)
    {

        SimpleMatrix actRes = inW.mult(actIn).plus(resW.mult(resState));

        resState = resActFunc(actRes);

        SimpleMatrix out = new SimpleMatrix(actIn.numRows() + resState.numRows(), 1);

        out.insertIntoThis(0, 0, actIn);
        out.insertIntoThis(actIn.numRows(), 0, resState);

        return outW.mult(out);
    }

    private String[] readFile(String inFile) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(inFile));
        String[] stringArray = null;
        try
        {
            String line = br.readLine();
            ArrayList<String> lineList = new ArrayList<String>();
            while (line != null) {

                lineList.add(line);
                line = br.readLine();
            }
            stringArray = lineList.toArray(new String[lineList.size()]);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        } finally
        {
            br.close();
        }

        return stringArray;
    }

    private  SimpleMatrix resActFunc(SimpleMatrix input)
    {
        SimpleMatrix tilde = tanhFunc(input);
        SimpleMatrix out = resState.scale(1.0 - leakAlpha).plus(tilde.scale(leakAlpha));
        return out;
    }

    private SimpleMatrix tanhFunc(SimpleMatrix input)
    {
        SimpleMatrix out = new SimpleMatrix(input.numRows(), 1);
        for (int idx = 0; idx < input.numRows(); idx++) {
            out.set(idx, 0, Math.tanh(input.get(idx)));
        }
        return out;
    }


    public static void main(String[] args)
    {
        EchoStateNet esn = new EchoStateNet("C:\\Users\\Frederik\\Desktop\\crap_weights.txt");



    }
}

