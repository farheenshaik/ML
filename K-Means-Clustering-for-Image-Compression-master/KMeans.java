/*** Image input function author :Vibhav Gogate
The University of Texas at Dallas
*****/
/**
 * Henry Dinh
 * CS 6375.001
 * Assignment 3
 * KMeans Clustering for Image Compression
**/

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class KMeans {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: Kmeans <input-image> <k>");
			return;
		}
		try {
			BufferedImage originalImage = ImageIO.read(new File(args[0]));
			int k = Integer.parseInt(args[1]);
			String output_image_name = args[0].substring(0, args[0].length() - 4) + "_output_k" + args[1]
					+ args[0].substring(args[0].length() - 4, args[0].length());
			System.out.println("Image:\t\t" + args[0]);
			System.out.println("Image output:\t" + output_image_name);
			BufferedImage kmeansJpg = kmeans_helper(originalImage, k);
			ImageIO.write(kmeansJpg, "jpg", new File(output_image_name));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		System.out.println("Image width:\t" + w);
		System.out.println("Image height:\t" + h);
		BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w, h, null);
		// Read rgb values from the image
		int[] rgb = new int[w * h];
		int count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[count++] = kmeansImage.getRGB(i, j);
			}
		}
		// Call kmeans algorithm: update the rgb values
		kmeans(rgb, k);

		// Write the new rgb values to the image
		count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				kmeansImage.setRGB(i, j, rgb[count++]);
			}
		}
		return kmeansImage;
	}

	// k-means code goes here
	// Update the array rgb by assigning each entry in the rgb array to its
	// cluster center
	private static void kmeans(int[] rgb, int k) {
		// k = number of colors
		// Set k values initially to the colors of random pixels in the image.
		// Make sure they're all different from each other
		int[] k_values = new int[k];
		Random rand = new Random();
		for (int i = 0; i < k_values.length; i++) {
			int random_num;
			boolean contains_duplicate = true;
			if (i == 0) {
				random_num = rand.nextInt(rgb.length);
				k_values[i] = rgb[random_num];
			} else {
				do {
					random_num = rand.nextInt(rgb.length);
					for (int j = 0; j < i; j++) {
						if (j == i - 1 && k_values[j] != rgb[random_num]) {
							// If at the last element in k_values and
							// no duplicates detected
							k_values[i] = rgb[random_num];
							contains_duplicate = false;
						} else if (k_values[j] == rgb[random_num]) {
							// Exit for loop because duplicate color detected
							// and
							// try again with new random number
							j = i;
						}
					}
				} while (contains_duplicate);
			}
			System.out.println("Inital k mean " + i + ": " + k_values[i]);
		}

		// Group together similar pixels in the image
		// to corresponding cluster centers.
		// pixel assignments (by their index)
		int[] pixel_assignments = new int[rgb.length];
		int[] num_assignments = new int[k];

		// Cluster sums for current cluster values (represented by index)
		int[] alpha_sum = new int[k];
		int[] red_sum = new int[k];
		int[] green_sum = new int[k];
		int[] blue_sum = new int[k];

		// iterate until converged. Shouldn't take more than 100
		int max_iterations = 100;
		int num_iterations = 1;
		System.out.println("Clustering k = " + k + " points...");
		while (num_iterations <= max_iterations) {
			if (num_iterations % 10 == 0)
				System.out.println("Iteration:\t" + num_iterations);
			// Clear number of assignments list first
			for (int i = 0; i < k_values.length; i++) {
				num_assignments[i] = 0;
				alpha_sum[i] = 0;
				red_sum[i] = 0;
				green_sum[i] = 0;
				blue_sum[i] = 0;
			}

			// Go through all pixels in rgb
			for (int i = 0; i < rgb.length; i++) {
				// Set min_dist initially to infinity (or very large number that
				// wouldn't appear as a distance anyways)
				double min_dist = Double.MAX_VALUE;
				int cluster_index = 0;
				// compare instance's RGB value to each cluster point
				for (int j = 0; j < k_values.length; j++) {
					int a_dist = (getAlpha(rgb[i]) - getAlpha(k_values[j]));
					int r_dist = (getRed(rgb[i]) - getRed(k_values[j]));
					int g_dist = (getGreen(rgb[i]) - getGreen(k_values[j]));
					int b_dist = (getBlue(rgb[i]) - getBlue(k_values[j]));
					double dist = Math.sqrt(a_dist * a_dist + r_dist * r_dist + g_dist * g_dist + b_dist * b_dist);
					if (dist < min_dist) {
						min_dist = dist;
						cluster_index = j;
					}
				}
				// Assign pixel to cluster
				pixel_assignments[i] = cluster_index;
				num_assignments[cluster_index]++;
				// Add pixel's individual argb values to respective sums for use
				// later
				alpha_sum[cluster_index] += getAlpha(rgb[i]);
				red_sum[cluster_index] += getRed(rgb[i]);
				green_sum[cluster_index] += getGreen(rgb[i]);
				blue_sum[cluster_index] += getBlue(rgb[i]);
			}

			// update previous assignments list
			for (int i = 0; i < k_values.length; i++) {
				int avg_alpha = (int) ((double) alpha_sum[i] / (double) num_assignments[i]);
				int avg_red = (int) ((double) red_sum[i] / (double) num_assignments[i]);
				int avg_green = (int) ((double) green_sum[i] / (double) num_assignments[i]);
				int avg_blue = (int) ((double) blue_sum[i] / (double) num_assignments[i]);

				k_values[i] = ((avg_alpha & 0x000000FF) << 24) | ((avg_red & 0x000000FF) << 16)
						| ((avg_green & 0x000000FF) << 8) | ((avg_blue & 0x000000FF) << 0);
			}
			num_iterations++;
		}

		// update RGB array
		for (int i = 0; i < rgb.length; i++) {
			rgb[i] = k_values[pixel_assignments[i]];
		}

		System.out.println("Clustering image converged.");
		for (int i = 0; i < k_values.length; i++) {
			System.out.println("Final k mean " + i + ": " + k_values[i]);
		}
	}

	// HELPER FUNCTIONS - to get individual R, G, and B values
	public static int getRed(int pix) {
		return (pix >> 16) & 0xFF;
	}

	public static int getGreen(int pix) {
		return (pix >> 8) & 0xFF;
	}

	public static int getBlue(int pix) {
		return pix & 0xFF;
	}

	public static int getAlpha(int pix) {
		return (pix >> 24) & 0xFF;
	}
}