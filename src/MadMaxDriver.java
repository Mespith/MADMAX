import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.genome.IGenome;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;
import org.ejml.simple.SimpleMatrix;

public class MadMaxDriver extends AbstractDriver {

    EchoStateNet esn;

    public MadMaxDriver() {
        initialize();
        Parser parser = new Parser();
        esn = parser.ParseForESN("best_driver.txt");
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
                { sensors.getTrackEdgeSensors()[9] }, { sensors.getTrackEdgeSensors()[12] }, { sensors.getTrackEdgeSensors()[18] }, { sensors.getLateralSpeed() }, { sensors.getTrackPosition() }};
        SimpleMatrix actInMat = new SimpleMatrix(actIn);
        SimpleMatrix actOut = esn.forward_propagation(actInMat);

        action.accelerate = actOut.get(0);
        action.steering = actOut.get(1);
        action.brake = actOut.get(2);
    }

    public String getDriverName() {
        return "Mad Max";
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