package ru.artyrian;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;

public class BufferedIntReader {
	private final Scanner scanner;

	private Deque<String> queue;
	private int queueSize;

	BufferedIntReader(File file) throws FileNotFoundException {
		scanner = new Scanner(file);
		queue = new LinkedList<>();
	}

	void setQueueSize(final int queueSize) {
		this.queueSize = queueSize;
	}

	boolean hasNext() {
		return !queue.isEmpty() || scanner.hasNext();
	}

	Item next() {
		if (queue.isEmpty()) {
			int i = 0;
			while (i <= queueSize && scanner.hasNextLine()) {
				queue.add(scanner.nextLine());
				i++;
			}
		}

		return hasNext() ? new Item(queue.removeFirst()) : null;
	}

}
