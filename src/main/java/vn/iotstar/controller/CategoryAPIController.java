package vn.iotstar.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import vn.iotstar.entity.Category;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IStorageService;

@Tag(name = "Category API", description = "CRUD danh muc san pham (Category)")
@RestController
@RequestMapping(path = "/api/category")
public class CategoryAPIController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @Operation(summary = "Lay danh sach tat ca Category")
    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        return new ResponseEntity<Response>(
                new Response(true, "Thành công", categoryService.findAll()), HttpStatus.OK);
    }

    @Operation(summary = "Lay 1 Category theo id")
    @PostMapping(path = "/getCategory")
    public ResponseEntity<?> getCategory(@Validated @RequestParam("id") Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return new ResponseEntity<Response>(
                    new Response(true, "Thành công", category.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<Response>(
                    new Response(false, "Không tìm thấy Category", null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Them moi Category (kem icon)")
    @PostMapping(path = "/addCategory")
    public ResponseEntity<?> addCategory(
            @Validated @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {

        Optional<Category> optCategory = categoryService.findByCategoryName(categoryName);
        if (optCategory.isPresent()) {
            return new ResponseEntity<Response>(
                    new Response(false, "Category đã tồn tại trong hệ thống", optCategory.get()),
                    HttpStatus.BAD_REQUEST);
        } else {
            Category category = new Category();
            if (icon != null && !icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                category.setIcon(storageService.getSorageFilename(icon, uuString));
                storageService.store(icon, category.getIcon());
            }
            category.setCategoryName(categoryName);
            categoryService.save(category);
            return new ResponseEntity<Response>(
                    new Response(true, "Thêm thành công", category), HttpStatus.OK);
        }
    }

    @Operation(summary = "Cap nhat Category")
    @PutMapping(path = "/updateCategory")
    public ResponseEntity<?> updateCategory(
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {

        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<Response>(
                    new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        } else {
            if (icon != null && !icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                optCategory.get().setIcon(storageService.getSorageFilename(icon, uuString));
                storageService.store(icon, optCategory.get().getIcon());
            }
            optCategory.get().setCategoryName(categoryName);
            categoryService.save(optCategory.get());
            return new ResponseEntity<Response>(
                    new Response(true, "Cập nhật thành công", optCategory.get()), HttpStatus.OK);
        }
    }

    @Operation(summary = "Xoa Category")
    @DeleteMapping(path = "/deleteCategory")
    public ResponseEntity<?> deleteCategory(@Validated @RequestParam("categoryId") Long categoryId) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<Response>(
                    new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        } else {
            categoryService.delete(optCategory.get());
            return new ResponseEntity<Response>(
                    new Response(true, "Xóa thành công", optCategory.get()), HttpStatus.OK);
        }
    }
}
