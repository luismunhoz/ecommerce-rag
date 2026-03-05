Feature: User Authentication
  As a user of the e-commerce platform
  I want to be able to register and login
  So that I can access my account and make purchases

  Scenario: User registration
    Given a new user with email "newuser@example.com" and password "password123"
    When the user registers with first name "John" and last name "Doe"
    Then the registration should be successful
    And the user should receive an access token

  Scenario: User login
    Given a registered user with email "logintest@example.com" and password "password123"
    When the user logs in
    Then the login should be successful
    And the user should receive an access token
