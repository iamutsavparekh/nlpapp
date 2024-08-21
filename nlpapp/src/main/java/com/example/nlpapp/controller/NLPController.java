package com.example.nlpapp.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling NLP-related operations.
 * Provides endpoints for file upload and NLP statistics calculation.
 */
@Controller
public class NLPController {

    private static final Logger logger = LoggerFactory.getLogger(NLPController.class);
    private static final String AGGREGATED_STATS_PATH = "aggregated_results.txt";

    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    // Fallback method to handle GET requests to /upload
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String handleGetUpload() {
        return "redirect:/"; // Redirect to the home page
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        Map<String, Double> nlpStats = new HashMap<>();
        Map<String, Double> aggregatedStats = new HashMap<>();
        Map<String, Double> comparison = new HashMap<>();

        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            return "index";
        }

        try {
            String text = new String(file.getBytes());
            logger.info("Uploaded text: " + text);

            nlpStats = calculateNLPStatistics(text);
            logger.info("NLP Stats: " + nlpStats);

            aggregatedStats = loadAggregatedResults();
            logger.info("Aggregated Stats: " + aggregatedStats);

            comparison = compareStatistics(nlpStats, aggregatedStats);
            logger.info("Comparison: " + comparison);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Failed to process the file.");
            return "index";
        }

        model.addAttribute("nlpStats", nlpStats);
        model.addAttribute("aggregatedStats", aggregatedStats);
        model.addAttribute("comparison", comparison);

        return "result";
    }

    private Map<String, Double> calculateNLPStatistics(String text) {
        Map<String, Double> stats = new HashMap<>();
        stats.put("avg_word_count", (double) text.split("\\s+").length);
        stats.put("avg_sentence_count", (double) text.split("[.!?]").length);
        stats.put("avg_word_length", (double) text.chars().filter(c -> c != ' ').count() / text.split("\\s+").length);
        stats.put("avg_unique_words", (double) text.split("\\s+").length); // Placeholder: needs unique word count logic
        stats.put("avg_stopwords_count", 0.0); // Placeholder: implement actual stopwords counting logic
        return stats;
    }

    private Map<String, Double> loadAggregatedResults() throws IOException {
        Map<String, Double> aggregatedStats = new HashMap<>();
        ClassPathResource resource = new ClassPathResource(AGGREGATED_STATS_PATH);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
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
