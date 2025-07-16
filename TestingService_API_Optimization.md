# TestingService API Optimization

## 🎯 Tối ưu hóa Response cho deleteService

### Vấn đề ban đầu
- Hàm `deleteService` trả về `void` 
- API chỉ trả về message đơn giản
- Không có thông tin chi tiết về operation
- Khó debug và track

### Giải pháp tối ưu

#### 1. **TestingServiceResponseDTO Enhanced**
```java
@Data
public class TestingServiceResponseDTO {
    private Long serviceId;
    private String serviceName;
    private String description;
    private BigDecimal price;
    private String status;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    
    // Additional fields for better response
    private String message;
    private String operation;
    private LocalDateTime operationTime;
}
```

#### 2. **Service Interface Updated**
```java
public interface TestingServiceService {
    // Thay đổi từ void sang TestingServiceResponseDTO
    TestingServiceResponseDTO deleteService(Integer id, boolean isDeleted);
}
```

#### 3. **Service Implementation Enhanced**
```java
@Override
public TestingServiceResponseDTO deleteService(Integer id, boolean isDeleted) {
    TestingService service = getServiceById(id);
    service.setIsDeleted(isDeleted);
    service.setUpdatedAt(LocalDateTime.now());
    TestingService savedService = testingServiceRepository.save(service);
    
    // Tạo response DTO với thông tin chi tiết
    TestingServiceResponseDTO responseDTO = modelMapper.map(savedService, TestingServiceResponseDTO.class);
    responseDTO.setMessage(isDeleted ? "Service deleted successfully" : "Service restored successfully");
    responseDTO.setOperation(isDeleted ? "DELETE" : "RESTORE");
    responseDTO.setOperationTime(LocalDateTime.now());
    
    return responseDTO;
}
```

