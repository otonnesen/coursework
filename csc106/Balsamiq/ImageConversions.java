/* 
 * Name: Oliver Tonnesen
 * ID: V00885732
 * Date: 10/30/2017
 * Filename: ImageConversions.java
 */
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * Class ImageConversions is a collection of methods that convert jpg images
 * into variations.
 */
public class ImageConversions {
	/**
	 * Handles all input instructions and relays the information to the
	 * appropriate method.
	 * If the input is not as specified, the program stops, with an informative message.
	 * @param args The input necessary to execute the appropriate conversion.
	 * The array is formated in one of the following ways:
	 * <ul style="list-style:none;">
	 * <li>[infile outfile] invert
	 * 	<ul style="list-style:none;">
	 *	<li>infile: image file  outfile: image file</li>
	 * 	</ul></li>
	 * <li>[infile outfile] verticalFlip</li>
	 * <li>[infile outfile] horizontalFlip</li>
	 * <li>[infile outfile] makeAscii</li>
	 * <li>[infile outfile] scale [scalefactor]
	 *	<ul style="list-style:none;">
	 *	<li>scalefactor: A number: 1 maintains original size, &lt; 1 reduces size, &gt; 1 enlarges size.</li>
	 *	</ul></li>
	 * <li>[infile outfile] rotate</li>
	 * </ul>
	 */
	public static void main(String[] args)
			throws FileNotFoundException {
		String inputfilename, outputfilename, methodCalled, scalefactor;
		if(args.length<3) {
			System.out.println("Invalid parameters, aborting...");
			System.exit(-1);
		} else {
			inputfilename = args[0];
			outputfilename = args[1];
			methodCalled = args[2];
			int[][] image = readGrayscaleImage(inputfilename);
			int[][] output = {{}};
			if(methodCalled.equals("invert")) {
				output = invert(image);
			} else if(methodCalled.equals("verticalFlip")) {
				output = verticalFlip(image);
			} else if(methodCalled.equals("horizontalFlip")) {
				output = horizontalFlip(image);
			} else if(methodCalled.equals("makeAscii")) {
				makeAscii(image, outputfilename);
				System.exit(0);
			} else if(methodCalled.equals("rotate")) {
				output = rotate(image);
			} else if(methodCalled.equals("change")) {
				output = change(image);
			} else if(methodCalled.equals("scale")) {
				scalefactor = args[3];
				output = scale(image, (Double.parseDouble(scalefactor)));
			} else {
				System.out.println("Invalid parameters, aborting...");
				System.exit(-1);
			}
			writeGrayscaleImage(outputfilename, output);
		}
	}

				
	// THIS METHOD MAY BE CALLED, BUT MUST NOT BE MODIFIED!
	/** 
	 * Reads an image file and converts it to a 2D array
	 * of integers.
	 * Each value in the array is a representation
	 * of the corresponding pixel's grayscale value.
	 * @param filename The name of the image file
	 * @return A 2D array of integers.
	 * @throws RuntimeException if the input file cannot be found or read.
	 */
	public static int[][] readGrayscaleImage(String filename) {
		int[][] result = null;
		File imageFile = new File(filename);
		BufferedImage image = null;
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException ioe) {
			System.err.println("Problems reading file named " + filename);
			throw new RuntimeException("Please ensure the image file is saved in the same directory as your java file.");
		}
		int height = image.getHeight();
		int width  = image.getWidth();
		result = new int[height][width];
		int rgb;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				rgb = image.getRGB(x, y);
				result[y][x] = rgb & 0xff;
			}
		}
		return result;
	}

	// THIS METHOD MAY BE CALLED, BUT MUST NOT BE MODIFIED!
	/**
	 * Reads a 2D array of integers and creates
	 * a grayscale image. Each pixel's grayscale value is
	 * based on the corresponding value in the 2D array.
	 * @param filename The name of the image file to create
	 * @param array The 2D array of integers
	 */
	public static void writeGrayscaleImage(String filename, int[][] array) {
		int width = array[0].length;
		int height = array.length;
		BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);

		int rgb;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				rgb = array[y][x];
				rgb |= rgb << 8;
				rgb |= rgb << 16;
				image.setRGB(x, y, rgb);
			}
		}
		File imageFile = new File(filename);
		try {
			ImageIO.write(image, "jpg", imageFile);
		} catch (IOException ioe) {
			System.err.println("The file could not be created " + filename);
			return;
		}
	}

