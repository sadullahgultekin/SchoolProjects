import numpy as np
import os
import matplotlib.pyplot as plt

## Sigmoid + Sigmoid + MSE
print("Activation Functions: Sigmoid + Sigmoid -- Loss Function: MSE")
np.random.seed(21)
def sigmoid(x):
    return np.where(x >= 0, 
                    1 / (1 + np.exp(-x)), 
                    np.exp(x) / (1 + np.exp(x)))

# xor points
x = np.array([[0,0],[0,1],[1,0],[1,1]])
# xor labels
y = np.array([0,1,1,0]).reshape(4,1)
# training settings
num_epoch = 10000
lr_rate = 0.8

# init weights
w1 = np.random.random((2,2))
w2 = np.random.random((2,1))

# to collect all losses
all_losses = []

for e in range(num_epoch):
    # forward
    inter = sigmoid(np.dot(x,w1))
    res = sigmoid(np.dot(inter,w2)).reshape(-1,1)

    # loss
    loss = 1/y.shape[0] * np.sum((res - y)**2)

    # backward
    g_w2 = np.dot(inter.T, (res-y)*res*(1-res))
    g_w1 = np.dot(x.T, np.dot((res-y)*res*(1-res), w2.T) * (inter*(1-inter)))

    # param update
    w2 -= lr_rate*g_w2
    w1 -= lr_rate*g_w1

    # collect loss
    all_losses.append(loss)

# pred
inter = sigmoid(np.dot(x,w1))
res = sigmoid(np.dot(inter,w2)).reshape(-1,1)
res[res >= 0.5] = 1
res[res < 0.5] = 0

print("labels:\n", y.tolist(), "\npredictions:\n", res.astype(int).tolist())

## ReLU + Sigmoid + MSE
print()
print("Activation Functions: ReLU + Sigmoid -- Loss Function: MSE")
np.random.seed(21)

def sigmoid(x):
    return np.where(x >= 0, 
                    1 / (1 + np.exp(-x)), 
                    np.exp(x) / (1 + np.exp(x)))

# xor points
x = np.array([[0,0],[0,1],[1,0],[1,1]])
# xor labels
y = np.array([0,1,1,0]).reshape(4,1)
# training settings
num_epoch = 80000
lr_rate = 0.001
num_hidden = 2

# init weights
w1 = np.random.random((2,num_hidden))
b1 = np.random.random((1,num_hidden))

w2 = np.random.random((num_hidden,1))
b2 = np.random.random((1,1))

# to collect all losses
all_losses = []

for e in range(num_epoch):
    # forward
    z1 = np.matmul(x,w1)
    z1 += b1
    a1 = np.maximum(z1, 0)

    z2 = np.matmul(a1,w2)
    z2 += b2
    a2 = sigmoid(z2)

    # loss
    loss = np.sum((a2 - y)**2)

    # backward
    dw2 = np.matmul(a1.T, (a2 - y) * (a2 * (1 - a2)))
    db2 = np.sum((a2 - y) * (a2 * (1 - a2)))

    temp = np.matmul((a2 - y) * (a2 * (1 - a2)), w2.T) * np.heaviside(z1, 0)
    dw1 = np.matmul(x.T, temp)
    db1 = np.sum(temp, axis=0)

    # update params
    w1 -= lr_rate * dw1
    b1 -= lr_rate * db1
    w2 -= lr_rate * dw2
    b2 -= lr_rate * db2

    # collect loss
    all_losses.append(loss)

# pred
z1 = np.matmul(x,w1)
z1 += b1
a1 = np.maximum(z1, 0)

z2 = np.matmul(a1,w2)
z2 += b2
a2 = sigmoid(z2)

a2[a2 >= 0.5] = 1
a2[a2 < 0.5] = 0

print("labels:\n", y.tolist(), "\npredictions:\n", a2.astype(int).tolist())

## Sigmoid + Sigmoid + NLL
print()
print("Activation Functions: Sigmoid + Sigmoid -- Loss Function: NLL")
np.random.seed(7)

def sigmoid(x):
    return np.where(x >= 0, 
                    1 / (1 + np.exp(-x)), 
                    np.exp(x) / (1 + np.exp(x)))

# xor points
x = np.array([[0,0],[0,1],[1,0],[1,1]])
# xor labels
y = np.array([0,1,1,0]).reshape(4,1)
# training settings
num_epoch = 10000
lr_rate = 0.1
num_hidden = 2

# init weights
w1 = np.random.random((2,num_hidden))
b1 = np.random.random((1,num_hidden))

w2 = np.random.random((num_hidden,1))
b2 = np.random.random((1,1))

# to collect all losses
all_losses = []

for e in range(num_epoch):
    # forward
    z1 = np.matmul(x,w1)
    z1 += b1
    a1 = sigmoid(z1)

    z2 = np.matmul(a1,w2)
    z2 += b2
    a2 = sigmoid(z2)
    
    # loss
    loss = -np.sum(y*np.log(a2)+(1-y)*np.log(1-a2))
    
    # backward
    temp1 = (a2 - y) * a2 * (1 - a2)
    dw2 = np.matmul(a1.T, temp1)
    db2 = np.sum(temp1)
    
    temp2 = np.matmul(temp1, w2.T) * (a1 * (1 - a1))
    dw1 = np.matmul(x.T, temp2)
    db1 = np.sum(temp2)

    # update params
    w1 -= lr_rate * dw1
    b1 -= lr_rate * db1
    w2 -= lr_rate * dw2
    b2 -= lr_rate * db2

    #collect loss
    all_losses.append(loss)
    
# pred
z1 = np.matmul(x,w1)
z1 += b1
a1 = sigmoid(z1)

z2 = np.matmul(a1,w2)
z2 += b2
a2 = sigmoid(z2)

a2[a2 >= 0.5] = 1
a2[a2 < 0.5] = 0

print("labels:\n", y.tolist(), "\npredictions:\n", a2.astype(int).tolist())