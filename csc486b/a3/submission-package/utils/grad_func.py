import numpy as np


def linear_svm_grad(loss_c, x, y):
    """Linear SVM gradient.

    Parameters
    ----------
    loss_c : ndarray
        The individual losses per sample and class, or any other value that
        needs to be reused when computing gradients.

    x : ndarray
        Input data that we want to predic the labels of. NxD, where D is the
        dimension of the input data.

    y : ndarray
        Ground truth labels associated with each sample. N numbers where each
        number corresponds to a class.

    Returns
    -------
    dW : ndarray
        Gradient associated with W. Should be the same shape (DxC) as W.

    db : ndarray
        Gradient associated with b. Should be the same shape (C) as b.

    """

    # loss_c : (N, C)
    # x : (N, D)
    # y : (N, )

    N, C = loss_c.shape
    D = x.shape[1]

    # Get number of penalized classes per sample (K in slides)
    K = np.sum(loss_c > 0, axis=1) - 1  # (N, )

    # ----------------------------------------
    # For the W (N, D, C)
    #
    # First, create a mask where we have samples within margin, i.e. where loss
    # is generated (second case). We also prepare it for broadcasting
    mask = np.reshape(loss_c > 0, (N, 1, C)).astype(float)
    # Then, the gradients that go here is simply the samples where the mask
    # survives.
    dW = np.reshape(x, (N, D, 1)) * mask
    # Now we fill the k=y part -- create weighted xs
    dW1 = np.reshape(-K, (N, 1)) * x  # (N, D)
    # Fill it in with fancy numpy indicing :-)
    dW[np.arange(N), :, y] = dW1
    # Now we average, since we averaged in the loss
    dW = np.mean(dW, axis=0)

    # ----------------------------------------
    # For the b (N, C)
    #
    # As above, we simply need to look at loss per class and put 1s
    db = np.reshape(loss_c > 0, (N, C)).astype(float)
    # Now we fill the k=y part
    db[np.arange(N), y] = -K
    # Now we average, since we averaged in the loss
    db = np.mean(db, axis=0)

    return dW, db

#
# linear_svm.py ends here


def logistic_regression_grad(loss_c, x, y):
    """Logistic regression gradient.

    Parameters
    ----------
    loss_c : ndarray
        The individual losses per sample and class, or any other value that
        needs to be reused when computing gradients.

    x : ndarray
        Input data that we want to predic the labels of. NxD, where D is the
        dimension of the input data.

    y : ndarray
        Ground truth labels associated with each sample. N numbers where each
        number corresponds to a class.

    Returns
    -------
    dW : ndarray
        Gradient associated with W. Should be the same shape (DxC) as W.

    db : ndarray
        Gradient associated with b. Should be the same shape (C) as b.

    """

    # loss_c : (N, C)
    # x : (N, D)
    # y : (N, )

    # TODO (20 points): Implment the gradients
    N, C = loss_c.shape
    D = x.shape[1]

    loss_c[np.arange(N), y] -= 1
    dW = np.dot(x.T, loss_c)
    db = np.sum(loss_c, axis=0)

    return dW, db


#
# logistic_regression.py ends here
