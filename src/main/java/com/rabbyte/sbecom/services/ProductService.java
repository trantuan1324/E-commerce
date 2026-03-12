package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.ProductRequestDTO;
import com.rabbyte.sbecom.dtos.ProductResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductRequestDTO handleCreateProduct(ProductRequestDTO reqProduct, Long categoryId);
    ProductResponseDTO handleGetAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    ProductResponseDTO handleGetProductsByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    ProductResponseDTO handleGetProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    ProductRequestDTO handleUpdateProduct(long productId, ProductRequestDTO reqProduct);
    ProductRequestDTO handleDeleteProduct(long productId);
    ProductRequestDTO handleUpdateProductImage(long productId, MultipartFile image) throws IOException;
}
