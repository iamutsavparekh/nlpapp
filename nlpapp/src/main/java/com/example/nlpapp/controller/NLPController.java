package com.example.nlpapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class NLPController {

    private static final String AGGREGATED_STATS_PATH = "nlpapp\\src\\main\\resources\\aggregated_results.txt";

    @GetMapping("/")
    public String index() {
        return "index"; // Return the name of the Thymeleaf template
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            return "index";
        }

        try {
            // Convert uploaded file to text
            String text = new String(file.getBytes());

            // Calculate NLP statistics
            Map<String, Double> nlpStats = calculateNLPStatistics(text);

            // Load aggregated results
            Map<String, Double> aggregatedStats = loadAggregatedResults();

            // Compare the statistics
            Map<String, Double> comparison = compareStatistics(nlpStats, aggregatedStats);

            // Add data to model
            model.addAttribute("nlpStats", nlpStats);
            model.addAttribute("aggregatedStats", aggregatedStats);
            model.addAttribute("comparison", comparison);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Failed to process the file.");
        }

        return "result"; // Return the name of the Thymeleaf template
    }

    private Map<String, Double> calculateNLPStatistics(String text) {
        // Implement NLP statistics calculations here (similar to Python)
        // For demonstration, this example returns some dummy values
        Map<String, Double> stats = new HashMap<>();
        stats.put("word_count", (double) text.split("\\s+").length);
        stats.put("sentence_count", (double) text.split("[.!?]").length);
        stats.put("avg_word_length", text.chars().filter(c -> c != ' ').count() / (double) text.split("\\s+").length);
        stats.put("unique_words", (double) text.split("\\s+").length); // Placeholder; needs unique word count logic
        stats.put("stopwords_count", 0.0); // Implement actual stopwords counting logic
        return stats;
    }

    private Map<String, Double> loadAggregatedResults() throws IOException {
        Map<String, Double> aggregatedStats = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(AGGREGATED_STATS_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    aggregatedStats.put(parts[0].trim(), Double.parseDouble(parts[1].trim()));
                }
            }
        }
        return aggregatedStats;
    }

    private Map<String, Double> compareStatistics(Map<String, Double> nlpStats, Map<String, Double> aggregatedStats) {
        Map<String, Double> comparison = new HashMap<>();
        for (String key : nlpStats.keySet()) {
            if (aggregatedStats.containsKey(key)) {
                comparison.put(key, nlpStats.get(key) - aggregatedStats.get(key));
            }
        }
        return comparison;
    }
}
