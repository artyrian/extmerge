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
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String header = reader.readLine();

            List<Item> items = new ArrayList<>();
            List<BufferedIntReader> bufferedReaders = new ArrayList<>();
            int totalCount = 0;

            // read to tmp sorted files wrapped into buffer int readers
            while (true) {
                String line = reader.readLine();
                if (items.size() == sizeLengthSplit || line == null ) {
                    BufferedIntReader bufferedIntReader = sortToBufferedReader(items);
                    bufferedReaders.add(bufferedIntReader);
                    items.clear();

                    if (line == null) {
                        if (bufferedReaders.isEmpty()) {
                            throw new RuntimeException("empty input file");
                        }
                        break;
                    }
                }

                items.add(new Item(line));
                totalCount++;
            }

            // create item array of min values from first values in buffered readers
            Item[] minItems = new Item[bufferedReaders.size()];
            for (int i = 0; i < bufferedReaders.size(); i++) {
                BufferedIntReader br = bufferedReaders.get(i);
                if (br.hasNext()) {
                    minItems[i] = br.next();
                }
            }

            // write to output file next min value from item array as merge alg
            try (PrintWriter fileResult = new PrintWriter(pathOutput)) {
                fileResult.write(header.concat("\n"));
                while (totalCount > 0) {
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

                    Item minItem = minItems[minIndex];
                    fileResult.write(minItem.toString().concat("\n"));
                    minItems[minIndex] = bufferedReaders.get(minIndex).next();
                    totalCount--;
                }
            } catch (IOException e) {
                System.err.println("some IOException on write result to file.");
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
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
