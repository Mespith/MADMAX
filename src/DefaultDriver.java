import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.genome.IGenome;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;

import java.io.*;
import java.util.Arrays;

public class DefaultDriver extends AbstractDriver {

    //NeuralNetwork neuralNetwork;
    EchoStateNet esn;
    File f;
    OutputStream stream;
    BufferedWriter writer;

    DefaultDriver() {
        initialize();
        //neuralNetwork = neuralNetwork.loadGenome(parser);
        //neuralNetwork = neuralNetwork.loadGenome();
        Parser parser = new Parser();
        esn = parser.ParseForESN("OutputWeights.txt");
    }

    public void loadGenome(IGenome genome) { }

    public void initialize(){
       this.enableExtras(new AutomatedClutch());
       this.enableExtras(new AutomatedGearbox());
       this.enableExtras(new AutomatedRecovering());
       this.enableExtras(new ABS());

        f = new File("train_data.txt");
        try {
            stream = new FileOutputStream(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = new BufferedWriter(new OutputStreamWriter(stream));
    }

    @Override
    public void control(Action action, SensorModel sensors) {

        double[] actIn = {1.0, sensors.getSpeed(), sensors.getAngleToTrackAxis()};

        double[] actOut = esn.doTimeStep(actIn);

        action.accelerate = actOut[0];
        action.steering = actOut[1];
        action.brake = actOut[2];

        System.out.println(Arrays.toString(actOut));

        // Example of a bot that drives pretty well; you can use this to generate data
//        action.steering = DriversUtils.alignToTrackAxis(sensors, 0.5);
//        if(sensors.getSpeed() > 60.0D) {
//            action.accelerate = 0.0D;
//            action.brake = 0.0D;
//        }
//
//        if(sensors.getSpeed() > 70.0D) {
//            action.accelerate = 0.0D;
//            action.brake = -1.0D;
//        }
//
//        if(sensors.getSpeed() <= 60.0D) {
//            action.accelerate = (80.0D - sensors.getSpeed()) / 80.0D;
//            action.brake = 0.0D;
//        }
//
//        if(sensors.getSpeed() < 30.0D) {
//            action.accelerate = 1.0D;
//            action.brake = 0.0D;
//        }
//        System.out.println(action.steering +"steering");
//        System.out.println(action.accelerate + "acceleration");
//        System.out.println(action.brake + "brake");
//
//        String data = sensors.getSpeed() + "," + sensors.getAngleToTrackAxis() + ";" + action.accelerate + "," + action.steering + "," + action.brake;
//        byte[] dataBytes = data.getBytes();
//        for (int i = 0; i < data.length(); i++) {
//            try {
//                writer.write(dataBytes[i]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            writer.newLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public String getDriverName() {
        return "simple example 2";
    }

    public void controlQualification(Action action, SensorModel sensors) { }

    public void defaultControl(Action action, SensorModel sensors){}

    @Override
    public double getSteering(SensorModel sensorModel) {
        return 0;
    }

    @Override
    public double getAcceleration(SensorModel sensorModel) {
        return 0;
    }

    public double getBraking(SensorModel sensorModel){
        return 0;
    }
}