package com.example.gender_healthcare_service.util;

import com.example.gender_healthcare_service.entity.enumpackage.MoodType;
import java.util.Arrays;
import java.util.List;

public class EnumUtils {
    
    /**
     * Chuyển đổi string thành MoodType một cách an toàn
     */
    public static MoodType safeMoodTypeFromString(String moodString) {
        if (moodString == null || moodString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return MoodType.valueOf(moodString.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Log error và trả về null nếu không tìm thấy enum
            System.err.println("Invalid mood type: " + moodString);
            return null;
        }
    }
    
    /**
     * Lấy danh sách tất cả các giá trị MoodType hợp lệ
     */
    public static List<String> getAllValidMoodTypes() {
        return Arrays.stream(MoodType.values())
                .map(MoodType::name)
                .toList();
    }
} 