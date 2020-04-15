import numpy as np


def linear_svm(W, b, x, y):
    """Linear SVM loss function.

    Also computes the individual per-class losses to be used for gradient
    computation, as well as the predicted labels.

    Parameters
    ----------
    W : ndarray
        The weight parameters of the linear classifier. D x C, where C is the
        number of classes, and D is the dimenstion of input data.

    b : ndarray
        The bias parameters of the linear classifier. C, where C is the number
        of classes.

    x : ndarray
        Input data that we want to predic the labels of. NxD, where D is the
        dimension of the input data.

    y : ndarray
        Ground truth labels associated with each sample. N numbers where each
        number corresponds to a class.

    Returns
    -------
    loss : float
        The average loss coming from this model. In the lecture slides,
        represented as \sum_i L_i.

    temp : ndarray
        Temporary values to be used later that are calulated alonside when we
        compute the loss. It turns out much of the computation that we do on
        our ``forward'' pass can be used for the ``backward'' pass. In case of
        the SVM loss, these are per-class loss values for each sample.

    pred : ndarray
        Predictions from the model. N numbers where each number corresponds to
        a class.

    """

    # Scores for all class (N, 10)
    s_all = np.matmul(x, W) + b
    # Predections to use later
    pred = np.argmax(s_all, axis=1)
    # Score for the correct class (N, )
    s_y = s_all[np.arange(len(y)), y]
    # Make Nx1 to sub from s_all
    s_y = np.reshape(s_y, (-1, 1))
    # Loss per class (including the correct one)
    loss_c = np.maximum(0, s_all - s_y + 1)
    # Compute loss by averaging of samples, summing over classes. We subtract 1
    # after the sum, since the correct label always returns 1 in terms of the
    # per-class loss, and should be excluded from the final loss
    loss = np.mean(np.sum(loss_c, axis=1) - 1, axis=0)

    return loss, loss_c, pred


def logistic_regression(W, b, x, y):
    """Logistic regression loss function.

    Also computes the individual per-class losses to be used for gradient
    computation, as well as the predicted labels.

    Parameters
    ----------
    W : ndarray
        The weight parameters of the linear classifier. D x C, where C is the
        number of classes, and D is the dimenstion of input data.

    b : ndarray
        The bias parameters of the linear classifier. C, where C is the number
        of classes.

    x : ndarray
        Input data that we want to predic the labels of. NxD, where D is the
        dimension of the input data.

    y : ndarray
        Ground truth labels associated with each sample. N numbers where each
        number corresponds to a class.

    Returns
    -------
    loss : float
        The average loss coming from this model. In the lecture slides,
        represented as \sum_i L_i.

    temp : ndarray

        Temporary values to be used later that are calulated alonside when we
        compute the loss. It turns out much of the computation that we do on
        our ``forward'' pass can be used for the ``backward'' pass. In case of
        multinomial logistic regression, this is the per-class probabilities.

    pred : ndarray
        Predictions from the model. N numbers where each number corresponds to
        a class.

    """

    # Scores for all class (N, 10)
    s_all = np.matmul(x, W) + b
    # Predections to use later
    pred = np.argmax(s_all, axis=1)
    # Do exponential and normalize to get probs
    probs = np.exp(s_all - s_all.max(axis=1, keepdims=True))
    probs = probs / probs.sum(axis=1, keepdims=True)
    # For the cross entropy case, we will return the probs in temp

    # Compute loss
    loss = np.mean(-np.log(probs[np.arange(len(y)), y]))

    return loss, probs, pred