#### 4. **ApiResponse Generic**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String operation;
    
    public static <T> ApiResponse<T> success(String message, T data, String operation) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), operation);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null);
    }
}
```

### API Endpoints

#### 1. **Delete Service**
```http
DELETE /api/admin/testing-services/{serviceId}
```

**Response Success:**
```json
{
  "success": true,
  "message": "Service deleted successfully",
  "data": {
    "serviceId": 1,
    "serviceName": "Blood Test",
    "description": "Complete blood count",
    "price": 150.00,
    "status": "ACTIVE",
    "durationMinutes": 30,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T11:45:00",
    "isDeleted": true,
    "message": "Service deleted successfully",
    "operation": "DELETE",
    "operationTime": "2024-01-15T11:45:00"
  },
  "timestamp": "2024-01-15T11:45:00",
  "operation": "DELETE"
}
```

**Response Error:**
```json
{
  "success": false,
  "message": "Service not found with ID: 999",
  "data": null,
  "timestamp": "2024-01-15T11:45:00",
  "operation": null
}
```

#### 2. **Restore Service**
```http
PUT /api/admin/testing-services/{serviceId}/restore
```

**Response Success:**
```json
{
  "success": true,
  "message": "Service restored successfully",
  "data": {
    "serviceId": 1,
    "serviceName": "Blood Test",
    "description": "Complete blood count",
    "price": 150.00,
    "status": "ACTIVE",
    "durationMinutes": 30,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T11:50:00",
    "isDeleted": false,
    "message": "Service restored successfully",
    "operation": "RESTORE",
    "operationTime": "2024-01-15T11:50:00"
  },
  "timestamp": "2024-01-15T11:50:00",
  "operation": "RESTORE"
}
```

### Lợi ích của tối ưu hóa

#### 1. **Thông tin chi tiết**
- Biết chính xác service nào bị xóa/khôi phục
- Thời gian operation
- Trạng thái trước và sau

#### 2. **Debug dễ dàng**
- Log đầy đủ thông tin
- Track operation history
- Audit trail

#### 3. **Frontend friendly**
- Response structured rõ ràng
- Success/error handling dễ dàng
- UI có thể hiển thị thông tin chi tiết

#### 4. **API consistency**
- Tất cả endpoints đều dùng ApiResponse
- Standardized error handling
- Consistent response format

### Controller Implementation

```java
@DeleteMapping("/testing-services/{serviceId}")
public ResponseEntity<ApiResponse<TestingServiceResponseDTO>> deleteTestingService(@PathVariable Integer serviceId) {
    try {
        TestingServiceResponseDTO response = testingServiceService.deleteService(serviceId, true);
        ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.success(
            "Service deleted successfully", 
            response, 
            "DELETE"
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    } catch (ServiceNotFoundException e) {
        ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.error("Service not found with ID: " + serviceId);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
        logger.error("Error deleting testing service: {}", e.getMessage(), e);
        ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.error("Failed to delete service");
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### Testing

#### Unit Test Example
```java
@Test
public void testDeleteService() {
    // Arrange
    Integer serviceId = 1;
    boolean isDeleted = true;
    
    // Act
    TestingServiceResponseDTO response = testingServiceService.deleteService(serviceId, isDeleted);
    
    // Assert
    assertNotNull(response);
    assertEquals("Service deleted successfully", response.getMessage());
    assertEquals("DELETE", response.getOperation());
    assertTrue(response.getIsDeleted());
    assertNotNull(response.getOperationTime());
}
```

### Migration Guide

#### 1. **Update Service Interface**
```java
// Before
void deleteService(Integer id, boolean isDeleted);

// After  
TestingServiceResponseDTO deleteService(Integer id, boolean isDeleted);
```

#### 2. **Update Service Implementation**
```java
// Before
public void deleteService(Integer id, boolean isDeleted) {
    TestingService service = getServiceById(id);
    service.setIsDeleted(isDeleted);
    service.setUpdatedAt(LocalDateTime.now());
    testingServiceRepository.save(service);
}

// After
public TestingServiceResponseDTO deleteService(Integer id, boolean isDeleted) {
    TestingService service = getServiceById(id);
    service.setIsDeleted(isDeleted);
    service.setUpdatedAt(LocalDateTime.now());
    TestingService savedService = testingServiceRepository.save(service);
    
    TestingServiceResponseDTO responseDTO = modelMapper.map(savedService, TestingServiceResponseDTO.class);
    responseDTO.setMessage(isDeleted ? "Service deleted successfully" : "Service restored successfully");
    responseDTO.setOperation(isDeleted ? "DELETE" : "RESTORE");
    responseDTO.setOperationTime(LocalDateTime.now());
    
    return responseDTO;
}
```

#### 3. **Update Controller**
```java
// Before
@DeleteMapping("/testing-services/{serviceId}")
public ResponseEntity<?> deleteTestingService(@PathVariable Integer serviceId) {
    testingServiceService.deleteService(serviceId, true);
    return new ResponseEntity<>("Service delete successfully", HttpStatus.OK);
}

// After
@DeleteMapping("/testing-services/{serviceId}")
public ResponseEntity<ApiResponse<TestingServiceResponseDTO>> deleteTestingService(@PathVariable Integer serviceId) {
    try {
        TestingServiceResponseDTO response = testingServiceService.deleteService(serviceId, true);
        ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.success(
            "Service deleted successfully", 
            response, 
            "DELETE"
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    } catch (ServiceNotFoundException e) {
        ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.error("Service not found with ID: " + serviceId);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
        logger.error("Error deleting testing service: {}", e.getMessage(), e);
        ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.error("Failed to delete service");
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### Kết luận

Tối ưu hóa này mang lại:
- ✅ Response chi tiết và có cấu trúc
- ✅ Error handling tốt hơn
- ✅ Debug và tracking dễ dàng
- ✅ Frontend integration thuận lợi
- ✅ API consistency
- ✅ Audit trail đầy đủ 