package com.example.online_food.Util;

public class StringUtils {
    public static String removeDiacritics(String str) {
        //input "Gà rán"
        //output "ga ran"

        if (str == null) return null;
        // Normalize the string to decompose combined characters (e.g., "ở" → "o" + combining mark)
        String normalized = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        // Remove diacritic marks and handle special Vietnamese character "đ"
        return normalized.replaceAll("\\p{M}", "").replace("đ", "d").replace("Đ", "D").toLowerCase();
    }


    public static String normalizeTK(String str) {
        if (str == null) return null;
        String normalized = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "").replace("đ", "d").replace("Đ", "D");
        return withoutDiacritics.replaceAll("\\s+", " ").trim().toLowerCase();  // ép về lowercase
    }

}
