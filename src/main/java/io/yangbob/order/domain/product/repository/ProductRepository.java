package io.yangbob.order.domain.product.repository;

import io.yangbob.order.domain.product.entity.Product;
import io.yangbob.order.domain.product.entity.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, ProductId> {
}
