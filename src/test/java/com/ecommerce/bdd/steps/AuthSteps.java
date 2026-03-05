package com.ecommerce.bdd.steps;

import com.ecommerce.application.dto.AuthResponse;
import com.ecommerce.application.dto.LoginRequest;
import com.ecommerce.application.dto.RegisterRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<AuthResponse> authResponse;
    private String userEmail;
    private String userPassword;

    @Given("a new user with email {string} and password {string}")
    public void aNewUserWithEmailAndPassword(String email, String password) {
        this.userEmail = email;
        this.userPassword = password;
    }

    @When("the user registers with first name {string} and last name {string}")
    public void theUserRegistersWithFirstNameAndLastName(String firstName, String lastName) {
        RegisterRequest request = RegisterRequest.builder()
                .email(userEmail)
                .password(userPassword)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        authResponse = restTemplate.postForEntity("/api/auth/register", request, AuthResponse.class);
    }

    @Then("the registration should be successful")
    public void theRegistrationShouldBeSuccessful() {
        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(authResponse.getBody()).isNotNull();
        assertThat(authResponse.getBody().getAccessToken()).isNotNull();
    }

    @Then("the user should receive an access token")
    public void theUserShouldReceiveAnAccessToken() {
        assertThat(authResponse.getBody().getAccessToken()).isNotEmpty();
        assertThat(authResponse.getBody().getTokenType()).isEqualTo("Bearer");
    }

    @Given("a registered user with email {string} and password {string}")
    public void aRegisteredUserWithEmailAndPassword(String email, String password) {
        RegisterRequest request = RegisterRequest.builder()
                .email(email)
                .password(password)
                .firstName("Test")
                .lastName("User")
                .build();

        restTemplate.postForEntity("/api/auth/register", request, AuthResponse.class);

        this.userEmail = email;
        this.userPassword = password;
    }

    @When("the user logs in")
    public void theUserLogsIn() {
        LoginRequest request = LoginRequest.builder()
                .email(userEmail)
                .password(userPassword)
                .build();

        authResponse = restTemplate.postForEntity("/api/auth/login", request, AuthResponse.class);
    }

    @Then("the login should be successful")
    public void theLoginShouldBeSuccessful() {
        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(authResponse.getBody()).isNotNull();
        assertThat(authResponse.getBody().getAccessToken()).isNotNull();
    }
}
