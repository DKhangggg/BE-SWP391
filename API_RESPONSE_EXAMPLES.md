# API Response Examples

## Blog Categories API

### GET /api/blog/categories

**Before (Entity Response):**
```json
[
  {
    "categoryID": 1,
    "categoryName": "Sức khỏe phụ nữ",
    "description": "Các bài viết về sức khỏe phụ nữ",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "isDeleted": false,
    "blogPosts": [
      {
        "postID": 1,
        "title": "Bài viết 1",
        "content": "Nội dung dài...",
        "author": {
          "id": 1,
          "fullName": "Admin",
          "email": "admin@example.com",
          // ... many more fields
        },
        "categories": [
          // Circular reference causing issues
        ],
        // ... many more fields
      }
      // ... more posts
    ]
  }
  // ... more categories
]
```

**After (DTO Response):**
```json
[
  {
    "categoryID": 1,
    "categoryName": "Sức khỏe phụ nữ",
    "description": "Các bài viết về sức khỏe phụ nữ"
  },
  {
    "categoryID": 2,
    "categoryName": "Dinh dưỡng",
    "description": "Các bài viết về dinh dưỡng"
  }
]
```

### GET /api/blog/categories/{categoryId}

**Response:**
```json
{
  "categoryID": 1,
  "categoryName": "Sức khỏe phụ nữ",
  "description": "Các bài viết về sức khỏe phụ nữ"
}
```

### POST /api/blog/categories

**Request:**
```json
{
  "name": "Tâm lý học",
  "description": "Các bài viết về tâm lý học"
}
```

**Response:**
```json
{
  "categoryID": 3,
  "categoryName": "Tâm lý học",
  "description": "Các bài viết về tâm lý học"
}
```

### PUT /api/blog/categories/{categoryId}

**Request:**
```json
{
  "name": "Tâm lý học cập nhật",
  "description": "Các bài viết về tâm lý học - đã cập nhật"
}
```

**Response:**
```json
{
  "categoryID": 3,
  "categoryName": "Tâm lý học cập nhật",
  "description": "Các bài viết về tâm lý học - đã cập nhật"
}
```

### DELETE /api/blog/categories/{categoryId}

**Response:** 204 No Content (empty body)

## Benefits of the Changes

1. **Reduced Response Size**: Loại bỏ các field không cần thiết như timestamps, isDeleted, và quan hệ phức tạp
2. **No Circular References**: Tránh vòng lặp serialization giữa BlogCategory và BlogPost
3. **Better Performance**: Ít dữ liệu truyền tải, tăng tốc độ API
4. **Cleaner Frontend Code**: Frontend chỉ nhận những gì cần thiết
5. **Type Safety**: Rõ ràng về kiểu dữ liệu trả về

## Frontend Usage

```javascript
// Before: Had to handle complex nested objects
const categories = response.data; // Complex structure with posts, author, etc.

// After: Simple, clean data
const categories = response.data; // Just id, name, description
categories.forEach(category => {
  console.log(category.categoryID, category.categoryName);
});
```
