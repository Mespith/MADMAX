import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Math;
import java.util.List;

import org.ejml.data.MatrixIterator64F;
import org.ejml.simple.SimpleMatrix;

public class EchoStateNet {

    public SimpleMatrix inW;
    public SimpleMatrix resW;
    public SimpleMatrix outW;
    public SimpleMatrix x_prev; //activation OUTPUT of last iteration
    public double leaking_rate;

    public EchoStateNet(double[][] inW, double[][] resW, double[][] outW, double leaking_rate)
    {
        this.inW = new SimpleMatrix(inW);
        this.resW = new SimpleMatrix(resW);
        this.outW = new SimpleMatrix(outW);
        this.x_prev = new SimpleMatrix(resW.length, 1);
        this.leaking_rate = leaking_rate;
    }

    public EchoStateNet(Genome gene) { this(gene, 0.3); }

    public EchoStateNet(Genome gene, double leakAlpha)
    {
        // 1: find numbers of nodes
        List<ConnectionGene> connections = gene.getConnections(); // gene.getNodes() or so...
        int nNodes = gene.getNrNodes(); //gene.parent.getNumNodes()
        int inSize = gene.getParentPopulation().inNodes; //gene.parent.getNumInNodes()
        int outSize = gene.getParentPopulation().outNodes; //gene.parent.getNumOutNodes()
        int hidSize = nNodes - inSize - outSize;
        boolean fakehid = false; // just for debug

        // make weight arrays. adopt ordering from nodes list. this being [in]+[out]+[hid]


        if (hidSize == 0) // create placeholder only if hidden layer is empty
        {
            hidSize = 1;
            fakehid = true;
        }

        double[][] inW = new double[hidSize][inSize];
        double[][] hidW = new double[hidSize][hidSize];
        double[][] outW = new double[outSize][hidSize + inSize]; // first come the hidden then the in connections

        // 2: go through connectionslist, set each one if expressed
        for (ConnectionGene con: connections) {
            // get source
            int source = gene.nodes.indexOf(con.in_node);
            // get target
            int target = gene.nodes.indexOf(con.out_node);
            // set source and target
            if (target >= inSize && target < inSize + outSize) //nodes to out layer
            {
                if (source < inSize) // in nodes to out layer
                {
                    outW[target - inSize][source + hidSize] = con.weight;
                } else                // hid nodes to out layer
                {
                    try {
                        outW[target - inSize][source - (inSize + outSize)] = con.weight;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println(target);
                        System.out.println(source);
                    }
                }
            } else if (source < inSize) //nodes from in to hidden layer
            {
                try {
                    inW[target - (inSize + outSize)][source] = con.weight;
                }catch (ArrayIndexOutOfBoundsException e)
                {
                    System.out.println(gene.getNrNodes());
                    System.out.println(inSize);
                    System.out.println(hidSize);
                    System.out.println(outSize);
                    System.out.println(fakehid);
                    System.out.println(target);
                    e.printStackTrace();
                    System.exit(1);
                }
            } else //nodes in recurrent layer
            {
                try {
                    hidW[target - (inSize + outSize)][source - (inSize + outSize)] = con.weight;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(target);
                    System.out.println(source);
                }
            }
        }

        this.inW = new SimpleMatrix(inW);
        this.resW = new SimpleMatrix(hidW);
        this.outW = new SimpleMatrix(outW);
        this.x_prev = new SimpleMatrix(hidSize, 1);
        this.leaking_rate = leakAlpha;
    }

    // This is method takes in a matrix of the sensor values and returns the values of the actuators.
    public SimpleMatrix forward_propagation(SimpleMatrix U)
    {
        SimpleMatrix x_tilde = inW.mult(U).plus(resW.mult(x_prev));
        x_tilde = tanhFunc(x_tilde);
        x_prev = x_prev.scale(1.0 - leaking_rate).plus(x_tilde.scale(leaking_rate));

        SimpleMatrix X = U.combine(U.numRows(), 0, x_prev);

        return outW.mult(X);
    }

    // Tanh function applied per element of the given matrix.
    private SimpleMatrix tanhFunc(SimpleMatrix input)
    {
        MatrixIterator64F iterator = input.iterator(true, 0, 0, input.numRows() - 1, input.numCols() - 1);
        while (iterator.hasNext()) {
            iterator.set(Math.tanh(iterator.next()));
        }
        return input;
    }

    public static void storeESN(EchoStateNet esn, String path)
    {
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");

            // print alpha
            writer.println(esn.leaking_rate);

            //print inW
            for (int idx = 0; idx < esn.inW.numRows(); idx++) {
                String s = Double.toString(esn.inW.get(idx, 0));
                for (int jdx = 1; jdx < esn.inW.numCols(); jdx++) {
                    s = s + ";" + esn.inW.get(idx, jdx);
                }
                writer.println(s);
            }
            //print resW
            for (int idx = 0; idx < esn.resW.numRows(); idx++) {
                String s = Double.toString(esn.resW.get(idx, 0));
                for (int jdx = 1; jdx < esn.resW.numCols(); jdx++) {
                    s = s + ";" + esn.resW.get(idx, jdx);
                }
                writer.println(s);
            }
            //print outW
            for (int idx = 0; idx < esn.outW.numRows(); idx++) {
                String s = Double.toString(esn.outW.get(idx, 0));
                for (int jdx = 1; jdx < esn.outW.numCols(); jdx++) {
                    s = s + ";" + esn.outW.get(idx, jdx);
                }
                writer.println(s);
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}