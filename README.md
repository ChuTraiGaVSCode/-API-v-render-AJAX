# Category-Product API — Spring Boot 3 + Swagger 3 + AJAX

Dự án bài tập hoàn chỉnh, gộp đủ 4 nội dung bạn yêu cầu:

| # | Yêu cầu | Nơi thể hiện trong project |
|---|---------|------------------------------|
| 1 | Đọc bài giảng `16_API RestFul` (REST, JSON, Jackson, Gson) | Áp dụng trực tiếp: Controller trả JSON qua Jackson (Spring Boot tự tích hợp), theo đúng chuẩn REST GET/POST/PUT/DELETE + status code |
| 2 | CRUD API Category trên Spring Boot 3 | `entity/Category.java`, `repository/CategoryRepository.java`, `service/ICategoryService.java` + `CategoryServiceImpl.java`, `controller/CategoryAPIController.java` |
| 3 | Cấu hình Swagger 3 | `pom.xml` (springdoc-openapi-starter-webmvc-ui) — **không cần viết Bean cấu hình gì thêm**, chỉ cần thêm dependency là có `/swagger-ui/index.html` |
| 4 | AJAX cho CRUD Product & Category | `src/main/resources/static/category.html`, `product.html` (jQuery `$.ajax`, `FormData`, giống hệt kỹ thuật trong tài liệu AJAX) |

## 1. Vì sao dùng H2 thay vì SQL Server/MySQL?

Để bạn **chạy được ngay lập tức**, không cần cài đặt hệ quản trị CSDL nào. H2 tự tạo file DB tại `./data/appdb.mv.db` khi chạy lần đầu.

Khi nộp bài / làm theo đúng tài liệu gốc (SQL Server), mở `application.properties`, comment phần H2 lại, bỏ comment phần MySQL/SQL Server mẫu, và thêm driver tương ứng vào `pom.xml`.

## 2. Vì sao dùng file HTML tĩnh (`static/*.html`) thay vì JSP?

Tài liệu AJAX gốc dùng JSP (`admin.jsp`, sitemesh...) — đây là kỹ thuật cũ, cần thêm nhiều cấu hình (jasper, sitemesh, view resolver) không còn được khuyến khích trong Spring Boot 3.

Kỹ thuật AJAX (jQuery `$.ajax`, `FormData`, `contentType:false`, `processData:false`) **hoàn toàn giống nhau**, chỉ khác là mình đặt code trong file `.html` tĩnh nằm ở `src/main/resources/static/` — Spring Boot tự phục vụ các file này mà không cần cấu hình gì thêm. Nếu môn học của bạn **bắt buộc dùng JSP**, xem mục "Chuyển sang JSP" bên dưới.

## 3. Cách chạy dự án

### Cách 1: Dùng Eclipse / Spring Tool Suite / IntelliJ
1. Import project dạng "Existing Maven Project".
2. Đợi Maven tải dependency xong (cần internet).
3. Chạy file `CategoryProductApiApplication.java` (Run as → Java Application / Spring Boot App).

### Cách 2: Dùng terminal (cần cài Maven)
```bash
cd category-product-api
mvn spring-boot:run
```

### Sau khi chạy thành công
- Trang chủ demo: http://localhost:8080/index.html
- Trang CRUD Category (AJAX): http://localhost:8080/category.html
- Trang CRUD Product (AJAX): http://localhost:8080/product.html
- Swagger UI (test API): **http://localhost:8080/swagger-ui/index.html**
- H2 Console (xem dữ liệu): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/appdb`
  - User: `sa`, Password: (để trống)

## 4. Danh sách API đã viết (test được luôn trên Swagger)

### Category — `/api/category`
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/category` | Lấy tất cả category |
| POST | `/api/category/getCategory?id=` | Lấy 1 category theo id |
| POST | `/api/category/addCategory` | Thêm category (form-data: `categoryName`, `icon`) |
| PUT | `/api/category/updateCategory` | Cập nhật (form-data: `categoryId`, `categoryName`, `icon`) |
| DELETE | `/api/category/deleteCategory?categoryId=` | Xóa category |

### Product — `/api/product`
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/product` | Lấy tất cả product |
| GET | `/api/product/byCategory?categoryId=` | Lấy product theo category |
| POST | `/api/product/getProduct?id=` | Lấy 1 product theo id |
| POST | `/api/product/addProduct` | Thêm product (form-data: `productName`, `imageFile`, `unitPrice`, `discount`, `description`, `categoryId`, `quantity`, `status`) |
| PUT | `/api/product/updateProduct` | Cập nhật product (thêm `productId`) |
| DELETE | `/api/product/deleteProduct?productId=` | Xóa product |

Tất cả API trả về format thống nhất:
```json
{ "status": true, "message": "Thành công", "body": { ... } }
```

## 5. Luồng test đề nghị (giống thứ tự bài học)

1. Chạy app → mở Swagger UI → test `POST /api/category/addCategory` để tạo vài Category.
2. Test `POST /api/product/addProduct` (chọn `categoryId` vừa tạo).
3. Mở `category.html` và `product.html` → kiểm tra bảng hiển thị đúng dữ liệu (GET bằng AJAX).
4. Thử Thêm/Sửa/Xóa trực tiếp trên giao diện AJAX → xem Network tab (F12) để hiểu request/response.
5. Vào `h2-console` xem dữ liệu thực trong bảng `CATEGORIES`, `PRODUCTS`.

## 6. Chuyển sang JSP (nếu môn học bắt buộc)

Nếu bạn cần bám sát 100% theo tài liệu gốc (dùng JSP thay vì HTML tĩnh):
1. Đóng gói lại thành **war**, thêm dependency `tomcat-embed-jasper` + `jstl`.
2. Copy nội dung `<script>` AJAX trong `category.html`/`product.html` dán y nguyên vào file `.jsp` tương ứng (cú pháp `$.ajax` không đổi).
3. `contextPath` trong JSP dùng: `<script>var contextPath = "${pageContext.request.contextPath}"</script>` (đã có sẵn trong tài liệu AJAX bạn gửi).

## 7. Cấu trúc thư mục

```
category-product-api/
├── pom.xml
├── uploads/                         # nơi lưu icon/ảnh upload (tự tạo khi chạy)
└── src/main/
    ├── java/vn/iotstar/
    │   ├── CategoryProductApiApplication.java
    │   ├── entity/        (Category, Product)
    │   ├── repository/    (CategoryRepository, ProductRepository)
    │   ├── service/       (interface + Impl cho Category, Product, Storage)
    │   ├── model/          (Response.java)
    │   ├── config/         (StorageProperties, WebConfig)
    │   ├── exception/      (StorageException...)
    │   └── controller/     (CategoryAPIController, ProductAPIController)
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html
            ├── category.html
            ├── product.html
            └── css/style.css
```

## 8. Lưu ý quan trọng

- Dự án được viết và kiểm tra logic thủ công (đọc kỹ theo từng dòng code trong tài liệu bạn cung cấp), **nhưng môi trường hiện tại không có kết nối tới Maven Central nên chưa build/run thử được**. Khi mở bằng Eclipse/IntelliJ có mạng, Maven sẽ tự tải dependency — nếu gặp lỗi biên dịch nhỏ (thường do version dependency), báo lại để mình sửa tiếp.
- Icon/ảnh sau khi upload được lưu ở thư mục `uploads/` (cạnh file `pom.xml`) và truy cập qua URL `/uploads/<tên file>` (cấu hình trong `WebConfig.java`).
