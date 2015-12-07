import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;

import java.util.LinkedList;

/**
 * part of a driver that evaluates performance throughout the race, may stop the race and finally returns fitness.
 *
 * This class does a basic evaluation and should be extended for further fitness functions.
 * Extension will require the following overwrites:
 *      fetchSensors()
 *      fetchActions()   fill either with desired parameters
 *      evalTimestep()   for online evaluation. and early termination. may be left empty
 *      computeFitness() for final fitness function. factor in temporaryFitness if it was used
 *
 * the rest should be pretty universal.
 *
 * Created by Frederik on 06.12.2015.
 */
public class RaceTracker {


    private static int MEM_SIZE = 1;                    //default size of past timesteps to keep track of.
    private double temporaryFitness;                    // keeps track of events during the race
    private double finalFitness;                        // final fitness value. computed once.
    private LinkedList<double[]> sensorMemory;          // list of n past sensor parameters
    private LinkedList<double[]> actionMemory;          // list of n past action parameters
    private static long TIMELIMIT = 12000;//Long.MAX_VALUE;     // number of time steps after which the race is terminated
    private long raceTime;                              // number of past timesteps in current race
    private boolean stopRace;                           // driver will terminate race if set to true

    public  RaceTracker()
    {
        this.temporaryFitness = 0;
        this.finalFitness = -1;
        this.sensorMemory = new LinkedList<>();
        this.actionMemory = new LinkedList<>();
        this.stopRace = false;
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

        if (stopRace || raceTime > TIMELIMIT) //
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

    /**
     * above: keep
     *
     * below: overwrite
     */

    private double[] fetchSensors(SensorModel sensors)
    {
        double[] s = {sensors.getDistanceFromStartLine(), sensors.getRacePosition()};
        return s;
    }

    private double[] fetchActions(Action actions)
    {
        double[] a = {actions.brake, actions.accelerate};
        return a;
    }

    private void evalTimestep()
    {
        if (actionMemory.getLast()[0] > 0.5 && actionMemory.getLast()[1] > 0.5)
        {
            //don't brake and accelerate at the same time!
            temporaryFitness += 1; // note that only the final fitness has to be positive.
        }
    }

    private double computeFitness()
    {
        double f = sensorMemory.getLast()[0]; // distance travelled
        f *= (raceTime - temporaryFitness)/raceTime; // factor in time spent pressing all pedals at once
        return f;
    }

}
