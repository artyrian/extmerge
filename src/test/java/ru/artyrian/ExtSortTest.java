package ru.artyrian;


import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class ExtSortTest {
	private enum GenerationType {
		RANDOM, INC, DEC, EQ
	}

	private final String resultTestPath = "resources/test_result.csv";

	@Test
	public void testSorted() {
		Assert.assertTrue(ifFileSorted("resources/sorted.csv"));
	}

	@Test
	public void testUnsorted() {
		Assert.assertFalse(ifFileSorted("resources/example1.csv"));
	}

	@Test
	public void testRandom() throws IOException {
		String absPath = generateItemsFile(100, GenerationType.RANDOM);
		Assert.assertFalse(ifFileSorted(absPath));
	}

	@Test
	public void testInc() throws IOException {
		String absPath = generateItemsFile(100, GenerationType.INC);
		Assert.assertTrue(ifFileSorted(absPath));

		ExtSort.sort(absPath, resultTestPath, 10);
		Assert.assertTrue(ifFileSorted(resultTestPath));
	}


	@Test
	public void testEq() throws IOException {
		String absPath = generateItemsFile(100, GenerationType.EQ);
		Assert.assertTrue(ifFileSorted(absPath));

		ExtSort.sort(absPath, resultTestPath, 10);
		Assert.assertTrue(ifFileSorted(resultTestPath));
	}

	@Test
	public void testDec() throws IOException {
		String absPath = generateItemsFile(100, GenerationType.DEC);
		Assert.assertFalse(ifFileSorted(absPath));

		ExtSort.sort(absPath, resultTestPath, 10);
		Assert.assertTrue(ifFileSorted(resultTestPath));
	}



	private String generateItemsFile(final int numLines, GenerationType type) throws IOException {
		File tmpOutput = File.createTempFile("_tmp", ".sort");

		Random random = new Random();
		try (PrintWriter fileOutput = new PrintWriter(tmpOutput)) {
			fileOutput.write("header".concat("\n"));
			for (int count = 0; count < numLines; count++) {
				Integer fid;
				switch (type) {
					case RANDOM:
						fid = random.nextInt();
						break;
					case EQ:
						fid = 1;
						break;
					case INC:
						fid = count;
						break;
					case DEC:
						fid = numLines - count;
						break;
					default:
						throw new RuntimeException("unknown generation type");
				}
				Item item = new Item(fid + "; xxx; yyy");
				fileOutput.write(item.toString().concat("\n"));
			}
		} catch (IOException e) {
			System.err.println("some IOException on write result to file.");
		}

		return tmpOutput.getAbsolutePath();
	}

	private static boolean ifFileSorted(String path) {
		Path file = Paths.get(path);
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			reader.readLine(); // header;

			Integer previousFid = null;
			while (true) {
				String line = reader.readLine();
				if (line == null ) {
						break;
				}
				Item item = new Item(line);
				int currentFid = item.getFid();
				if (previousFid != null && previousFid > currentFid) {
					return false;
				}
				previousFid = currentFid;
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}

		return true;
	}
}
