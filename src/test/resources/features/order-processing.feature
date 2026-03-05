Feature: Order Processing
  As a customer
  I want to create and manage orders
  So that I can purchase products

  Scenario: Create a new order
    Given an authenticated user
    And a product "Laptop Stand" with price 49.99 and 10 in stock
    When I create an order for 2 unit(s) of the product
    Then the order should be created successfully
    And the order status should be "PENDING"
    And the order total should be 99.98
    And the product stock should be reduced to 8
