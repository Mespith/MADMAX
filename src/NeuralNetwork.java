import java.io.*;
import java.util.Vector;

public class NeuralNetwork implements Serializable {

	private Vector<Vector<Double>> W;
	private Vector<Vector<Double>> Win;
	private Vector<Vector<Double>> Wout;
	private Vector<Double> x_prev;
	private Double leaking_rate;

	NeuralNetwork(Vector<Vector<Double>> W, Vector<Vector<Double>> Win, Vector<Vector<Double>> Wout, Double leaking_rate){
		this.W = W;
		this.Win = Win;
		this.Wout = Wout;
		this.leaking_rate = leaking_rate;

		this.x_prev = new Vector<>(this.W.size());
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

		 return parser.ParseNetworkFile("OutputWeights.txt");
	 }
	}
