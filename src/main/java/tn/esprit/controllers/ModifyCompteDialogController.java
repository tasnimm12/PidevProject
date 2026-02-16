package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.CompteBancaire;
import tn.esprit.entities.User;
import tn.esprit.services.CompteBancaireServices;
import tn.esprit.services.UserServices;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ModifyCompteDialogController implements Initializable {
    
    @FXML private ComboBox<String> userComboBox;
    @FXML private Label userHintLabel;
    @FXML private TextField numeroCompteField;
    @FXML private TextField nomTitulaireField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField soldeField;
    @FXML private ComboBox<String> deviseComboBox;
    @FXML private ComboBox<String> typeCompteComboBox;
    @FXML private CheckBox actifCheckBox;
    
    private CompteBancaireServices compteServices;
    private UserServices userServices;
    private CompteBancaire compte;
    private Runnable onSaveCallback;
    
    public ModifyCompteDialogController() {
        compteServices = new CompteBancaireServices();
        userServices = new UserServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Currency options
        deviseComboBox.getItems().addAll("USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "CNY", "TND");
        
        // Account type options
        typeCompteComboBox.getItems().addAll("CHECKING", "SAVINGS", "INVESTMENT", "BUSINESS", "CREDIT");
        
        loadUsers();
    }
    
    private void loadUsers() {
        try {
            List<User> users = userServices.afficher();
            for (User user : users) {
                userComboBox.getItems().add(user.getPrenom() + " " + user.getNom() + " (" + user.getEmail() + ")");
            }
        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void setCompte(CompteBancaire compte) {
        this.compte = compte;
        
        // Load user info
        try {
            User user = userServices.getById(compte.getUserId());
            if (user != null) {
                userComboBox.setValue(user.getPrenom() + " " + user.getNom() + " (" + user.getEmail() + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Fill form
        numeroCompteField.setText(compte.getNumeroCompte());
        nomTitulaireField.setText(compte.getTitulaire());
        emailField.setText(compte.getEmail());
        telephoneField.setText(compte.getTelephone());
        soldeField.setText(compte.getSolde().toString());
        deviseComboBox.setValue(compte.getDevise());
        typeCompteComboBox.setValue(compte.getTypeCompte());
        actifCheckBox.setSelected(compte.isActif());
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    @FXML
    private void handleSave() {
        if (!validateFields()) {
            return;
        }
        
        try {
            compte.setNumeroCompte(numeroCompteField.getText().trim());
            compte.setTitulaire(nomTitulaireField.getText().trim());
            compte.setEmail(emailField.getText().trim());
            compte.setTelephone(telephoneField.getText().trim());
            compte.setSolde(new BigDecimal(soldeField.getText().trim()));
            compte.setDevise(deviseComboBox.getValue());
            compte.setTypeCompte(typeCompteComboBox.getValue());
            compte.setActif(actifCheckBox.isSelected());
            
            compteServices.modifier(compte);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            closeDialog();
            
        } catch (SQLException e) {
            String errorMsg = parseErrorMessage(e);
            showError(errorMsg);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showError("Invalid balance format. Please enter a valid number.");
        }
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        if (numeroCompteField.getText().trim().isEmpty()) {
            errors.append("• Account number is required\n");
        } else if (numeroCompteField.getText().trim().length() < 10 || numeroCompteField.getText().trim().length() > 20) {
            errors.append("• Account number must be 10-20 characters\n");
        }
        
        if (nomTitulaireField.getText().trim().isEmpty()) {
            errors.append("• Account holder name is required\n");
        } else if (nomTitulaireField.getText().trim().length() < 3) {
            errors.append("• Holder name must be at least 3 characters\n");
        }
        
        if (!emailField.getText().trim().isEmpty() && !emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.append("• Invalid email format\n");
        }
        
        if (!telephoneField.getText().trim().isEmpty() && !telephoneField.getText().matches("^\\+?[0-9]{8,15}$")) {
            errors.append("• Invalid phone format (8-15 digits, optional +)\n");
        }
        
        if (soldeField.getText().trim().isEmpty()) {
            errors.append("• Balance is required\n");
        } else {
            try {
                BigDecimal balance = new BigDecimal(soldeField.getText().trim());
                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                    errors.append("• Balance cannot be negative\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Invalid balance format\n");
            }
        }
        
        if (deviseComboBox.getValue() == null) {
            errors.append("• Currency is required\n");
        }
        
        if (typeCompteComboBox.getValue() == null) {
            errors.append("• Account type is required\n");
        }
        
        if (errors.length() > 0) {
            showError("Please fix the following errors:\n\n" + errors.toString());
            return false;
        }
        
        return true;
    }
    
    private String parseErrorMessage(SQLException e) {
        String message = e.getMessage().toLowerCase();
        
        if (message.contains("duplicate") && message.contains("numero_compte")) {
            return "❌ Account number already exists!\n\nPlease use a different account number.";
        } else if (message.contains("foreign key")) {
            return "❌ Invalid user reference!\n\nThe selected user does not exist.";
        } else if (message.contains("data too long")) {
            return "❌ Data too long!\n\nPlease shorten your input.";
        }
        
        return "Error updating account: " + e.getMessage();
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) numeroCompteField.getScene().getWindow();
        stage.close();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
