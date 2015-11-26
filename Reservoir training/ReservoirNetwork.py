import scipy.sparse as sp
import numpy as np
import scipy.sparse.linalg as lin
from sklearn.linear_model import RidgeCV

class ReservoirNetwork:
    def __init__(self, Nx, Nu, alpha, leaking_rate, a = 90):
        # The dimension of the reservoir (which is square)
        self.Nx = Nx
        # The dimension of the input.
        self.Nu = Nu
        self.alpha = alpha
        self.leaking_rate = leaking_rate

        # Initialize the Reservoir
        self.W = self.initializeW()
        # Initialize the input connections.
        self.Wi = np.random.uniform(-a, a, size=(Nx, Nu + 1))/a

    def initializeW(self):
        W = sp.rand(self.Nx, self.Nx, 0.1)
        rows, cols, vals = sp.find(W)
        vals = vals - np.mean(vals)

        W = sp.coo_matrix((vals,(rows,cols)),shape=(self.Nx, self.Nx))
        specrad = np.max(np.absolute(lin.eigs(W, self.Nx - 2)[0]))
        vals = self.alpha / specrad * vals

        W = sp.coo_matrix((vals,(rows,cols)),shape = (self.Nx, self.Nx))

        return W

    def X(self, U):
        # The amount of training data.
        T = len(U)
        # Start the construction of the activations of the reservoir with the first input.
        x_prev = np.zeros(self.Nx)
        X = self.x(U[0], x_prev, 0.1)
        x_prev = X
        # Now loop through all the training data and activate the reservoir.
        for n in range(1, T):
            x = self.x(U[n], x_prev, 0.1)
            X = np.column_stack((X, x))
            x_prev = X[:, n]

        # Concatenate a row of ones, the input and the activations.
        Ones = np.ones(T)
        Z = np.row_stack((Ones, U.T))
        return np.row_stack((Z, X))

    def x(self, u, x_prev):
        z = np.concatenate(([1], u))
        x_tilde = np.tanh(self.Wi.dot(z) + self.W.dot(x_prev))
        return (1 - self.leaking_rate) * x_prev + self.leaking_rate * x_tilde

    def train(self, U, Y):
        X = self.X(U)
        #clf = RidgeCV(1.0, False, False, False)
        #clf.fit(X, Y.T)
        #self.Wout = clf.coef_
        temp_1 = np.dot(Y.T, X.T)
        temp_2 = X.dot(X.T)
        temp_2 = temp_2 + 0.1 * np.eye(1 + self.Nu + self.Nx)
        self.Wout = temp_1.dot(np.linalg.inv(temp_2))