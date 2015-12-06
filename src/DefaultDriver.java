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
        double[][] actIn = {{sensors.getSpeed(), sensors.getAngleToTrackAxis()}};
        SimpleMatrix actInMat = new SimpleMatrix(actIn);
        SimpleMatrix actOut = esn.forward_propagation(actInMat);

        action.accelerate = actOut.get(0);
        action.steering = actOut.get(1);
        action.brake = actOut.get(2);

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