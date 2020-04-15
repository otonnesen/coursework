# import libraries here
from PIL import Image
import numpy as np
import h5py
from scipy import ndimage
import math


def read_image(image_path, *args, **kwargs):
    """Read an image into a numpy array given the path of the file.

    Parameters
    ----------
    image_path : string
        Path to the image file.

    Returns
    -------
    image : ndarray
        HWC formatted image.
    """
    image = np.asarray(Image.open(image_path))

    return image


def invert_image(image, *args, **kwargs):
    """Invert the image color by subtracting the image from a white image.

    Parameters
    ----------
    image : ndarray
        Original image.

    Returns
    -------
    inv_image : numpy.ndarray
        Inverted image.
    """
    white = np.ndarray(shape=image.shape, dtype=image.dtype)
    white.fill(255)
    inv_image = white - image

    return inv_image


def save_image_to_h5(image, h5_path, *args, **kwargs):
    """Save an image to H5 file.

    Parameters
    ----------
    image : ndarray
        The image to be saved.

    h5_path: string
        Path to the H5 file.
    """
    with h5py.File(h5_path, "w") as f:
        f.create_group("data").create_dataset("image", data=image)


def read_image_from_h5(h5_path, *args, **kwargs):
    """Read an image into a numpy array given the path of a H5 file.

    Parameters
    ----------
    h5_path : string
        Path to the H5 file.

    Returns
    -------
    image : ndarray
        HWC formatted image.
    """
    image = np.ndarray(shape=(640, 480, 3), dtype=int)
    with h5py.File(h5_path, "r") as f:
        image = np.asarray(f["data"]["image"])

    return image


def gray_scale_image(image, *args, **kwargs):
    """Return the gray_scale image by taking the mean over channels.

    Parameters
    ----------
    image : ndarray
        Original image.

    Returns
    -------
    gray_scale : ndarray
        HW formmated gray scale image.
    """
    gray_scale = np.ndarray(shape=(640, 480), dtype=int)
    gray_scale = np.mean(image, axis=2, dtype=int)

    return gray_scale


def find_difference_of_gaussian_blur(image, k1, k2, *args, **kwargs):
    """Find the difference of two Gaussian blurs from an image.

    Parameters
    ----------
    image : ndarray
        Original image.

    k1 : scalar
        First standard deviation for Gaussian kernel.

    k2 : scalar
        Second standard deviation for Gaussian kernel.

    Returns
    -------
    res : ndarray
        Normalized difference of Gaussian blurs.
    """
    gs = gray_scale_image(image)
    gf1 = ndimage.filters.gaussian_filter(gs, k1, mode="reflect")
    gf2 = ndimage.filters.gaussian_filter(gs, k2, mode="reflect")
    res = gf2 - gf1
    bot = min(res.flatten())
    top = max(res.flatten())

    res = (res-bot)/(top-bot)

    return res


def keep_top_percentile(image, percentile, *args, **kwargs):
    """Find the difference of two Gaussian blurs from an image.

    Parameters
    ----------
    image : ndarray
        Original image.

    percentile : scalar
        Top percentile pixels will be kept.

    Returns
    -------
    thresholded : ndarray
        Image with the high value pixles.
    """
    threshold = np.percentile(image, 100-percentile)
    thresholded = (image >= threshold) * image

    return thresholded
