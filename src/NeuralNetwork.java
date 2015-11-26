import java.io.*;
import java.util.Vector;

public class NeuralNetwork implements Serializable {

	NeuralNetwork(){ }

	private static final long serialVersionUID = -88L;

    // Train the Neural Network
    public void train(TrainingData data) {
		// This is the size of the training set.
        int size = data.X.size();

        for (int i = 0; i < size; i++) {
            Vector<Double> x = data.X.get(i);
            Vector<Double> y = data.Y.get(i);
            // Do something with the input and output vectors.
        }
    }

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
	 public NeuralNetwork loadGenome() {

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
	}
