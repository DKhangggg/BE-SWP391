# 🛠️ CORS Fix Guide

## ✅ Đã Fix CORS cho bạn!

### 🔧 Những gì đã được thêm:

1. **CorsConfig.java** - Configuration CORS hoàn chỉnh
2. **SecurityConfig.java** - Tích hợp CORS vào security
3. **Support multiple ports**: 3000, 5173, 4000

### 🚀 Cách khởi động lại Backend:

#### 1. **Trong IntelliJ IDEA:**
```
1. Stop ứng dụng hiện tại (click Stop button đỏ)
2. Chờ 2-3 giây
3. Click Run (hoặc Shift+F10)
4. Đợi console hiện: "Started GenderHealthcareServiceApplication"
```

#### 2. **Hoặc dùng Maven command:**
```bash
cd gender-healthcare-service
mvn spring-boot:run
```

### ✅ Test CORS fix:

#### **Option 1: Trong Browser Console (F12)**
```javascript
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'test', password: 'test' })
})
.then(response => console.log('✅ CORS fixed!', response))
.catch(error => console.log('❌ Still error:', error));
```

#### **Option 2: Truy cập ApiTestComponent**
```
http://localhost:3000/api-test  (hoặc port của bạn)
Click "Test Kết nối Backend"
```

### 🔍 Expected Results sau khi fix:

#### ✅ **CORS Fixed - Sẽ thấy:**
```
✅ Backend connected successfully!
Response time: 123ms
Status: 401 (Unauthorized) - Đây là bình thường, vì chưa login
```

#### ❌ **Nếu vẫn lỗi:**
```
❌ Backend connection failed: AxiosError
```

### 🐛 Troubleshooting:

#### **Nếu vẫn bị CORS:**

1. **Kiểm tra Backend đã restart chưa:**
   - Console phải hiện: `Started GenderHealthcareServiceApplication`
   - Không có error logs màu đỏ

2. **Kiểm tra port Backend:**
   ```
   Browser: http://localhost:8080
   Phải thấy: "Whitelabel Error Page" (đây là normal)
   ```

3. **Clear browser cache:**
   ```
   Ctrl + Shift + R (hard refresh)
   ```

4. **Kiểm tra Multiple IntelliJ instances:**
   - Chỉ chạy 1 instance backend
   - Kill all java processes nếu cần

#### **Nếu vẫn ERR_FAILED:**

1. **Database issue:**
   ```
   Kiểm tra SQL Server đã start chưa
   Kiểm tra connection string trong application.properties
   ```

2. **Port conflict:**
   ```
   Netstat -an | findstr :8080
   Kill process nếu port bị occupied
   ```

### 🎯 CORS Configuration Details:

**Allowed Origins:**
- `http://localhost:3000` ✅ (React default)  
- `http://localhost:5173` ✅ (Vite default)
- `http://localhost:4000` ✅ (Alternative)
- `http://127.0.0.1:*` ✅ (Alternative localhost)
- Local network patterns ✅

**Allowed Methods:**
- GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD ✅

**Headers:**
- All headers allowed ✅
- Credentials supported ✅
- Proper exposed headers ✅

### 📞 Quick Test Commands:

```bash
# 1. Test backend is running
curl http://localhost:8080

# 2. Test CORS specifically  
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -X OPTIONS \
     http://localhost:8080/api/auth/login

# 3. Should see CORS headers in response
```

### 🎉 Success Indicators:

✅ **No more CORS errors in console**  
✅ **Backend responding (even with 401/403 is OK)**  
✅ **ApiTestComponent shows connection success**  
✅ **Can make API calls from frontend**  

---

**🚀 Sau khi restart Backend, test ngay trong ApiTestComponent!** 