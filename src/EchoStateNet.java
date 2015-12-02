import java.lang.Math;

import org.ejml.data.MatrixIterator64F;
import org.ejml.simple.SimpleMatrix;

public class EchoStateNet {

    SimpleMatrix inW;
    SimpleMatrix resW;
    SimpleMatrix outW;
    SimpleMatrix x_prev; //activation OUTPUT of last iteration
    double leaking_rate;

    public EchoStateNet(double[][] inW, double[][] resW, double[][] outW, double leaking_rate)
    {
        this.inW = new SimpleMatrix(inW);
        this.resW = new SimpleMatrix(resW);
        this.outW = new SimpleMatrix(outW);
        this.x_prev = new SimpleMatrix(resW.length, 1);
        this.leaking_rate = leaking_rate;
    }

    // This is method takes in a matrix of the sensor values and returns the values of the actuators.
    public SimpleMatrix forward_propagation(SimpleMatrix U)
    {
        SimpleMatrix x_tilde = inW.mult(U).plus(resW.mult(x_prev));
        x_tilde = tanhFunc(x_tilde);
        x_prev = x_prev.scale(1.0 - leaking_rate).plus(x_tilde.scale(leaking_rate));

        SimpleMatrix X = U.combine(U.numRows(), 0, x_prev);

        return outW.mult(X);
    }

    // Tanh function applied per element of the given matrix.
    private SimpleMatrix tanhFunc(SimpleMatrix input)
    {
        MatrixIterator64F iterator = input.iterator(true, 0, 0, input.numRows() - 1, input.numCols() - 1);
        while (iterator.hasNext()) {
            iterator.set(Math.tanh(iterator.next()));
        }
        return input;
    }
}

