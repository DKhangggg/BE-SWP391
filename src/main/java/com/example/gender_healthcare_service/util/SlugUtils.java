package com.example.gender_healthcare_service.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtils {
    
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");
    
    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        
        return slug.toLowerCase();
    }
    
    public static String generateUniqueSlug(String input, String existingSlug) {
        String baseSlug = generateSlug(input);
        if (existingSlug == null || existingSlug.isEmpty()) {
            return baseSlug;
        }
        
        // If the generated slug is different from existing, return it
        if (!baseSlug.equals(existingSlug)) {
            return baseSlug;
        }
        
        // If same, add timestamp to make it unique
        return baseSlug + "-" + System.currentTimeMillis();
    }
} 