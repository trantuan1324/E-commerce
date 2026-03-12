package com.rabbyte.sbecom.controllers;

import com.rabbyte.sbecom.dtos.ProductRequestDTO;
import com.rabbyte.sbecom.dtos.ProductResponseDTO;
import com.rabbyte.sbecom.services.ProductService;
import com.rabbyte.sbecom.utils.constants.AppConstant;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {


    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductRequestDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO requestProduct,
            @PathVariable Long categoryId
    ) {
        var result = this.productService.handleCreateProduct(requestProduct, categoryId);
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponseDTO> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_BY_PRODUCT_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstant.SORT_DIR, required = false) String sortDir
    ) {
        ProductResponseDTO productResponseDTO = this.productService.handleGetAllProducts(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.status(200).body(productResponseDTO);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponseDTO> getProductsByCategoryId(
            @RequestParam(name = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_BY_PRODUCT_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstant.SORT_DIR, required = false) String sortDir,
            @PathVariable Long categoryId) {
        ProductResponseDTO productResponseDTO = this.productService.handleGetProductsByCategoryId(categoryId, pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.status(200).body(productResponseDTO);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponseDTO> getProductsByKeyword(
            @RequestParam(name = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_BY_PRODUCT_ID, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstant.SORT_DIR, required = false) String sortDir,
            @PathVariable String keyword) {
        ProductResponseDTO productResponseDTO = this.productService.handleGetProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.status(200).body(productResponseDTO);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductRequestDTO> updateProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO,
            @PathVariable Long productId
    ) {
        ProductRequestDTO result = this.productService.handleUpdateProduct(productId, productRequestDTO);
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductRequestDTO> deleteProductById(@PathVariable long productId) {
        ProductRequestDTO result = this.productService.handleDeleteProduct(productId);
        return ResponseEntity.status(200).body(result);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductRequestDTO> updateProductImage(
            @PathVariable long productId,
            @RequestParam("image") MultipartFile image) throws IOException {
        var updatedProduct = this.productService.handleUpdateProductImage(productId, image);
        return ResponseEntity.status(200).body(updatedProduct);
    }
}
