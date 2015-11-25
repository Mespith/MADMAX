import scipy.sparse as sp
import numpy as np
import scipy.sparse.linalg as lin

class ReservoirNetwork:
    def __init__(self, Nx, Nu, alpha, a = 90):
        self.Nx = Nx
        self.Nu = Nu
        self.alpha = alpha

        self.W = self.initializeW()
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
        T = len(U)
        Ones = np.ones(T)
        X = np.ndarray((self.Nx, T))
        for n in range(T):
            x_prev = np.zeros(self.Nx)
            if n > 0:
                x_prev = X[n-1]
            X[n] = self.x(U[n], x_prev)

        Z = np.concatenate((Ones, U))
        return np.concatenate((Z, X))

    def x(self, u, x_prev):
        z = np.concatenate(([1], u))
        x_tilde = np.tanh(np.dot(self.Wi, z) + np.dot(self.W, x_prev))
        return (1 - self.alpha) * x_prev + self.alpha * x_tilde

    def y(Wo, ux):
        return np.dot(Wo, ux)