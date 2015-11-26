import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Arrays;

public class EchoStateNet {

    double[][] inW;
    double[][] resW;
    double[][] outW;
    double[] resState; //activation OUTPUT of last iteration
    double leakAlpha;

    public EchoStateNet(double[][] inW, double[][] resW, double[][] outW)
    {
        this.inW = inW;
        this.resW = resW;
        this.outW = outW;
        this.resState = new double[resW.length];
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
        this.inW = inW;
        this.resW = resW;
        this.outW = outW;
        this.resState = new double[resSize];
    }


    public double[] doTimeStep(double[] actIn)
    {
        double[] actRes = new double[resState.length];

        double[] outIn = actIn; // activation from inputs TODO delete outIN...

        for (int idx = 0; idx < outIn.length; idx++)
        {
            for (int jdx = 0; jdx < actRes.length; jdx++)
            {
                actRes[jdx] += outIn[idx] * inW[idx][jdx];

            }
        } //actRes now input to res from in-layer

        for (int idx = 0; idx < actRes.length; idx++)
        {
            for (int jdx = 0; jdx < actRes.length; jdx++) {
                actRes[jdx] += resState[idx] * resW[idx][jdx];
            }
        } //actRes now in to res from in-layer and reservoir

        resState = resActFunc(actRes);

        double[] out = new double[outIn.length + resState.length];
        for (int idx = 0; idx < outIn.length; idx++)
        {
            out[idx] = outIn[idx];
        }
        for (int idx = 0; idx < resState.length; idx++)
        {
            out[idx + outIn.length] = resState[idx];
        }

        double[] y = new double[outW.length] ;
        for (int idx = 0; idx < outW.length; idx++) {
            for (int jdx = 0; jdx < outW[0].length; jdx++) {
                y[idx] += outW[idx][jdx] * out[jdx];
            }
        }
        return y;
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

    private  double[] resActFunc(double[] input)
    {
        double[] tilde = tanhFunc(input);
        double[] out = new double[resState.length];
        for (int idx = 0; idx < resState.length; idx++) {
            out[idx] = (1.0 - leakAlpha) * resState[idx] + leakAlpha * tilde[idx];
        }
        return out;
    }

    private double[] tanhFunc(double[] input)
    {
        double[] out = new double[input.length];
        for (int idx = 0; idx < input.length; idx++) {
            out[idx] = Math.tanh(input[idx]);
        }
        return out;
    }


    public static void main(String[] args)
    {
        EchoStateNet esn = new EchoStateNet("C:\\Users\\Frederik\\Desktop\\testRes.txt");
        double[] inArr = {1.0, 2};
        double[] o1 = esn.doTimeStep(inArr);
        double[] o2 = esn.doTimeStep(inArr);
        System.out.println(Arrays.toString(o1));
        System.out.println(Arrays.toString(o2));
    }
}

