package ru.artyrian;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtSort {

    public static void main(String[] args) {
        ExtSort.sort("resources/example1.csv", "resources/result.csv", 10);
    }

    public static void sort(final String pathInput, final String pathOutput, final int sizeLengthSplit) {
        Path file = Paths.get(pathInput);
        try (BufferedReader reader = Files.newBufferedReader(file);
             PrintWriter fileOut = new PrintWriter(pathOutput))
        {
            fileOut.write(reader.readLine().concat("\n"));   // write header

            List<BufferedIntReader> bufferedReaders = buildSwapFiles(reader, sizeLengthSplit);
            writeMerge(bufferedReaders, fileOut);
        } catch (IOException e) {
            System.err.println("some IOException on write result to file.");
        }
    }

    // read to tmp sorted files wrapped into buffer int readers
    private static List<BufferedIntReader> buildSwapFiles(final BufferedReader reader, final int lenSplit) throws IOException {
        List<BufferedIntReader> result = new ArrayList<>();

        List<Item> items = new ArrayList<>();
        while (true) {
            String line = reader.readLine();
            if (items.size() == lenSplit || line == null ) {
                BufferedIntReader bufferedIntReader = sortToBufferedReader(items);
                result.add(bufferedIntReader);
                items.clear();

                if (line == null) {
                    if (result.isEmpty()) {
                        throw new RuntimeException("empty input file");
                    }
                    break;
                }
            }

            items.add(new Item(line));
        }

        return result;
    }

    // create item array of min values from first values in buffered readers
    private static Item[] getMinItems(List<BufferedIntReader> bufferedReaders) {
        Item[] minItems = new Item[bufferedReaders.size()];

        for (int i = 0; i < bufferedReaders.size(); i++) {
            BufferedIntReader br = bufferedReaders.get(i);
            if (br.hasNext()) {
                minItems[i] = br.next();
            }
        }

        return minItems;
    }

    private static void writeMerge(List<BufferedIntReader> bufferedReaders, PrintWriter fileOut) {
        Item[] minItems = getMinItems(bufferedReaders);

        int emptyBuffer = 0;
        while (minItems.length > emptyBuffer) {
            int minIndex = 0;
            for (int i = 0; i < minItems.length; i++) {
                Item curItem = minItems[i];
                Item minItem = minItems[minIndex];
                if (curItem != null) {
                    if (minItem == null || curItem.getFid() < minItem.getFid()) {
                        minIndex = i;
                    }
                }
            }

            fileOut.write(minItems[minIndex].toString().concat("\n"));

            minItems[minIndex] = bufferedReaders.get(minIndex).next();
            if (minItems[minIndex] != null) {
                emptyBuffer++;
            }
        }

    }

    private static BufferedIntReader sortToBufferedReader(List<Item> items) throws IOException {
        Collections.sort(items, (t1, t2) -> t1.getFid() - t2.getFid());

        File tmp = File.createTempFile("_sort", ".tmp");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp.getAbsoluteFile()))) {
            for (Item item : items) {
                bw.write(item.toString() + "\n");
            }
        } catch (IOException e) {
            System.err.format("Tmp IOException: %s%n", e);
        } finally {
            items.clear();
        }

        return new BufferedIntReader(tmp);
    }
}
