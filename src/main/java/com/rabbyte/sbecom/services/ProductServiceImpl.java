package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.ProductDTO;
import com.rabbyte.sbecom.dtos.ProductResponse;
import com.rabbyte.sbecom.entities.Category;
import com.rabbyte.sbecom.entities.Product;
import com.rabbyte.sbecom.exceptions.ApiException;
import com.rabbyte.sbecom.repositories.CategoryRepository;
import com.rabbyte.sbecom.repositories.ProductRepository;
import com.rabbyte.sbecom.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ModelMapper modelMapper, CategoryService categoryService, FileService fileService) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
        this.fileService = fileService;
    }

    @Override
    public ProductDTO handleCreateProduct(ProductDTO requestProduct, Long categoryId) {
        Category category = this.categoryService.handleGetCategoryById(categoryId);

        if (this.productRepository.existsProductByProductName(requestProduct.getProductName())) {
            throw new ApiException("Product name already exist!!!");
        }

        Product product = modelMapper.map(requestProduct, Product.class);

        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice =
                requestProduct.getPrice() - ((requestProduct.getDiscount() * 0.01) * requestProduct.getPrice());
        requestProduct.setSpecialPrice(specialPrice);

        Product savedProduct = this.productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse handleGetAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Product> productsPage = this.productRepository.findAll(pageable);

        if (productsPage.isEmpty()) {
            throw new ApiException("No products found");
        }

        ProductResponse productResponse = new ProductResponse();

        List<Product> products = productsPage.getContent();

        List<ProductDTO> productDTOs = modelMapper.map(products, new TypeToken<List<ProductDTO>>(){}.getType());
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(productsPage.getNumber() + 1);
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse handleGetProductsByCategoryId(
            Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<Product> productPage = this.productRepository.findAll(pageable);
        Category category = this.categoryService.handleGetCategoryById(categoryId);

        List<Product> products = this.productRepository.findProductsByCategoryOrderByPriceAsc(category);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("products");
        }
        ProductResponse productResponse = new ProductResponse();
        List<ProductDTO> productDTOs = modelMapper.map(products, new TypeToken<List<ProductDTO>>(){}.getType());

        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(productPage.getNumber() + 1);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse handleGetProductsByKeyword(
            String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Product> productPage = this.productRepository.findAll(pageable);

        String formatKeyword = ("%" + keyword + "%").toLowerCase();
        List<Product> products = this.productRepository.findProductsByProductNameIsLikeIgnoreCase(formatKeyword);

        ProductResponse productResponse = new ProductResponse();
        List<ProductDTO> productDTOs = modelMapper.map(products, new TypeToken<List<ProductDTO>>(){}.getType());

        productResponse.setPageNumber(productPage.getNumber() + 1);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());

        productResponse.setContent(productDTOs);

        return productResponse;
    }

    @Override
    public ProductDTO handleUpdateProduct(long productId, ProductDTO reqProduct) {
        Product product = this.productRepository.findById(productId).orElseThrow(() ->
            new ResourceNotFoundException("product", "productId", productId)
        );

        if (this.productRepository.existsProductByProductName(reqProduct.getProductName())) {
            throw new ApiException("Product name already exist!!!");
        }

        double specialPrice =
                reqProduct.getPrice() - ((reqProduct.getDiscount() * 0.01) * reqProduct.getPrice());
        reqProduct.setSpecialPrice(specialPrice);

        product.setProductName(reqProduct.getProductName());
        product.setPrice(reqProduct.getPrice());
        product.setDiscount(reqProduct.getDiscount());
        product.setSpecialPrice(reqProduct.getSpecialPrice());
        product.setQuantity(reqProduct.getQuantity());
        product.setDescription(reqProduct.getDescription());

        Product savedProduct = this.productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO handleDeleteProduct(long productId) {
        Product product = this.productRepository.findById(productId).orElseThrow(() ->
            new ResourceNotFoundException("product", "productId", productId)
        );
        this.productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO handleUpdateProductImage(long productId, MultipartFile image) throws IOException {
        Product dbResult = this.productRepository.findById(productId).orElseThrow(() ->
            new ResourceNotFoundException("product", "productId", productId)
        );
        String fileName = this.fileService.uploadImage(path, image);
        dbResult.setImage(fileName);
        Product savedProduct = this.productRepository.save(dbResult);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }


}
