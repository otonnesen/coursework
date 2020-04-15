# features.py ---
#
# Filename: features.py
# Description:
# Author: Kwang Moo Yi
# Maintainer:
# Created: Sun Jan 14 21:06:57 2018 (-0800)
# Version:
# Package-Requires: ()
# URL:
# Doc URL:
# Keywords:
# Compatibility:
#
#

# Commentary:
#
#
#
#

# Change Log:
#
#
#
# Copyright (C), Visual Computing Group @ University of Victoria.

# Code:

import numpy as np
from skimage.color import rgb2hsv
from skimage.feature import hog


def extract_h_histogram(data):
    """Extract Hue Histograms from data.

    Parameters
    ----------
    data : ndarray
        NHWC formatted input images.

    Returns
    -------
    h_hist : ndarray (float)
        Hue histgram per image, extracted and reshaped to be NxD, where D is
        the number of bins of each histogram.

    """

    h_hist = np.ndarray(shape=(data.shape[0], 16))

    for i in range(len(data)):
        hue = rgb2hsv(data[i])[:, :, 0]
        hist, _ = np.histogram(hue, np.linspace(0, 1, 17))
        h_hist[i] = hist

    # Assertion to help you check if implementation is correct
    assert h_hist.shape == (data.shape[0], 16)

    return h_hist


def extract_hog(data):
    """Extract HOG from data.

    Parameters
    ----------
    data : ndarray
        NHWC formatted input images.

    Returns
    -------
    hog_feat : ndarray (float)
        HOG features per image, extracted and reshaped to be NxD, where D is
        the dimension of HOG features.

    """

    # Using HOG
    print("Extracting HOG...")

    data = np.mean(data, axis=3)
    hog_feat = np.ndarray(shape=(data.shape[0], 324))
    for i in range(data.shape[0]):
        hog_feat[i] = hog(data[i])

    # Assertion to help you check if implementation is correct
    assert hog_feat.shape == (data.shape[0], 324)

    return hog_feat


#
# features.py ends here
