package com.example.gender_healthcare_service.entity.enumpackage;

/**
 * Healthcare Role System - Phân quyền dựa trên role hiện có
 * 
 * Hierarchy (từ thấp đến cao):
 * GUEST < CUSTOMER < CONSULTANT < STAFF < MANAGER < ADMIN
 */
public enum Role {
    // Level 0: Khách (chưa đăng ký)
    ROLE_GUEST("Khách", 0),
    
    // Level 1: Khách hàng/Bệnh nhân đã đăng ký
    ROLE_CUSTOMER("Khách hàng", 1),
    
    // Level 2: Tư vấn viên y tế
    ROLE_CONSULTANT("Tư vấn viên", 2),
    
    // Level 3: Nhân viên (bao gồm: y tá, kỹ thuật viên, lễ tân)
    ROLE_STAFF("Nhân viên", 3),
    
    // Level 4: Quản lý phòng ban
    ROLE_MANAGER("Quản lý", 4),
    
    // Level 5: Quản trị viên hệ thống
    ROLE_ADMIN("Quản trị viên", 5);
    
    private final String description;
    private final int level;
    
    Role(String description, int level) {
        this.description = description;
        this.level = level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getLevel() {
        return level;
    }
    
    /**
     * Kiểm tra quyền có cao hơn role khác không
     */
    public boolean hasHigherAuthorityThan(Role other) {
        return this.level > other.level;
    }
    
    /**
     * Kiểm tra quyền có bằng hoặc cao hơn role khác không
     */
    public boolean hasAuthorityLevel(Role requiredRole) {
        return this.level >= requiredRole.level;
    }
}
