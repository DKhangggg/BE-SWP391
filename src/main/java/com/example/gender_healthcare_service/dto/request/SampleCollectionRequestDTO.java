package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleCollectionRequestDTO {
    
    @NotBlank(message = "Họ tên người lấy mẫu không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String collectorFullName;
    
    @NotBlank(message = "Số CCCD/CMND không được để trống")
    @Size(max = 20, message = "Số CCCD/CMND không được vượt quá 20 ký tự")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CCCD/CMND phải từ 9-12 chữ số")
    private String collectorIdCard;
    
    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Số điện thoại không hợp lệ")
    private String collectorPhoneNumber;
    
    @NotBlank(message = "Mối quan hệ với người đặt lịch không được để trống")
    @Pattern(regexp = "^(SELF|FAMILY_MEMBER|FRIEND|OTHER)$",
             message = "Mối quan hệ phải là một trong: SELF, FAMILY_MEMBER, FRIEND, OTHER")
    private String relationshipToBooker;

    private LocalDate collectorDateOfBirth;

    @Size(max = 10, message = "Giới tính không được vượt quá 10 ký tự")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$",
             message = "Giới tính phải là một trong: MALE, FEMALE, OTHER")
    private String collectorGender;

    @NotNull(message = "Thời gian lấy mẫu không được để trống")
    private LocalDateTime sampleCollectionDate;
    
    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    private String notes;
    
    // Helper methods for validation
    public boolean isSelf() {
        return "SELF".equals(relationshipToBooker);
    }
    
    public boolean isFamilyMember() {
        return "FAMILY_MEMBER".equals(relationshipToBooker);
    }
    
    public boolean isFriend() {
        return "FRIEND".equals(relationshipToBooker);
    }
    
    public boolean isOther() {
        return "OTHER".equals(relationshipToBooker);
    }

    // Gender helper methods
    public boolean isMale() {
        return "MALE".equals(collectorGender);
    }

    public boolean isFemale() {
        return "FEMALE".equals(collectorGender);
    }

    public boolean isOtherGender() {
        return "OTHER".equals(collectorGender);
    }

    // Validation helper
    public boolean isValidRelationship() {
        return isSelf() || isFamilyMember() || isFriend() || isOther();
    }

    public boolean isValidGender() {
        return collectorGender == null || isMale() || isFemale() || isOtherGender();
    }
    
    // Display name for relationship
    public String getRelationshipDisplayName() {
        switch (relationshipToBooker) {
            case "SELF":
                return "Chính chủ";
            case "FAMILY_MEMBER":
                return "Người nhà";
            case "FRIEND":
                return "Bạn bè";
            case "OTHER":
                return "Khác";
            default:
                return relationshipToBooker;
        }
    }

    // Display name for gender
    public String getGenderDisplayName() {
        if (collectorGender == null) return "";
        switch (collectorGender) {
            case "MALE":
                return "Nam";
            case "FEMALE":
                return "Nữ";
            case "OTHER":
                return "Khác";
            default:
                return collectorGender;
        }
    }
}
