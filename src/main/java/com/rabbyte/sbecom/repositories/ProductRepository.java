package com.rabbyte.sbecom.repositories;

import com.rabbyte.sbecom.entities.Category;
import com.rabbyte.sbecom.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findProductsByCategoryOrderByPriceAsc(Category category);
    boolean existsProductByProductName(String productName);
    List<Product> findProductsByProductNameIsLikeIgnoreCase(String keyword);
}
