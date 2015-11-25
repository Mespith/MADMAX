import numpy as np

class TrainingData:
    def __init__(self, dataPath):
        f = open(dataPath)
        self.X = []
        self.Y = []
        for line in f:
            data = line.split(';')
            _input = data[0].split(',')
            _output = data[1].split(',')
            x = np.ndarray(len(_input))
            y = np.ndarray(len(_output))

            for i in range(len(_input)):
                x[i] = float(_input[i])
            for j in range(len(_output)):
                y[j] = float(_output[j])

            self.X.append(x)
            self.Y.append(y)

        f.close()