package com.ecommerce.infrastructure.adapter.in.messaging;

import com.ecommerce.application.dto.CreateProductRequest;
import com.ecommerce.application.dto.ProductSyncMessage;
import com.ecommerce.application.dto.UpdateProductRequest;
import com.ecommerce.application.service.ProductService;
import com.ecommerce.domain.exception.ProductNotFoundException;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class ProductMessageConsumer {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @RabbitListener(queues = "${app.messaging.product-sync.queue}")
    public void handleProductSync(ProductSyncMessage message) {
        if (message.getAction() == null || message.getSku() == null) {
            log.warn("Discarding malformed product sync message: missing action or sku. message={}", message);
            return;
        }

        log.info("Received product sync message: action={}, sku={}", message.getAction(), message.getSku());

        switch (message.getAction().toUpperCase()) {
            case "CREATE" -> handleCreate(message);
            case "UPDATE" -> handleUpdate(message);
            case "DELETE" -> handleDelete(message);
            default -> log.warn("Discarding product sync message with unknown action='{}', sku='{}'",
                    message.getAction(), message.getSku());
        }
    }

    private void handleCreate(ProductSyncMessage message) {
        // Upsert: if the SKU already exists treat the message as an update
        productRepository.findBySku(message.getSku()).ifPresentOrElse(
                existing -> {
                    log.info("SKU '{}' already exists (id={}), treating CREATE as UPDATE", message.getSku(), existing.getId());
                    performUpdate(existing, message);
                },
                () -> {
                    CreateProductRequest request = CreateProductRequest.builder()
                            .name(message.getName())
                            .description(message.getDescription())
                            .price(message.getPrice())
                            .stockQuantity(message.getStockQuantity())
                            .sku(message.getSku())
                            .imageUrl(message.getImageUrl())
                            .categoryId(message.getCategoryId())
                            .build();
                    productService.createProduct(request);
                    log.info("Product created from sync message: sku='{}'", message.getSku());
                }
        );
    }

    private void handleUpdate(ProductSyncMessage message) {
        productRepository.findBySku(message.getSku()).ifPresentOrElse(
                existing -> performUpdate(existing, message),
                () -> log.warn("Discarding UPDATE message: product not found for sku='{}'", message.getSku())
        );
    }

    private void handleDelete(ProductSyncMessage message) {
        productRepository.findBySku(message.getSku()).ifPresentOrElse(
                existing -> {
                    try {
                        productService.deleteProduct(existing.getId());
                        log.info("Product deleted from sync message: sku='{}', id={}", message.getSku(), existing.getId());
                    } catch (ProductNotFoundException e) {
                        log.warn("Discarding DELETE message: product no longer exists for sku='{}'", message.getSku());
                    }
                },
                () -> log.warn("Discarding DELETE message: product not found for sku='{}'", message.getSku())
        );
    }

    private void performUpdate(Product existing, ProductSyncMessage message) {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .name(message.getName())
                .description(message.getDescription())
                .price(message.getPrice())
                .stockQuantity(message.getStockQuantity())
                .sku(message.getSku())
                .imageUrl(message.getImageUrl())
                .categoryId(message.getCategoryId())
                .active(message.getActive())
                .build();
        productService.updateProduct(existing.getId(), request);
        log.info("Product updated from sync message: sku='{}', id={}", message.getSku(), existing.getId());
    }
}
