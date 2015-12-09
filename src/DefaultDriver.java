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
    public RaceTracker tracker;

    DefaultDriver() {
        initialize();
    }

    DefaultDriver(EchoStateNet esn) {
        this.esn = esn;
        this.tracker = new RaceTracker();
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

        double[][] actIn = {{ sensors.getSpeed() }, { sensors.getAngleToTrackAxis() }, { sensors.getTrackEdgeSensors()[0] }, { sensors.getTrackEdgeSensors()[6] },
                { sensors.getTrackEdgeSensors()[9] }, { sensors.getTrackEdgeSensors()[12] }, { sensors.getTrackEdgeSensors()[18] }, { sensors.getOpponentSensors()[0] },
                { sensors.getOpponentSensors()[6] }, { sensors.getOpponentSensors()[12] }, { sensors.getOpponentSensors()[18] }, { sensors.getOpponentSensors()[24] },
                { sensors.getOpponentSensors()[30] }, { sensors.getLateralSpeed() }, { sensors.getTrackPosition() }};
        SimpleMatrix actInMat = new SimpleMatrix(actIn);
        SimpleMatrix actOut = esn.forward_propagation(actInMat);

        action.accelerate = actOut.get(0);
        action.steering = actOut.get(1);
        action.brake = actOut.get(2);

        tracker.doTimestep(sensors, action); // collects data on race. may set action.abandonRace = True
                                        // if temporary fitness is too bad or time-limit is reached
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