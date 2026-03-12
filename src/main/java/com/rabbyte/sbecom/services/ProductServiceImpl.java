package com.rabbyte.sbecom.services;

import com.rabbyte.sbecom.dtos.ProductRequestDTO;
import com.rabbyte.sbecom.dtos.ProductResponseDTO;
import com.rabbyte.sbecom.entities.Category;
import com.rabbyte.sbecom.entities.Product;
import com.rabbyte.sbecom.exceptions.ApiException;
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

import java.io.IOException;
import java.util.List;

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
    public ProductRequestDTO handleCreateProduct(ProductRequestDTO requestProduct, Long categoryId) {
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
        return modelMapper.map(savedProduct, ProductRequestDTO.class);
    }

    @Override
    public ProductResponseDTO handleGetAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Product> productsPage = this.productRepository.findAll(pageable);

        if (productsPage.isEmpty()) {
            throw new ApiException("No products found");
        }

        ProductResponseDTO productResponseDTO = new ProductResponseDTO();

        List<Product> products = productsPage.getContent();

        List<ProductRequestDTO> productRequestDTOS = modelMapper.map(products, new TypeToken<List<ProductRequestDTO>>(){}.getType());
        productResponseDTO.setContent(productRequestDTOS);
        productResponseDTO.setPageNumber(productsPage.getNumber() + 1);
        productResponseDTO.setPageSize(productsPage.getSize());
        productResponseDTO.setTotalPages(productsPage.getTotalPages());
        productResponseDTO.setTotalElements(productsPage.getTotalElements());
        productResponseDTO.setLastPage(productsPage.isLast());

        return productResponseDTO;
    }

    @Override
    public ProductResponseDTO handleGetProductsByCategoryId(
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
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        List<ProductRequestDTO> productRequestDTOS = modelMapper.map(products, new TypeToken<List<ProductRequestDTO>>(){}.getType());

        productResponseDTO.setContent(productRequestDTOS);
        productResponseDTO.setPageNumber(productPage.getNumber() + 1);
        productResponseDTO.setPageSize(productPage.getSize());
        productResponseDTO.setTotalPages(productPage.getTotalPages());
        productResponseDTO.setTotalElements(productPage.getTotalElements());
        productResponseDTO.setLastPage(productPage.isLast());

        return productResponseDTO;
    }

    @Override
    public ProductResponseDTO handleGetProductsByKeyword(
            String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Product> productPage = this.productRepository.findAll(pageable);

        String formatKeyword = ("%" + keyword + "%").toLowerCase();
        List<Product> products = this.productRepository.findProductsByProductNameIsLikeIgnoreCase(formatKeyword);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        List<ProductRequestDTO> productRequestDTOS = modelMapper.map(products, new TypeToken<List<ProductRequestDTO>>(){}.getType());

        productResponseDTO.setPageNumber(productPage.getNumber() + 1);
        productResponseDTO.setPageSize(productPage.getSize());
        productResponseDTO.setTotalPages(productPage.getTotalPages());
        productResponseDTO.setTotalElements(productPage.getTotalElements());
        productResponseDTO.setLastPage(productPage.isLast());

        productResponseDTO.setContent(productRequestDTOS);

        return productResponseDTO;
    }

    @Override
    public ProductRequestDTO handleUpdateProduct(long productId, ProductRequestDTO reqProduct) {
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
        return modelMapper.map(savedProduct, ProductRequestDTO.class);
    }

    @Override
    public ProductRequestDTO handleDeleteProduct(long productId) {
        Product product = this.productRepository.findById(productId).orElseThrow(() ->
            new ResourceNotFoundException("product", "productId", productId)
        );
        this.productRepository.delete(product);
        return modelMapper.map(product, ProductRequestDTO.class);
    }

    @Override
    public ProductRequestDTO handleUpdateProductImage(long productId, MultipartFile image) throws IOException {
        Product dbResult = this.productRepository.findById(productId).orElseThrow(() ->
            new ResourceNotFoundException("product", "productId", productId)
        );
        String fileName = this.fileService.uploadImage(path, image);
        dbResult.setImage(fileName);
        Product savedProduct = this.productRepository.save(dbResult);
        return modelMapper.map(savedProduct, ProductRequestDTO.class);
    }


}
