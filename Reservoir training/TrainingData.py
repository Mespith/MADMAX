import numpy as np

class TrainingData:
    def __init__(self, dataPath):
        f = open(dataPath)
        # X and Y are lists because we don't know how big our training set is.
        self.U = []
        self.Y = []
        for line in f:
            data = line.split(';')
            _input = data[0].split(',')
            _output = data[1].split(',')
            # These are the training and target vectors for this example.
            x = np.ndarray(len(_input))
            y = np.ndarray(len(_output))
            # Fill the vectors.
            for i in range(len(_input)):
                x[i] = float(_input[i])
            for j in range(len(_output)):
                y[j] = float(_output[j])
            # Add the vectors to the training set.
            self.U.append(x)
            self.Y.append(y)
        # Because we need to compute things about the whole set, they need to be matrices.
        self.U = np.asarray(self.U)
        self.Y = np.asarray(self.Y)

        f.close()