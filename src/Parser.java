import org.ejml.simple.SimpleMatrix;

import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.io.*;
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

    /*public Population resurrectPopulation(List<String> matrixFiles, double c1, double c2, double c3, int inNodes, int outNodes, double kill_rate, double mutation_rate, double P_addNode, double P_addWeight, double P_mutateWeights,
                                          double P_changeWeight, double permutation, double compatibility_threshold, int pop_size, Random rng) {

        //Create new population according to new set of parameters.
        Population resurrectedPopulation = new Population(c1, c2, c3, inNodes, outNodes, kill_rate, mutation_rate, P_addNode, P_addWeight, P_mutateWeights,
                                                            P_changeWeight, permutation, compatibility_threshold, pop_size, rng);

        //Add the different echo state network matrices to a list, each element corresponding to a genome in the population
        List<EchoStateNet> genomeMatrices = new ArrayList<>();
        for (String fileName : matrixFiles) {
            //Read the genome echo state network from file
            EchoStateNet matrices = ParseForESN(fileName);
            //Create a new Genome
            Genome currentGenome = new Genome(resurrectedPopulation);
            resurrectedPopulation.getGeneration().add(currentGenome);
            //Go through all the old input nodes
            for (int i = 0; i < matrices.inW.numCols(); i++){
                //update their weights to the output nodes
                for (int j = resurrectedPopulation.inNodes; j < resurrectedPopulation.inNodes + matrices.outW.numRows(); j++){
                    currentGenome.getConnections().get(i).weight = matrices.inW.get(j, i);
                }
                //add connection genes from the input nodes to the hidden nodes
                for (int j = resurrectedPopulation.inNodes + resurrectedPopulation.outNodes; j < resurrectedPopulation.inNodes + resurrectedPopulation.outNodes + matrices.resW.numRows(); j++){
                    currentGenome.getNodes().add(++currentGenome.nr_of_nodes);
                    currentGenome.getConnections().add(new ConnectionGene(i, j, matrices.resW.get(j, i), resurrectedPopulation.incrementInnovationNr()));
                }
            }
            resurrectedPopulation.nodeId = currentGenome.nr_of_nodes > resurrectedPopulation.nodeId ? currentGenome.nr_of_nodes : resurrectedPopulation.nodeId;
        }

        //Go through all the input nodes
        for (int i = 0; i < resurrectedPopulation.inNodes; i++){

        }


        return resurrectedPopulation;
    }
*/


}