/*
 * Inverts each value in a 2D array of integers between 0 and 255.
 * Input: 2D array of integers from 0 to 255
 * Output: 2D array of the inverse values of the input
 */
	public static int[][] invert(int[][] image) {
		for(int i = 0; i < image.length; i++) {
			for(int j = 0; j < image[i].length; j++) {
				image[i][j] = 255 - image[i][j];
			}
		}
		return image;
	}

/*
 * Reverses all the values in each row in a 2D array of integers.
 * Input: 2D array of integers from 0 to 255
 * Output: 2D array of integers with all rows reversed
 */
	public static int[][] horizontalFlip(int[][] image) {

		int temp;
		for(int i = 0; i < image.length; i++) {

			for(int j = 0; j < image[i].length/2; j++) {
				temp = image[i][j];
				image[i][j] = image[i][image[i].length-1-j];
				image[i][image[i].length-1-j] = temp;
				
			}

		}

		return image;
	}

/*
 * Reverses all the values in each column in a 2D array of integers.
 * Input: 2D array of integers from 0 to 255
 * Output: 2D array of integers with all columns reversed
 */
	public static int[][] verticalFlip(int[][] image) {

		int[] temp;
		for(int i = 0; i < image.length/2; i++) {
			temp = image[i];
			image[i] = image[image.length-1-i];
			image[image.length-1-i] = temp;
		}
		return image;
	}

/*
 * Creates an ASCI image text file in which each character represents a range of integer values.
 * Input: 
 * 	- image:	2D array of integers from 0 to 255
 * 	- outName:	Name of file to which to output the ASCII image
 * Output: None
 */
	public static void makeAscii(int[][] image, String outName) 
			throws FileNotFoundException {
		PrintStream output = new PrintStream(new File(outName));
		char[][] asciiImage = new char[image.length][image[0].length];
		for(int i = 0; i < image.length; i++) {
			for(int j = 0; j < image[i].length; j++) {
				if(image[i][j] >= 0 && image[i][j] <= 20) {		output.print('M');
				} else if(image[i][j] >= 21 && image[i][j] <= 40) {	output.print('L');
				} else if(image[i][j] >= 41 && image[i][j] <= 60) {	output.print('I');
				} else if(image[i][j] >= 61 && image[i][j] <= 80) {	output.print('o');
				} else if(image[i][j] >= 81 && image[i][j] <= 100) {	output.print('|');
				} else if(image[i][j] >= 101 && image[i][j] <= 120) {	output.print('=');
				} else if(image[i][j] >= 121 && image[i][j] <= 140) {	output.print('*');
				} else if(image[i][j] >= 141 && image[i][j] <= 160) {	output.print(':');
				} else if(image[i][j] >= 161 && image[i][j] <= 180) {	output.print('-');
				} else if(image[i][j] >= 181 && image[i][j] <= 200) {	output.print(',');
				} else if(image[i][j] >= 201 && image[i][j] <= 220) {	output.print('.');
				} else {						output.print(' ');
				}


			}
			output.println();
		}
	}
	
/*
 * Scales a 2D array of integers by a given factor
 * Input:
 * 	- image:	2D array of integers from 0 to 255
 * 	- scalefactor:	The factor by which to scale the array
 * Output: 2D array of integers scaled by a factor
 */
	public static int[][] scale(int[][] image, double scalefactor) {
		int[][] scaledImage = new int[(int) (image.length*scalefactor)][(int) (image[0].length*scalefactor)];
		for(int i = 0; i < scaledImage.length; i++) {
			for(int j = 0; j < scaledImage[i].length; j++) {
				scaledImage[i][j] = image[(int) (i/scalefactor)][(int) (j/scalefactor)];
			}
		}
		return scaledImage;
	}

/*
 * Rotates a 2D array of integers 90 degrees clockwise.
 * Input: 2D array of integers from 0 to 255
 * Output: 2D array of integers with values rotated
 */
	public static int[][] rotate(int[][] image) {
		int[][] rotatedImage = new int[image[0].length][image.length];
		for(int i = 0; i < image[0].length; i++) {
			for(int j = 0; j < image.length-1; j++) {
				rotatedImage[i][j] = image[image.length-j-1][i];
			}
		}
		return rotatedImage;
	}

	public static int[][] change(int[][] image) {
		for(int i = 0; i < image.length; i++) {
			for(int j = 0; j < image[i].length; j++) {
				if(image[i][j] <= 50) {
					image[i][j] = 102;
				}
			}
		}
		return image;
	}


}
