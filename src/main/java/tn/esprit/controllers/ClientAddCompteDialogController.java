package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.CompteBancaire;
import tn.esprit.entities.User;
import tn.esprit.services.CompteBancaireServices;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ClientAddCompteDialogController implements Initializable {
    
    @FXML private TextField numeroField;
    @FXML private TextField titulaireField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField soldeField;
    @FXML private ComboBox<String> deviseComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private CheckBox actifCheckBox;
    @FXML private Label errorLabel;
    
    private CompteBancaireServices compteServices;
    private User currentUser;
    private boolean success = false;
    
    public ClientAddCompteDialogController() {
        compteServices = new CompteBancaireServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Currency options
        deviseComboBox.getItems().addAll("USD", "EUR", "TND", "GBP", "CAD", "AUD", "JPY", "CHF");
        deviseComboBox.setValue("USD");
        
        // Account types
        typeComboBox.getItems().addAll("Checking", "Savings", "Business", "Investment");
        
        // Auto-fill account holder from user when set
        titulaireField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused && titulaireField.getText().isEmpty() && currentUser != null) {
                titulaireField.setText(currentUser.getPrenom() + " " + currentUser.getNom());
            }
        });
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Auto-fill email from user profile if available
        if (user != null && user.getEmail() != null) {
            emailField.setPromptText(user.getEmail());
        }
    }
    
    @FXML
    private void handleAdd() {
        errorLabel.setVisible(false);
        
        String error = validateFields();
        if (error != null) {
            showError(error);
            return;
        }
        
        try {
            String numero = numeroField.getText().trim();
            String titulaire = titulaireField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            BigDecimal solde = new BigDecimal(soldeField.getText().trim());
            String devise = deviseComboBox.getValue();
            String type = typeComboBox.getValue();
            boolean actif = actifCheckBox.isSelected();
            
            CompteBancaire compte = new CompteBancaire(
                currentUser.getIdUser(), 
                numero, 
                titulaire, 
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone,
                solde, 
                devise, 
                type, 
                LocalDate.now(), 
                actif
            );
            
            compteServices.ajouter(compte);
            success = true;
            closeDialog();
            
        } catch (SQLException e) {
            String errorMsg = parseError(e);
            showError(errorMsg);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showError("Invalid balance amount. Please enter a valid number.");
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private String validateFields() {
        // Account number validation
        if (numeroField.getText().trim().isEmpty()) {
            return "Account number is required.";
        }
        
        String numero = numeroField.getText().trim();
        if (numero.length() < 10) {
            return "Account number must be at least 10 characters long.";
        }
        
        if (numero.length() > 50) {
            return "Account number must not exceed 50 characters.";
        }
        
        if (!numero.matches("[A-Za-z0-9]+")) {
            return "Account number can only contain letters and numbers.";
        }
        
        // Account holder validation
        if (titulaireField.getText().trim().isEmpty()) {
            return "Account holder name is required.";
        }
        
        if (titulaireField.getText().trim().length() < 3) {
            return "Account holder name must be at least 3 characters.";
        }
        
        if (titulaireField.getText().trim().length() > 100) {
            return "Account holder name must not exceed 100 characters.";
        }
        
        // Email validation (if provided)
        String email = emailField.getText().trim();
        if (!email.isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return "Please enter a valid email address.";
            }
        }
        
        // Phone validation (if provided)
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty()) {
            if (!phone.matches("^[+]?[0-9]{8,20}$")) {
                return "Please enter a valid phone number (8-20 digits, optional +).";
            }
        }
        
        // Currency validation
        if (deviseComboBox.getValue() == null || deviseComboBox.getValue().isEmpty()) {
            return "Please select a currency.";
        }
        
        // Account type validation
        if (typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty()) {
            return "Please select an account type.";
        }
        
        // Balance validation
        try {
            BigDecimal solde = new BigDecimal(soldeField.getText().trim());
            if (solde.compareTo(BigDecimal.ZERO) < 0) {
                return "Balance cannot be negative.";
            }
            if (solde.compareTo(new BigDecimal("999999999.99")) > 0) {
                return "Balance amount is too large.";
            }
        } catch (NumberFormatException e) {
            return "Invalid balance amount. Please enter a valid number (e.g., 1000.50).";
        }
        
        return null;
    }
    
    private String parseError(SQLException e) {
        String msg = e.getMessage().toLowerCase();
        
        if (msg.contains("duplicate entry") && msg.contains("numero_compte")) {
            return "This account number already exists. Please use a different account number.";
        }
        
        if (msg.contains("foreign key") && msg.contains("user_id")) {
            return "User account error. Please try logging in again.";
        }
        
        if (msg.contains("data too long")) {
            return "One or more fields exceed the maximum allowed length.";
        }
        
        return "Unable to create account. Please check your information and try again.";
    }
    
    private void showError(String message) {
        errorLabel.setText("⚠️ " + message);
        errorLabel.setVisible(true);
    }
    
    private void closeDialog() {
        Stage stage = (Stage) numeroField.getScene().getWindow();
        stage.close();
    }
    
    public boolean isSuccess() {
        return success;
    }
}
