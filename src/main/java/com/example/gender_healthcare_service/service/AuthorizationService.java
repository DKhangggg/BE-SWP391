package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.entity.enumpackage.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Service quản lý phân quyền cho hệ thống Healthcare
 * Định nghĩa rõ ràng ai có thể làm gì
 */
@Service
public class AuthorizationService {

    /**
     * Kiểm tra user có role cụ thể không
     */
    public boolean hasRole(Authentication auth, Role role) {
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        
        return auth.getAuthorities().stream()
                .anyMatch(authority -> 
                    authority.getAuthority().equals(role.name()) ||
                    authority.getAuthority().equals("ROLE_" + role.name().substring(5))
                );
    }

    /**
     * Kiểm tra user có ít nhất một trong các role không
     */
    public boolean hasAnyRole(Authentication auth, Role... roles) {
        for (Role role : roles) {
            if (hasRole(auth, role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra user có quyền cấp cao hơn hoặc bằng role yêu cầu không
     */
    public boolean hasMinimumRole(Authentication auth, Role minimumRole) {
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }

        Role userRole = getCurrentUserRole(auth);
        return userRole != null && userRole.hasAuthorityLevel(minimumRole);
    }

    /**
     * Lấy role cao nhất của user hiện tại
     */
    public Role getCurrentUserRole(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) {
            return Role.ROLE_GUEST;
        }

        Role highestRole = Role.ROLE_GUEST;
        
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String roleName = authority.getAuthority();
            
            // Xử lý cả ROLE_XXX và XXX format
            if (roleName.startsWith("ROLE_")) {
                roleName = roleName.substring(5);
            }
            
            try {
                Role role = Role.valueOf("ROLE_" + roleName);
                if (role.hasHigherAuthorityThan(highestRole)) {
                    highestRole = role;
                }
            } catch (IllegalArgumentException e) {
                // Ignore invalid roles
            }
        }
        
        return highestRole;
    }

    // ========== BUSINESS LOGIC PERMISSIONS ==========

    /**
     * Có thể xem thông tin bệnh nhân không
     */
    public boolean canViewPatientInfo(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_CONSULTANT);
    }

    /**
     * Có thể chỉnh sửa thông tin bệnh nhân không
     */
    public boolean canEditPatientInfo(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_STAFF);
    }

    /**
     * Có thể quản lý booking không
     */
    public boolean canManageBookings(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_STAFF);
    }

    /**
     * Có thể xem báo cáo tài chính không
     */
    public boolean canViewFinancialReports(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_MANAGER);
    }

    /**
     * Có thể quản lý tư vấn viên không
     */
    public boolean canManageConsultants(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_MANAGER);
    }

    /**
     * Có thể quản lý dịch vụ xét nghiệm không
     */
    public boolean canManageTestingServices(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_MANAGER);
    }

    /**
     * Có thể cập nhật kết quả xét nghiệm không
     */
    public boolean canUpdateTestResults(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_STAFF);
    }

    /**
     * Có thể xem dashboard admin không
     */
    public boolean canViewAdminDashboard(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_MANAGER);
    }

    /**
     * Có thể quản lý người dùng không
     */
    public boolean canManageUsers(Authentication auth) {
        return hasRole(auth, Role.ROLE_ADMIN);
    }

    /**
     * Có thể xem blog và nội dung công khai không
     */
    public boolean canViewPublicContent(Authentication auth) {
        return true; // Tất cả mọi người có thể xem
    }

    /**
     * Có thể tạo/chỉnh sửa blog không
     */
    public boolean canManageBlog(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_CONSULTANT);
    }

    /**
     * Có thể trả lời Q&A không
     */
    public boolean canAnswerQuestions(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_CONSULTANT);
    }

    /**
     * Có thể đặt lịch tư vấn không
     */
    public boolean canBookConsultation(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_CUSTOMER);
    }

    /**
     * Có thể theo dõi chu kỳ kinh nguyệt không
     */
    public boolean canTrackMenstrualCycle(Authentication auth) {
        return hasMinimumRole(auth, Role.ROLE_CUSTOMER);
    }
} 