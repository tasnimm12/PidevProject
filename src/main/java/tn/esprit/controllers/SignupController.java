package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserServices;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SignupController implements Initializable {
    
    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField telephoneField;
    
    @FXML
    private DatePicker dateNaissancePicker;
    
    @FXML
    private ComboBox<String> roleComboBox;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private CheckBox termsCheckbox;
    
    @FXML
    private Label messageLabel;
    
    @FXML
    private Button signupButton;
    
    private UserServices userServices;
    
    public SignupController() {
        userServices = new UserServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate role combo box
        roleComboBox.getItems().addAll("client", "organisateur");
        roleComboBox.setValue("client");
    }
    
    @FXML
    private void handleSignup() {
        // Validation
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create new user
            User newUser = new User(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText().trim(),
                telephoneField.getText().trim(),
                dateNaissancePicker.getValue(),
                roleComboBox.getValue(),
                "actif"
            );
            
            // Check if email already exists
            if (emailExists(newUser.getEmail())) {
                showMessage("Email already registered", true);
                return;
            }
            
            // Add user to database
            userServices.ajouter(newUser);
            
            // Show success message
            showMessage("Account created successfully! Redirecting to login...", false);
            
            // Navigate to login after delay
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleLoginNavigation);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (SQLException e) {
            showMessage("Database error: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    private boolean validateForm() {
        // Clear previous message
        messageLabel.setVisible(false);
        
        // Check first name
        if (prenomField.getText().trim().isEmpty()) {
            showMessage("First name is required", true);
            prenomField.requestFocus();
            return false;
        }
        
        // Validate first name (only letters and spaces)
        if (!prenomField.getText().trim().matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            showMessage("First name should contain only letters", true);
            prenomField.requestFocus();
            return false;
        }
        
        // Check last name
        if (nomField.getText().trim().isEmpty()) {
            showMessage("Last name is required", true);
            nomField.requestFocus();
            return false;
        }
        
        // Validate last name (only letters and spaces)
        if (!nomField.getText().trim().matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            showMessage("Last name should contain only letters", true);
            nomField.requestFocus();
            return false;
        }
        
        // Check email
        if (emailField.getText().trim().isEmpty()) {
            showMessage("Email address is required", true);
            emailField.requestFocus();
            return false;
        }
        
        // Check email format
        if (!isValidEmail(emailField.getText().trim())) {
            showMessage("Invalid email format (example: user@domain.com)", true);
            emailField.requestFocus();
            return false;
        }
        
        // Check phone
        if (telephoneField.getText().trim().isEmpty()) {
            showMessage("Phone number is required", true);
            telephoneField.requestFocus();
            return false;
        }
        
        // Check phone format
        if (!isValidPhone(telephoneField.getText().trim())) {
            showMessage("Invalid phone number (minimum 8 digits)", true);
            telephoneField.requestFocus();
            return false;
        }
        
        // Check date of birth
        if (dateNaissancePicker.getValue() == null) {
            showMessage("Date of birth is required", true);
            dateNaissancePicker.requestFocus();
            return false;
        }
        
        // Check if date is not in the future
        if (dateNaissancePicker.getValue().isAfter(LocalDate.now())) {
            showMessage("Date of birth cannot be in the future", true);
            dateNaissancePicker.requestFocus();
            return false;
        }
        
        // Check if user is at least 18 years old
        if (dateNaissancePicker.getValue().isAfter(LocalDate.now().minusYears(18))) {
            showMessage("You must be at least 18 years old to register", true);
            dateNaissancePicker.requestFocus();
            return false;
        }
        
        // Check password
        if (passwordField.getText().trim().isEmpty()) {
            showMessage("Password is required", true);
            passwordField.requestFocus();
            return false;
        }
        
        // Check password length
        if (passwordField.getText().trim().length() < 6) {
            showMessage("Password must be at least 6 characters long", true);
            passwordField.requestFocus();
            return false;
        }
        
        // Check password strength
        String password = passwordField.getText().trim();
        if (!password.matches(".*[A-Za-z].*")) {
            showMessage("Password must contain at least one letter", true);
            passwordField.requestFocus();
            return false;
        }
        
        // Check confirm password
        if (confirmPasswordField.getText().trim().isEmpty()) {
            showMessage("Please confirm your password", true);
            confirmPasswordField.requestFocus();
            return false;
        }
        
        // Check password match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showMessage("Passwords do not match", true);
            confirmPasswordField.requestFocus();
            return false;
        }
        
        // Check terms acceptance
        if (!termsCheckbox.isSelected()) {
            showMessage("You must accept the terms and conditions", true);
            termsCheckbox.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean emailExists(String email) throws SQLException {
        return userServices.afficher().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
    
    @FXML
    private void handleLoginNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Login");
        } catch (Exception e) {
            showMessage("Error loading login page: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        
        if (isError) {
            messageLabel.setStyle("-fx-text-fill: #f56565;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #48bb78;");
        }
    }
    
    private boolean isValidEmail(String email) {
        // Comprehensive email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    private boolean isValidPhone(String phone) {
        // Remove spaces and special characters for validation
        String cleanPhone = phone.replaceAll("[\\s\\-()]", "");
        // Check if it contains only digits and + (for international format)
        // And has minimum 8 digits
        return cleanPhone.matches("^\\+?[0-9]{8,15}$");
    }
}
