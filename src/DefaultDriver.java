import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.genome.IGenome;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;

import java.io.*;

public class DefaultDriver extends AbstractDriver {

    private EchoStateNet esn;
    public int position;

    DefaultDriver() {
        initialize();
    }

    DefaultDriver(EchoStateNet esn) {
        this.esn = esn;
    }

    public void loadGenome(IGenome genome) { }

    public void initialize(){
       this.enableExtras(new AutomatedClutch());
       this.enableExtras(new AutomatedGearbox());
       this.enableExtras(new AutomatedRecovering());
       this.enableExtras(new ABS());
    }

    @Override
    public void control(Action action, SensorModel sensors) {
        position = sensors.getRacePosition();
        double[] actIn = {sensors.getSpeed(), sensors.getAngleToTrackAxis()};

        double[] actOut = esn.forward_propagation(actIn);

        action.accelerate = actOut[0];
        action.steering = actOut[1];
        action.brake = actOut[2];

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