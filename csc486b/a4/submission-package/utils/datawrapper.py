# datawrapper.py ---
#
# Filename: datawrapper.py
# Description:
# Author: Kwang Moo Yi
# Maintainer:
# Created: Wed Jan 23 16:36:38 2019 (-0800)
# Version:
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
import torch
from torch.utils import data

from utils.cifar10 import load_data
from utils.features import extract_h_histogram, extract_hog


class CIFAR10Dataset(data.Dataset):
    """The dataset wrapper for CIFAR 10.

    While writing this class is completely unecessary if we were to obtain the
    CIFAR10 data in raw format since PyTorch already has it written. However,
    in our case we will also try to extract data, we will implement it
    ourselves.

    """

    def __init__(self, config, mode):
        """Initialization.

        Parameters
        ----------

        config:
            Configuration object that holds the command line arguments.

        mode: str
            A string detailing which split for this dataset object to load. For
            example, train, valid, test.


        Notes
        -----

        By default, we assume the dataset is at downloads

        """

        # Save configuration
        self.config = config

        print("Loading CIFAR10 Dataset from {} for {}ing ...".format(
            config.data_dir, mode), end="")

        # Load data
        data, label = load_data(config.data_dir, mode)
        self.data = data
        self.label = label

        print(" done.")

        # TODO (10 points): extract features and update `self.data`, and at the
        # same time save the shape of each sample (not the entire data shape!)
        # to `self.sample_shp`.
        #
        # It turns out extracting data on-the-fly on a normal PC takes longer!
        # Since CIFAR10 is not a large dataset, we'll do it here. With large
        # datasets, we don't have this option.
        #
        # Also store the shape of each sample (depending on the feature
        # extraction that we apply)
        print("Extracting features for the entire {} set ... ".format(
            mode), end="")
        if config.feature_type == "hog":
            self.data = extract_hog(data)
            self.sample_shp = self.data[0].shape
        elif config.feature_type == "h_histogram":
            self.data = extract_h_histogram(data)
            self.sample_shp = self.data[0].shape

        self.data = self.data.astype(np.float32)

        print("done.")

    def __len__(self):
        """ Returns number of samples. """

        # TODO (5 points): return the number of elements at `self.data`
        return self.data.shape[0]

    def __getitem__(self, index):
        """Function to grab one data sample

        Parameters
        ----------

        index: int
            Index to the sample that we are trying to extract.


        Returns
        -------

        data_cur: torch.Tensor
            A torch tensor that holds the data. For GPU compatibility in the
            future, we will develop the habit of converting everythiing in to
            `float32`

        label_cur: int
            The label of the current data sample. This is typically a simple
            indexing operation to the label array

        """

        # TODO (10 points): Implement the get_item method
        data_cur = self.data[index]
        label_cur = self.label[index]

        return data_cur, label_cur


#
# datawrapper.py ends here
