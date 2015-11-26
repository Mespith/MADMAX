import TrainingData as td
import ReservoirNetwork as rn

alpha = 0.34
Nx = 10

training_data = td.TrainingData("AllTrainingData.txt")
network = rn.ReservoirNetwork(Nx, len(training_data.U[0]), alpha)
X = network.X(training_data.U)
print X

