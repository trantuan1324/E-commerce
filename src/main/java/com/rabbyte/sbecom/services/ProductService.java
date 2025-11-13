package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.ProductDTO;
import com.rabbyte.sbecom.dtos.ProductResponse;
import com.rabbyte.sbecom.entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO handleCreateProduct(ProductDTO reqProduct, Long categoryId);
    ProductResponse handleGetAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    ProductResponse handleGetProductsByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    ProductResponse handleGetProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    ProductDTO handleUpdateProduct(long productId, ProductDTO reqProduct);
    ProductDTO handleDeleteProduct(long productId);
    ProductDTO handleUpdateProductImage(long productId, MultipartFile image) throws IOException;
}
