Feature: Product Management
  As a customer
  I want to view and search products
  So that I can find items to purchase

  Scenario: View product details
    Given a product "Wireless Mouse" exists with price 29.99 and stock 100
    When I request the product details
    Then I should see the product name "Wireless Mouse"
    And I should see the price 29.99

  Scenario: Search for products
    Given a product "Gaming Keyboard" exists with price 79.99 and stock 50
    When I search for products containing "Gaming"
    Then I should find 1 product(s)

  Scenario: View empty product list
    Given no products exist
    When I request all products
    Then I should receive an empty list
