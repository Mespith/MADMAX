import TrainingData as td
import ReservoirNetwork as rn
import scipy.sparse as sp
import numpy as np

alpha = 0.34
Nx = 10
leaking_rate = 0.1

training_data = td.TrainingData("AllTrainingData.txt")
network = rn.ReservoirNetwork(Nx, len(training_data.U[0]), alpha, leaking_rate)
network.train(training_data.U, training_data.Y)

f = open("OutputWeights.txt", 'w')
f.write(str(Nx)+","+str(len(network.Wi))+","+str(len(network.Wout))+" " + str(leaking_rate) +"\n")
rows, cols, vals = sp.find(network.W)
counter = 0
W = np.zeros((Nx,Nx))
for i, row in enumerate(rows):
    W[row][cols[i]] = vals[i]
for row in W:
    f.write(str(row) + "\n")
for row in network.Wi:
    f.write(str(row) + "\n")
for row in network.Wout:
    f.write(str(row) + "\n")