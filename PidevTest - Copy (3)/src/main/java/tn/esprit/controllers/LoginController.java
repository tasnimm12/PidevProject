package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserServices;

import java.sql.SQLException;
import java.util.List;

public class LoginController {
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private CheckBox rememberMeCheckbox;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    private UserServices userServices;
    
    public LoginController() {
        userServices = new UserServices();
    }
    
    @FXML
    private void handleLogin() {
        // Clear previous error
        errorLabel.setVisible(false);
        
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validation - Check empty fields
        if (email.isEmpty()) {
            showError("Email address is required");
            emailField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Password is required");
            passwordField.requestFocus();
            return;
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            showError("Invalid email format. Please enter a valid email address");
            emailField.requestFocus();
            return;
        }
        
        // Validate password length
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            passwordField.requestFocus();
            return;
        }
        
        try {
            // Authenticate user
            User user = authenticateUser(email, password);
            
            if (user != null) {
                // Check account status
                if ("desactive".equals(user.getStatutCompte())) {
                    showError("Your account has been deactivated. Please contact support.");
                    return;
                }
                
                // Navigate based on role
                navigateToDashboard(user);
            } else {
                showError("Invalid email or password");
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private User authenticateUser(String email, String password) throws SQLException {
        List<User> users = userServices.afficher();
        
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getMotDePasse().equals(password)) {
                return user;
            }
        }
        
        return null;
    }
    
    private void navigateToDashboard(User user) {
        try {
            String fxmlFile = "";
            
            // Route based on role
            switch (user.getRole().toLowerCase()) {
                case "admin":
                    fxmlFile = "/fxml/AdminDashboard.fxml";
                    break;
                case "client":
                    fxmlFile = "/fxml/ClientDashboard.fxml";
                    break;
                case "organisateur":
                    fxmlFile = "/fxml/OrganisateurDashboard.fxml";
                    break;
                default:
                    showError("Invalid user role");
                    return;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            
            // Pass user data to dashboard controller based on role
            String role = user.getRole().toLowerCase();
            if ("admin".equals(role)) {
                AdminDashboardController controller = loader.getController();
                controller.setCurrentUser(user);
            } else if ("organisateur".equals(role)) {
                OrganisateurDashboardController controller = loader.getController();
                controller.setCurrentUser(user);
            } else {
                ClientDashboardController controller = loader.getController();
                controller.setCurrentUser(user);
            }
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(getTitle(user.getRole()));
            
        } catch (Exception e) {
            showError("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getTitle(String role) {
        if (role == null) return "Credix";
        switch (role.toLowerCase()) {
            case "admin": return "Credix - Admin Dashboard";
            case "organisateur": return "Credix - Organisateur Dashboard";
            default: return "Credix - Client Dashboard";
        }
    }
    
    @FXML
    private void handleSignUpNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Sign Up");
        } catch (Exception e) {
            showError("Error loading signup page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private boolean isValidEmail(String email) {
        // More comprehensive email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    @FXML
    private void initialize() {
        // Add input listeners for real-time validation feedback
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (errorLabel.isVisible() && !newValue.trim().isEmpty()) {
                errorLabel.setVisible(false);
            }
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (errorLabel.isVisible() && !newValue.trim().isEmpty()) {
                errorLabel.setVisible(false);
            }
        });
    }
}
