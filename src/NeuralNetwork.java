import org.ejml.data.MatrixIterator64F;
import org.ejml.simple.SimpleMatrix;

import java.io.*;
import java.util.ArrayList;
import java.util.Vector;

public class NeuralNetwork implements Serializable {

	private SimpleMatrix W;
	private SimpleMatrix Win;
	private SimpleMatrix Wout;
	private SimpleMatrix x_prev;
	private Double leaking_rate;

	NeuralNetwork(SimpleMatrix W, SimpleMatrix Win, SimpleMatrix Wout, Double leaking_rate){
		this.W = W;
		this.Win = Win;
		this.Wout = Wout;
		this.leaking_rate = leaking_rate;

		this.x_prev = new SimpleMatrix(this.W.numRows(), 1);
		this.x_prev.zero();
	}

	NeuralNetwork(String fileName) {
		String[] weightStrings = null;
		try {
			weightStrings = readFile(fileName);
		} catch(IOException ex)
		{
			ex.printStackTrace();
		}
		Integer fileIndex = 0;
		String[] firstLine = weightStrings[fileIndex].split(";");
		fileIndex++;
		int inSize = Integer.valueOf(firstLine[0]);
		int resSize = Integer.valueOf(firstLine[1]);
		int outSize = Integer.valueOf(firstLine[2]);
		this.leaking_rate = Double.valueOf(firstLine[3]);

		SimpleMatrix inW = new SimpleMatrix(resSize, inSize + 1);
		SimpleMatrix resW = new SimpleMatrix(resSize, resSize);
		SimpleMatrix outW = new SimpleMatrix(outSize, resSize + inSize + 1);

		// fill inW
		for (int row = 0; row < resSize; row++) {
			String[] line = weightStrings[fileIndex].split(";");
			fileIndex++;
			for (int col = 0; col < resSize; col++) {
				inW.set(row, col, Double.valueOf(line[col]));
			}
		}

		// fill resW
		for (int row = 0; row < resSize; row++) {
			String[] line = weightStrings[fileIndex].split(";");
			fileIndex++;
			for (int col = 0; col < resSize; col++) {
				resW.set(row, col, Double.valueOf(line[col]));
			}
		}

		// fill outW
		for (int row = 0; row < resSize+inSize; row++) {
			String[] line = weightStrings[fileIndex].split(";");
			fileIndex++;
			for (int col = 0; col < outSize; col++) {
				outW.set(row, col, Double.valueOf(line[col]));
			}
		}
		this.Win = inW;
		this.W = resW;
		this.Wout = outW;
		this.x_prev = new SimpleMatrix(this.W.numRows(), 1);
		this.x_prev.zero();
	}

	public SimpleMatrix ForwardPropagation(SimpleMatrix U) {
		// The concatenation [1;u]
		SimpleMatrix z = new SimpleMatrix(1, 1);
		z.set(0, 1.);
		z = z.combine(1, 0, U);

		// The "update" function of the x value
		SimpleMatrix x_tilde = this.Win.mult(z).plus(this.W.mult(this.x_prev));
		MatrixIterator64F iterator = x_tilde.iterator(true, 0, 0, x_tilde.numRows(), x_tilde.numCols());
		while (iterator.hasNext()) {
			iterator.set(Math.tanh(iterator.next()));
		}

		// The actual x value
		SimpleMatrix x = this.x_prev.scale((1 - this.leaking_rate)).plus(x_tilde.scale(this.leaking_rate));

		// Concatenation [1;u;x]
		SimpleMatrix X = z.combine(z.numRows(), 0, x);

		return this.Wout.mult(X);
	}

	private static final long serialVersionUID = -88L;

	//Store the state of this neural network
	public void storeGenome() {
		ObjectOutputStream out = null;
		try {
			//create the memory folder manually
			out = new ObjectOutputStream(new FileOutputStream("memory/madmax.mem"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Load a neural network from memory
	 public NeuralNetwork loadGenome(Parser parser) {

		 // Read from disk using FileInputStream
		 FileInputStream f_in = null;
		 try {
			 f_in = new FileInputStream("memory/madmax.mem");
		 } catch (FileNotFoundException e) {
			 e.printStackTrace();
		 }

		 // Read object using ObjectInputStream
		 ObjectInputStream obj_in = null;
		 try {
			 obj_in = new ObjectInputStream(f_in);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }

		 // Read an object
		 try {
			 return (NeuralNetwork) obj_in.readObject();
		 } catch (IOException e) {
			 e.printStackTrace();
		 } catch (ClassNotFoundException e) {
			 e.printStackTrace();
		 }

		 return null;
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
}
