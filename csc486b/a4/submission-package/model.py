# model.py ---
#
# Filename: model.py
# Description:
# Author: Kwang Moo Yi
# Maintainer:
# Created: Thu Jan 24 17:28:40 2019 (-0800)
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
from torch import nn


class MyNetwork(nn.Module):
    """Network class """

    def __init__(self, config, input_shp, mean=None, std=None):
        """Initialization of the model.

        Parameters
        ----------

        config:
            Configuration object that holds the command line arguments.

        input_shp: tuple or list
            Shape of each input data sample.

        mean: np.array
            Mean value to be used for data normalization. We will store this in
            a torch.Parameter

        std: np.array
            Std value to be used for data normalization. We will store this in
            a torch.Parameter
        """

        # Run initialization for super class
        super(MyNetwork, self).__init__()

        # Store configuration
        self.config = config

        # TODO (5 points): Create torch.Tensor for holding mean, std. We will
        # apply these later, and if nothing is given, we would want to do
        # nothing to our data, i.e. mean should be set to zeros, std should be
        # set to ones. We also want all tensors to be `float32`
        if mean is None and std is None:
            mean_tensor = torch.zeros(input_shp, dtype=torch.float32)
            std_tensor = torch.ones(input_shp, dtype=torch.float32)
        elif mean is not None and std is not None:
            mean_tensor = torch.Tensor(mean.astype(np.float32))
            std_tensor = torch.Tensor(std.astype(np.float32))
        else:
            raise Exception("One of mean or std was None, but the other was not.")


        # TODO (5 points): Wrap the created Tensors as parameters, so that we
        # can easily save and load (we can later get a list of everything
        # inside the morel by doing model.parameters(). Also make sure we mark
        # that these will not be updated by the optimizer by saying that
        # gradient computation should not be performed
        self.mean = nn.Parameter(mean_tensor, requires_grad=False)
        self.std = nn.Parameter(std_tensor, requires_grad=False)

        # If mean, std is provided, update values accordingly
        if mean is not None and std is not None:
            #
            # ALTHOUGH THIS IS NOT PART OF YOUR IMPLEMENTATION, SEE BELOW.
            #
            # Note here that we use [:] so that actually assign the values, not
            # simply change the reference.
            self.mean[:] = torch.from_numpy(mean.astype(np.float32))
            self.std[:] = torch.from_numpy(std.astype(np.float32))

        # TODO: (5 points) We'll create `config.num_hidden` number of linear
        # layers, which each has `config.num_unit` of outputs. We will also
        # connect them with ReLU activation functions (see torch.nn.ReLU). We
        # will procedurally generate them as class attributes according to the
        # configurations. `setattr` Python builtin will be helpful here.
        self.num_layers = config.num_hidden+1
        # self.linear_0 = nn.Linear(input_shp[0], config.num_unit) # For Kwang's model
        self.linear0 = nn.Linear(input_shp[0], config.num_unit)

        # for i in range(1, config.num_hidden): # For Kwang's model
        #     setattr(self, "linear_"+str(i),\
        #             nn.Linear(config.num_unit, config.num_unit))
        for i in range(config.num_hidden):
            setattr(self, "linear"+str(i+1),\
                    nn.Linear(config.num_unit, config.num_unit))

        self.output = nn.Linear(config.num_unit, config.num_class)

    def forward(self, x):
        """Forward pass for the model 

        Parameters
        ----------

        x: torch.Tensor
            Input data for the model to be applied. Note that this data is
            typically in the shape of BCHW or BC, where B is the number of
            elements in the batch, and C is the number of dimension of our
            feature. H, W is when we use raw images. In the current assignment,
            it wil l be of shape BC.

        Returns
        -------

        x: torch.Tensor

            We will reuse the variable name, because often the case it's more
            convenient to do so. We will first normalize the input, and then
            feed it to our linear layer by simply calling the layer as a
            function with normalized x as argument.

        """

        # TODO (5 points): Normalize data.
        x = (x-self.mean)/self.std

        # TODO: (5 points)Apply layers. One thing that could be helpful is
        # the `getattr` Python builtin, which is the opposite of setattr.
        # above.
        for i in range(self.num_layers):
            # x = getattr(self, "linear_"+str(i))(x) # For Kwang's model
            x = getattr(self, "linear"+str(i))(x)
            x = nn.ReLU()(x)
        x = self.output(x)

        return x


#
# model.py ends here
