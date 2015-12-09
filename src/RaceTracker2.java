import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;

import java.util.LinkedList;

/**
 * Created by Frederik on 08.12.2015.
 */
public class RaceTracker2 extends RaceTracker {


    static int MEM_SIZE = 2;
    double lapDist;
    int lapCount;
    double offset;
    int MAX_LAPS = 2;
    boolean halfWayDone;
    double halfWayDist;

    public RaceTracker2(){
        super();
        lapDist = 0;
        lapCount = 0;
        halfWayDone = true;
        halfWayDist = 0;
        TIMELIMIT = 10000;
    }

    public void doTimestep(SensorModel sensors, Action actions)
    {
        raceTime++; // count raceTime

        // get relevant sensor and action parameters and store them
        double[] sensorData = fetchSensors(sensors);
        double[] actionData = fetchActions(actions);

        sensorMemory.add(sensorData);
        actionMemory.add(actionData);

        if (sensorMemory.size() > MEM_SIZE) // delete old values
        {
            sensorMemory.remove(0);
            actionMemory.remove(0);
        }

        evalTimestep(); // evaluate temporary fitness. set stopRace

        if (raceTime > TIMELIMIT) //
        {
            actions.abandonRace = true;
        }
    }

    public double getFitness()
    {
        if (finalFitness == -1) // calculate finalFitness
        {
            finalFitness = computeFitness();
        }
        return finalFitness;
    }

    private double[] fetchSensors(SensorModel sensors)
    {
        double[] s = {sensors.getDistanceFromStartLine(),sensors.getLastLapTime(),
                      sensors.getDistanceRaced(), sensors.getAngleToTrackAxis()};
        return s;
    }

    private double[] fetchActions(Action actions)
    {
        double[] a = {actions.brake, actions.accelerate};
        return a;
    }

    private void evalTimestep()
    {
        // see if lap was completed
        if (offset == 0)
        {
            offset = sensorMemory.getFirst()[0];
            halfWayDist = offset/2;
        }
        if (halfWayDone && sensorMemory.getFirst()[0] - sensorMemory.getLast()[0] > 1000)
        {
            lapDist = sensorMemory.getFirst()[0];
            lapCount++;
            halfWayDone = false;
        }
        if (sensorMemory.getLast()[0] - sensorMemory.getFirst()[0] > 1000)
        {
            lapDist -= sensorMemory.getFirst()[0];
            lapCount--;
            halfWayDone = true;
        }
        if (sensorMemory.getFirst()[0] < halfWayDist && sensorMemory.getLast()[0] > halfWayDist)
        {
            halfWayDone = true;
        }
        if (actionMemory.getLast()[0] > 0.5 && actionMemory.getLast()[1] > 0.5)
        {
            //don't brake and accelerate at the same time!
            temporaryFitness += 1; // note that only the final fitness has to be positive.
        }
        else if (Math.abs(sensorMemory.getLast()[1]) > 1.5)
        {
            temporaryFitness += 1; //don't go parallel to the track.
        }
    }

    private double computeFitness()
    {
        double f = sensorMemory.getLast()[0] + lapDist - offset; // distance travelled
        //System.out.println("lapDist = " + lapDist);
        //System.out.println("offset = " + offset);
        //System.out.println("sensorMemory.getLast()[0] = " + sensorMemory.getLast()[0]);

        f *= (raceTime - temporaryFitness)/raceTime; // factor in time spent pressing all pedals at once
        return f;
    }
}
