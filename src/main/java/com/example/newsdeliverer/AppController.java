package com.example.newsdeliverer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class AppController {
    private final ChatGPTService chatGPTService;
    private final CsvReader csvReader;
    private List<String> cities;
    private List<String> news;

    @Autowired
    public AppController(ChatGPTService chatGPTService, CsvReader csvReader) {
        this.chatGPTService = chatGPTService;
        this.csvReader = csvReader;
        this.cities = CsvReader.getFixedListFromCsv("static/uscities.csv", "city");
        this.news = CsvReader.getFixedListFromCsv("static/articles.csv", "Article");
    }

    @GetMapping("/process-string")
    public String processString(@RequestParam String input) {
        String response = chatGPTService.analyzeNewsArticles(news);
        List<AbstractMap.SimpleEntry<String, String>> parsedResponse = parseStringToPairs(response);
        StringBuilder result = new StringBuilder();
        if (cities.contains(input)) {
            for (int i = 0; i < parsedResponse.size(); i++) {
                AbstractMap.SimpleEntry<String, String> pair = parsedResponse.get(i);
                if ("local".equals(pair.getKey()) && input.equals(pair.getValue())) {
                    result.append(news.get(i)).append("\n\n");
                }
            }
            if(result.isEmpty()){
                return "No news for this city.";
            }
            else{
                return result.toString();
            }
        } else {
            return "No city with this name";
        }
    }


    public static List<AbstractMap.SimpleEntry<String, String>> parseStringToPairs(String input) {
        List<AbstractMap.SimpleEntry<String, String>> pairs = new ArrayList<>();
        String[] tokens = input.split(" ");

        for (int i = 0; i < tokens.length; i++) {
            String locality = tokens[i];
            if (locality.equals("local") || locality.equals("global")) {
                if (i + 1 < tokens.length) {
                    StringBuilder cityName = new StringBuilder(tokens[i + 1]);
                    i++;
                    while (i + 1 < tokens.length && !tokens[i + 1].equals("local") && !tokens[i + 1].equals("global")) {
                        cityName.append(" ").append(tokens[i + 1]);
                        i++;
                    }
                    pairs.add(new AbstractMap.SimpleEntry<>(locality, cityName.toString().trim()));
                }
            }
        }
        return pairs;
    }
}
