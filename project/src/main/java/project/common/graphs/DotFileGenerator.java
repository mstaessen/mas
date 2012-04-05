package project.common.graphs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class DotFileGenerator {
	private final int width;
	private final int height;
	private final File outputFile;
	private static final String BASE_PATH = "files/maps/";
	private static final int DEFAULT_WIDTH = 10;
	private static final int DEFAULT_HEIGHT = 10;
	private static final int NODE_DISTANCE = 500;
	private static final int EDGE_MAX_SPEED = 300;

	public DotFileGenerator(int width, int height, File outputFile) {
		this.width = width;
		this.height = height;
		this.outputFile = outputFile;
	}

	private void generate() throws FileNotFoundException {
		final PrintWriter writer = new PrintWriter(outputFile);

		System.out.println("Generating a " + width + "x" + height + " grid...");

		writer.println("digraph mapgraph {");
		writeNodes(writer);
		writeEdges(writer);
		writer.println("}");
		writer.flush();

		System.out.println(width + "x" + height + " grid generated.");
	}

	private void writeNodes(PrintWriter writer) {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				writer.print(getNodeName(row, col));
				writer.print("[p=\"");
				writer.print(col * NODE_DISTANCE);
				writer.print(",");
				writer.print(row * NODE_DISTANCE);
				writer.println("\"]");
			}
		}
	}

	private void writeEdges(PrintWriter writer) {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				// there is a node in the north
				if (row - 1 >= 0) {
					writer.println(getEdge(row, col, row - 1, col));
				}

				// there is a node in the east
				if (col + 1 < width) {
					writer.println(getEdge(row, col, row, col + 1));
				}

				// there is a node in the south
				if (row + 1 < height) {
					writer.println(getEdge(row, col, row + 1, col));
				}

				// there is a node in the west
				if (col - 1 >= 0) {
					writer.println(getEdge(row, col, row, col - 1));
				}
			}
		}
	}

	private String getEdge(int fromRow, int fromCol, int toRow, int toCol) {
		return getNodeName(fromRow, fromCol) + " -> " + getNodeName(toRow, toCol) + "[d=\""
				+ calculateDistance(fromRow, fromCol, toRow, toCol) + "\", s=\"" + EDGE_MAX_SPEED + "\"]";
	}

	private int calculateDistance(int fromRow, int fromCol, int toRow, int toCol) {
		return (Math.abs(fromRow - toRow) + Math.abs(fromCol - toCol)) * NODE_DISTANCE;
	}

	private String getNodeName(int row, int col) {
		return "n" + row + "_" + col;
	}

	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		int width = promptInt(reader, "Width?", DEFAULT_WIDTH);
		int height = promptInt(reader, "Height?", DEFAULT_HEIGHT);
		File file = promptFile(reader, "Target file?", generateFileName(width, height));

		DotFileGenerator generator = new DotFileGenerator(width, height, file);
		try {
			generator.generate();
		} catch (FileNotFoundException e) {
			System.err.println("Grid creation failed.");
		}
	}

	private static File generateFileName(int width, int height) {
		return new File(BASE_PATH + "grid-" + width + "x" + height + ".dot");
	}

	private static int promptInt(BufferedReader reader, String message, int dflt) {
		int value = dflt;
		try {
			printPrompt(message, String.valueOf(dflt));
			String input = reader.readLine();
			if (!input.isEmpty()) {
				value = Integer.parseInt(input);
			}
			if (value < 1) {
				synchronized (System.err) {
					System.err.println("Value must be strictly positive. Try again.");
					return promptInt(reader, message, dflt);
				}
			}
		} catch (IOException e) {
			synchronized (System.out) {
				System.err.println("The value given is not an integer. Try again.");
				return promptInt(reader, message, dflt);
			}
		} catch (NumberFormatException e) {
			synchronized (System.out) {
				System.err.println("The value given is not an integer. Try again.");
				return promptInt(reader, message, dflt);
			}
		}
		return value;
	}

	private static void printPrompt(String message, String dflt) {
		System.out.print(message);
		System.out.print(" [");
		System.out.print(dflt);
		System.out.println("]");
		System.out.print("> ");
	}

	private static File promptFile(BufferedReader reader, String message, File dflt) {
		File file = dflt;
		try {
			printPrompt(message, dflt.toString());
			String input = reader.readLine();
			if (!input.isEmpty()) {
				file = new File(input);
			}
			if (file.isDirectory()) {
				synchronized (System.err) {
					System.err.println("File is a directory. Try again.");
					return promptFile(reader, message, dflt);
				}
			}
		} catch (IOException e) {
			synchronized (System.out) {
				System.err.println("IO Error. Try again.");
				return promptFile(reader, message, dflt);
			}
		}
		return file;
	}

}
