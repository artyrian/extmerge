package ru.artyrian;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BufferedIntReader {
	private final Scanner scanner;

	public BufferedIntReader(File file) throws FileNotFoundException {
		scanner = new Scanner(file);
	}

	public boolean hasNext() {
		return scanner.hasNextLine();
	}

	public Item next() {
		if (!hasNext()) {
			return null;
		}

		String line = scanner.nextLine();
		return new Item(line);
	}

}
