import java.io.File;
import cicontest.algorithm.abstracts.AbstractAlgorithm;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.client.Controller;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResults;
import race.TorcsConfiguration;

public class DefaultDriverAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 654963126362653L;
    private static Parser parser;

    public Class<? extends Driver> getDriverClass(){
        return DefaultDriver.class;
    }

    public void run(boolean continue_from_checkpoint) { }

    public void trainNeuralNetwork(){
        //NeuralNetwork neuralNetwork = new NeuralNetwork();
        parser = new Parser();
        NeuralNetwork network = parser.ParseNetworkFile("OutputWeights.txt");
        //neuralNetwork.train(data);
        //neuralNetwork.storeGenome();
    }

    public void run() {
        trainNeuralNetwork();
        Race race = new Race();
        race.setTrack("road", "aalborg");
        race.setTermination(Race.Termination.LAPS, 1);
        race.setStage(Controller.Stage.RACE);
        race.addCompetitor(new DefaultDriver());
        Boolean withGUI = true;
        RaceResults results;
        if(withGUI) {
            results = race.runWithGUI();
        } else {
            results = race.run();
        }
    }

    public static void main(String[] args) {
        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));
		/*
		 *
		 * Start without arguments to run the algorithm
		 * Start with -continue to continue a previous run
		 * Start with -show to show the best found
		 * Start with -show-race to show a race with 10 copies of the best found
		 * Start with -human to race against the best found
		 *
		 */

        // add you argument and function to train your neural network; you can edit this class as you wish
        DefaultDriverAlgorithm algorithm = new DefaultDriverAlgorithm();
        DriversUtils.registerMemory(algorithm.getDriverClass());
        if(args.length > 0 && args[0].equals("-show")){
            new DefaultRace().showBest();
        } else if(args.length > 0 && args[0].equals("-show-race")){
            new DefaultRace().showBestRace();
        } else if(args.length > 0 && args[0].equals("-human")){
            new DefaultRace().raceBest();
       // } else if(args.length > 0 && args[0].equals("-generateData")){
            //generateData();
      //  } else if(args.length > 0 && args[0].equals("-train")){
            //trainNeuralNetwork();
        } else if(args.length > 0 && args[0].equals("-continue")){
            if(DriversUtils.hasCheckpoint()){
                DriversUtils.loadCheckpoint().run(true);
            } else {
                algorithm.run();
            }
        } else {
            algorithm.run();
        }
    }
}