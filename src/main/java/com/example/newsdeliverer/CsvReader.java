package com.example.newsdeliverer;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CsvReader {

    public static List<String> getFixedListFromCsv(String inputCsv, String columnName) {
        List<String> values = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(new ClassPathResource(inputCsv).getInputStream())))) {
            String[] header = reader.readNext();  // Read the header

            // Find the index of the specified column
            int columnIndex = -1;
            for (int i = 0; i < header.length; i++) {
                if (header[i].equals(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                throw new RuntimeException("Column '" + columnName + "' not found in the CSV file.");
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (values.size() < 200) {
                    values.add(nextLine[columnIndex]);
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return values;
    }

}
