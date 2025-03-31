package edu.ycp.cs320.tbag.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {
    /**
     * Loads a CSV file with the given delimiter.
     *
     * @param filePath the full path to the CSV file
     * @param delimiterRegex the regular expression for the delimiter (e.g., "\\|" for a pipe)
     * @return a List of String arrays, where each array represents a row of data
     * @throws IOException if there is an error reading the file
     */
    public static List<String[]> loadCSV(String filePath, String delimiterRegex) throws IOException {
        List<String[]> records = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Split the line using the provided delimiter regex
                String[] values = line.split(delimiterRegex);
                // Trim whitespace from each field
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }
                records.add(values);
            }
        } finally {
            br.close();
        }
        return records;
    }
}
