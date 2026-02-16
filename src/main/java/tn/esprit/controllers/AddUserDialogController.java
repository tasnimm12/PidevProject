package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserServices;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddUserDialogController implements Initializable {
    
    @FXML private TextField prenomField;
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label errorLabel;
    
    private UserServices userServices;
    private boolean userAdded = false;
    
    public AddUserDialogController() {
        userServices = new UserServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.getItems().addAll("admin", "client", "organisateur");
        roleComboBox.setValue("client");
        
        statusComboBox.getItems().addAll("actif", "desactive");
        statusComboBox.setValue("actif");
    }
    
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Check if email exists
            if (emailExists(emailField.getText().trim())) {
                showError("Email already exists");
                return;
            }
            
            User newUser = new User(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText().trim(),
                telephoneField.getText().trim(),
                dateNaissancePicker.getValue(),
                roleComboBox.getValue(),
                statusComboBox.getValue()
            );
            
            userServices.ajouter(newUser);
            userAdded = true;
            closeDialog();
            
        } catch (SQLException e) {
            showError("Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private boolean validateForm() {
        errorLabel.setVisible(false);
        
        if (prenomField.getText().trim().isEmpty()) {
            showError("First name is required");
            return false;
        }
        
        if (!prenomField.getText().trim().matches("^[a-zA-Z\\s]+$")) {
            showError("First name should contain only letters");
            return false;
        }
        
        if (nomField.getText().trim().isEmpty()) {
            showError("Last name is required");
            return false;
        }
        
        if (!nomField.getText().trim().matches("^[a-zA-Z\\s]+$")) {
            showError("Last name should contain only letters");
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showError("Email is required");
            return false;
        }
        
        if (!isValidEmail(emailField.getText().trim())) {
            showError("Invalid email format");
            return false;
        }
        
        if (telephoneField.getText().trim().isEmpty()) {
            showError("Phone is required");
            return false;
        }
        
        if (!isValidPhone(telephoneField.getText().trim())) {
            showError("Invalid phone number");
            return false;
        }
        
        if (dateNaissancePicker.getValue() == null) {
            showError("Date of birth is required");
            return false;
        }
        
        if (dateNaissancePicker.getValue().isAfter(LocalDate.now().minusYears(18))) {
            showError("User must be at least 18 years old");
            return false;
        }
        
        if (passwordField.getText().trim().isEmpty()) {
            showError("Password is required");
            return false;
        }
        
        if (passwordField.getText().trim().length() < 6) {
            showError("Password must be at least 6 characters");
            return false;
        }
        
        return true;
    }
    
    private boolean emailExists(String email) throws SQLException {
        return userServices.afficher().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    private boolean isValidPhone(String phone) {
        String cleanPhone = phone.replaceAll("[\\s\\-()]", "");
        return cleanPhone.matches("^\\+?[0-9]{8,15}$");
    }
    
    private void closeDialog() {
        Stage stage = (Stage) prenomField.getScene().getWindow();
        stage.close();
    }
    
    public boolean isUserAdded() {
        return userAdded;
    }
}
