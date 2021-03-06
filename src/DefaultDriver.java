import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.genome.IGenome;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;
import org.ejml.simple.SimpleMatrix;

import java.io.*;

public class DefaultDriver extends AbstractDriver {

    EchoStateNet esn;

    DefaultDriver() {
        initialize();
        Parser parser = new Parser();
        esn = parser.ParseForESN("OutputWeights.txt");
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

        double[][] sensorArray = {{1.0}, {sensors.getSpeed()}, {sensors.getAngleToTrackAxis()}};
        SimpleMatrix sensorMat = new SimpleMatrix(sensorArray);
        SimpleMatrix actuatorMat = esn.forward_propagation(sensorMat);

        action.accelerate = actuatorMat.get(0, 0);
        action.steering = actuatorMat.get(1, 0);
        action.brake = actuatorMat.get(2, 0);
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