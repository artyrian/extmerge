package ru.artyrian;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ExtSort {


    public static void main(String[] args) {
        final int SWAP_LINES = 3;

        ExtSort.sort("resources/example1.csv", "resources/result.csv", SWAP_LINES);
    }

    public static void sort(final String pathInput, final String pathOutput, final int swapLinesSize) {
        try (Scanner scanner = new Scanner(new File(pathInput));
             PrintWriter fileOut = new PrintWriter(pathOutput))
        {
            fileOut.write(scanner.nextLine().concat("\n"));   // write header

            List<BufferedIntReader> bufferedReaders = buildSwapFiles(scanner, swapLinesSize);
            writeMerge(bufferedReaders, fileOut);
        } catch (IOException e) {
            System.err.println("some IOException on write result to file.");
        }
    }

    // write to swap files wrapped into bufferIntReader sorted values
    private static List<BufferedIntReader> buildSwapFiles(final Scanner scanner, final int swapLinesSize) throws IOException {
        List<BufferedIntReader> result = new ArrayList<>();

        List<Item> items = new ArrayList<>();
        while (true) {
            if (items.size() == swapLinesSize || !scanner.hasNextLine() ) {
                Collections.sort(items, (t1, t2) -> t1.getFid() - t2.getFid());
                File swap = writeToSwap(items);
                result.add(new BufferedIntReader(swap));
                items.clear();

                if (!scanner.hasNextLine()) {
                    if (result.isEmpty()) {
                        throw new RuntimeException("empty input file");
                    }
                    break;
                }
            }

            String line = scanner.nextLine();
            items.add(new Item(line));
        }

        final int bufferedLinesSize = swapLinesSize / result.size();
        for (BufferedIntReader br : result) {
            br.setQueueSize(bufferedLinesSize);
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
            if (minItems[minIndex] == null) {
                emptyBuffer++;
            }
        }

    }

    private static File writeToSwap(List<Item> items) throws IOException {
        File tmp = File.createTempFile("_sort", ".tmp");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp.getAbsoluteFile()))) {
            for (Item item : items) {
                bw.write(item.toString() + "\n");
            }
        } catch (IOException e) {
            System.err.format("Tmp IOException: %s%n", e);
        }

        return tmp;
    }
}
