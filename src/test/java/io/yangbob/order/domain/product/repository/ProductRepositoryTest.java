package io.yangbob.order.domain.product.repository;

import io.yangbob.order.domain.product.entity.Product;
import io.yangbob.order.domain.product.entity.ProductId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("기본적인 저장 및 조회")
    void saveAndFindAndDeleteTest() {
        String name = "선풍기";
        int price = 125000;
        Product product = new Product(name, price);
        ProductId productId = product.getId();
        assertThat(productRepository.findById(productId).isPresent()).isFalse();

        productRepository.save(product);
        em.flush();
        em.clear();

        Product findProduct = productRepository.findById(productId).get();
        assertThat(findProduct).isNotNull();
        assertThat(findProduct).isEqualTo(product);
        assertThat(findProduct.getName()).isEqualTo(name);
        assertThat(findProduct.getPrice()).isEqualTo(price);

        productRepository.delete(findProduct);
        em.flush();
        em.clear();

        assertThat(productRepository.findById(productId).isPresent()).isFalse();
    }
}
