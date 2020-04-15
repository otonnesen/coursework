import numpy as np
import math


def linear_svm(W, b, x, y):
    """Linear SVM loss function.

    Parameters
    ----------
    W : ndarray
        The weight parameters of the linear classifier. D x C, where C is the
        number of classes, and D is the dimension of input data.

    b : ndarray
        The bias parameters of the linear classifier. C, where C is the number
        of classes.

    x : ndarray
        Input data that we want to predict the labels of. NxD, where D is the
        dimension of the input data.

    y : ndarray
        Ground truth labels associated with each sample. N numbers where each
        number corresponds to a class.

    Returns
    -------
    loss : float
        The average loss coming from this model. In the lecture slides,
        represented as \frac{1}{N}\sum_i L_i.
    """

    s = np.dot(x, W) + b

    # Remove true labels from s
    s_j = s[np.arange(s.shape[1]) != y[:, np.newaxis]] \
            .reshape(s.shape[0], s.shape[1]-1)

    # Array of values of true labels in s
    s_yi = np.asarray([s[np.arange(s.shape[0]), y]]).transpose()

    # Find max between 0 and s_j-syi+1
    m = (s_j - s_yi + 1) * ((s_j - s_yi + 1) > 0)
    L_i = np.sum(m, axis=1)

    loss = np.mean(L_i)

    return loss


def logistic_regression(W, b, x, y):
    """Logistic regression loss function.

    Parameters
    ----------
    W : ndarray
        The weight parameters of the linear classifier. D x C, where C is the
        number of classes, and D is the dimension of input data.

    b : ndarray
        The bias parameters of the linear classifier. C, where C is the number
        of classes.

    x : ndarray
        Input data that we want to predict the labels of. NxD, where D is the
        dimension of the input data.

    y : ndarray
        Ground truth labels associated with each sample. N numbers where each
        number corresponds to a class.

    Returns
    -------
    loss : float
        The average loss coming from this model. In the lecture slides,
        represented as \frac{1}{N}\sum_i L_i.

    """

    # TODO: implement the function
    s = np.dot(x, W) + b

    # Array of values of true labels in s
    s_yi = s[np.arange(s.shape[0]), y]

    loss = -np.sum(np.log((math.e ** s_yi) / np.sum(math.e ** s, axis=1))) \
            / s.shape[0]

    return loss
