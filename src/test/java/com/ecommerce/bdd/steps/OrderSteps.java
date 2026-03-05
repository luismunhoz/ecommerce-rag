package com.ecommerce.bdd.steps;

import com.ecommerce.application.dto.*;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.User;
import com.ecommerce.infrastructure.adapter.out.persistence.CategoryJpaRepository;
import com.ecommerce.infrastructure.adapter.out.persistence.OrderJpaRepository;
import com.ecommerce.infrastructure.adapter.out.persistence.ProductJpaRepository;
import com.ecommerce.infrastructure.adapter.out.persistence.UserJpaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private OrderJpaRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authToken;
    private ResponseEntity<OrderResponse> orderResponse;
    private Product testProduct;

    @Before
    public void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Given("an authenticated user")
    public void anAuthenticatedUser() {
        String uniqueEmail = "order-test-" + System.currentTimeMillis() + "@example.com";

        if (!userRepository.existsByEmail(uniqueEmail)) {
            User user = User.builder()
                    .email(uniqueEmail)
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Order")
                    .lastName("Tester")
                    .build();
            user.addRole(User.Role.ROLE_USER);
            userRepository.save(user);
        }

        LoginRequest loginRequest = LoginRequest.builder()
                .email(uniqueEmail)
                .password("password123")
                .build();

        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, AuthResponse.class
        );

        assertThat(authResponse.getBody()).isNotNull();
        authToken = authResponse.getBody().getAccessToken();
    }

    @Given("a product {string} with price {double} and {int} in stock")
    public void aProductWithPriceAndInStock(String name, double price, int stock) {
        Category category = categoryRepository.save(
                Category.builder()
                        .name("Order Test Category")
                        .slug("order-test-category-" + System.currentTimeMillis())
                        .build()
        );

        testProduct = productRepository.save(
                Product.builder()
                        .name(name)
                        .description("Product for order testing")
                        .price(BigDecimal.valueOf(price))
                        .stockQuantity(stock)
                        .sku("ORDER-SKU-" + System.currentTimeMillis())
                        .category(category)
                        .active(true)
                        .build()
        );
    }

    @When("I create an order for {int} unit\\(s) of the product")
    public void iCreateAnOrderForUnitsOfTheProduct(int quantity) {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .items(List.of(
                        OrderItemRequest.builder()
                                .productId(testProduct.getId())
                                .quantity(quantity)
                                .build()
                ))
                .shippingAddress("123 Test Street, Test City")
                .paymentMethod("credit_card")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateOrderRequest> entity = new HttpEntity<>(request, headers);

        orderResponse = restTemplate.exchange(
                "/api/orders",
                HttpMethod.POST,
                entity,
                OrderResponse.class
        );
    }

    @Then("the order should be created successfully")
    public void theOrderShouldBeCreatedSuccessfully() {
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(orderResponse.getBody()).isNotNull();
        assertThat(orderResponse.getBody().getOrderNumber()).isNotNull();
    }

    @Then("the order status should be {string}")
    public void theOrderStatusShouldBe(String expectedStatus) {
        assertThat(orderResponse.getBody().getStatus()).isEqualTo(expectedStatus);
    }

    @Then("the order total should be {double}")
    public void theOrderTotalShouldBe(double expectedTotal) {
        assertThat(orderResponse.getBody().getTotalAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(expectedTotal));
    }

    @Then("the product stock should be reduced to {int}")
    public void theProductStockShouldBeReducedTo(int expectedStock) {
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(expectedStock);
    }
}
