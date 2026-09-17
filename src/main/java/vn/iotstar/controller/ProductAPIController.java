package vn.iotstar.controller;

import java.util.Date;
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
import vn.iotstar.entity.Product;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@Tag(name = "Product API", description = "CRUD san pham (Product)")
@RestController
@RequestMapping(path = "/api/product")
public class ProductAPIController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @Operation(summary = "Lay danh sach tat ca Product")
    @GetMapping
    public ResponseEntity<?> getAllProduct() {
        return new ResponseEntity<Response>(
                new Response(true, "Thành công", productService.findAll()), HttpStatus.OK);
    }

    @Operation(summary = "Lay danh sach Product theo Category")
    @GetMapping(path = "/byCategory")
    public ResponseEntity<?> getProductByCategory(@RequestParam("categoryId") Long categoryId) {
        return new ResponseEntity<Response>(
                new Response(true, "Thành công", productService.findByCategoryId(categoryId)), HttpStatus.OK);
    }

    @Operation(summary = "Lay 1 Product theo id")
    @PostMapping(path = "/getProduct")
    public ResponseEntity<?> getProduct(@Validated @RequestParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<Response>(
                    new Response(true, "Thành công", product.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<Response>(
                    new Response(false, "Không tìm thấy Product", null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Them moi Product (kem hinh anh)")
    @PostMapping(path = "/addProduct")
    public ResponseEntity<?> addProduct(
            @Validated @RequestParam("productName") String productName,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @Validated @RequestParam("discount") Double discount,
            @RequestParam(value = "description", required = false) String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @Validated @RequestParam("status") Short status) {

        Optional<Product> optProduct = productService.findByProductName(productName);
        if (optProduct.isPresent()) {
            return new ResponseEntity<Response>(
                    new Response(false, "Sản phẩm này đã tồn tại trong hệ thống", optProduct.get()),
                    HttpStatus.BAD_REQUEST);
        }

        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<Response>(
                    new Response(false, "Category không tồn tại", null), HttpStatus.BAD_REQUEST);
        }

        Product product = new Product();
        product.setProductName(productName);
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);
        product.setCategory(optCategory.get());
        product.setCreateDate(new Date());

        if (imageFile != null && !imageFile.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String uuString = uuid.toString();
            product.setImages(storageService.getSorageFilename(imageFile, uuString));
            storageService.store(imageFile, product.getImages());
        }

        Product saved = productService.save(product);
        return new ResponseEntity<Response>(
                new Response(true, "Thêm thành công", saved), HttpStatus.OK);
    }

    @Operation(summary = "Cap nhat Product")
    @PutMapping(path = "/updateProduct")
    public ResponseEntity<?> updateProduct(
            @Validated @RequestParam("productId") Long productId,
            @Validated @RequestParam("productName") String productName,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @Validated @RequestParam("discount") Double discount,
            @RequestParam(value = "description", required = false) String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @Validated @RequestParam("status") Short status) {

        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<Response>(
                    new Response(false, "Không tìm thấy Product", null), HttpStatus.BAD_REQUEST);
        }

        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<Response>(
                    new Response(false, "Category không tồn tại", null), HttpStatus.BAD_REQUEST);
        }

        Product product = optProduct.get();
        product.setProductName(productName);
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);
        product.setCategory(optCategory.get());

        if (imageFile != null && !imageFile.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String uuString = uuid.toString();
            product.setImages(storageService.getSorageFilename(imageFile, uuString));
            storageService.store(imageFile, product.getImages());
        }

        Product saved = productService.save(product);
        return new ResponseEntity<Response>(
                new Response(true, "Cập nhật thành công", saved), HttpStatus.OK);
    }

    @Operation(summary = "Xoa Product")
    @DeleteMapping(path = "/deleteProduct")
    public ResponseEntity<?> deleteProduct(@Validated @RequestParam("productId") Long productId) {
        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<Response>(
                    new Response(false, "Không tìm thấy Product", null), HttpStatus.BAD_REQUEST);
        } else {
            productService.delete(optProduct.get());
            return new ResponseEntity<Response>(
                    new Response(true, "Xóa thành công", optProduct.get()), HttpStatus.OK);
        }
    }
}
