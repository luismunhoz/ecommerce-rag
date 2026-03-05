package com.ecommerce.bdd.steps;

import com.ecommerce.application.dto.ProductDTO;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.Product;
import com.ecommerce.infrastructure.adapter.out.persistence.CategoryJpaRepository;
import com.ecommerce.infrastructure.adapter.out.persistence.ProductJpaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    private ResponseEntity<?> response;
    private Product testProduct;

    @Before
    public void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Given("a product {string} exists with price {double} and stock {int}")
    public void aProductExistsWithPriceAndStock(String name, double price, int stock) {
        Category category = categoryRepository.save(
                Category.builder()
                        .name("Test Category")
                        .slug("test-category")
                        .build()
        );

        testProduct = productRepository.save(
                Product.builder()
                        .name(name)
                        .description("Test product description")
                        .price(BigDecimal.valueOf(price))
                        .stockQuantity(stock)
                        .sku("SKU-" + System.currentTimeMillis())
                        .category(category)
                        .active(true)
                        .build()
        );
    }

    @When("I request the product details")
    public void iRequestTheProductDetails() {
        response = restTemplate.getForEntity(
                "/api/products/" + testProduct.getId(),
                ProductDTO.class
        );
    }

    @Then("I should see the product name {string}")
    public void iShouldSeeTheProductName(String expectedName) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductDTO product = (ProductDTO) response.getBody();
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(expectedName);
    }

    @Then("I should see the price {double}")
    public void iShouldSeeThePrice(double expectedPrice) {
        ProductDTO product = (ProductDTO) response.getBody();
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(expectedPrice));
    }

    @When("I search for products containing {string}")
    public void iSearchForProductsContaining(String query) {
        response = restTemplate.exchange(
                "/api/products/search?q=" + query,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
    }

    @Then("I should find {int} product\\(s)")
    public void iShouldFindProducts(int expectedCount) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> page = (Map<String, Object>) response.getBody();
        assertThat(page).isNotNull();
        assertThat(((Number) page.get("totalElements")).intValue()).isEqualTo(expectedCount);
    }

    @Given("no products exist")
    public void noProductsExist() {
        productRepository.deleteAll();
    }

    @When("I request all products")
    public void iRequestAllProducts() {
        response = restTemplate.exchange(
                "/api/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
    }

    @Then("I should receive an empty list")
    public void iShouldReceiveAnEmptyList() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> page = (Map<String, Object>) response.getBody();
        assertThat(page).isNotNull();
        assertThat(((Number) page.get("totalElements")).intValue()).isEqualTo(0);
    }
}
