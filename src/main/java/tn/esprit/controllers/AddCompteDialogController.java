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

public class AddCompteDialogController implements Initializable {
    
    @FXML private ComboBox<User> userComboBox;
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
    private UserServices userServices;
    private boolean success = false;
    
    public AddCompteDialogController() {
        compteServices = new CompteBancaireServices();
        userServices = new UserServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUsers();
        
        deviseComboBox.getItems().addAll("USD", "EUR", "TND", "GBP", "CAD");
        deviseComboBox.setValue("USD");
        
        typeComboBox.getItems().addAll("Checking", "Savings", "Business", "Investment");
    }
    
    private void loadUsers() {
        try {
            List<User> users = userServices.afficher();
            userComboBox.getItems().addAll(users);
            
            userComboBox.setCellFactory(lv -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getPrenom() + " " + item.getNom() + " (" + item.getEmail() + ")");
                }
            });
            
            userComboBox.setButtonCell(new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getPrenom() + " " + item.getNom());
                }
            });
        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
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
            User selectedUser = userComboBox.getValue();
            String numero = numeroField.getText().trim();
            String titulaire = titulaireField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            BigDecimal solde = new BigDecimal(soldeField.getText().trim());
            String devise = deviseComboBox.getValue();
            String type = typeComboBox.getValue();
            boolean actif = actifCheckBox.isSelected();
            
            CompteBancaire compte = new CompteBancaire(
                selectedUser.getIdUser(), numero, titulaire, 
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone,
                solde, devise, type, LocalDate.now(), actif
            );
            
            compteServices.ajouter(compte);
            success = true;
            closeDialog();
            
        } catch (SQLException e) {
            String errorMsg = parseError(e);
            showError(errorMsg);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showError("Invalid balance amount.");
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private String validateFields() {
        if (userComboBox.getValue() == null) {
            return "Please select a user.";
        }
        
        if (numeroField.getText().trim().isEmpty()) {
            return "Account number is required.";
        }
        
        if (numeroField.getText().trim().length() < 10) {
            return "Account number must be at least 10 characters.";
        }
        
        if (titulaireField.getText().trim().isEmpty()) {
            return "Account holder name is required.";
        }
        
        if (deviseComboBox.getValue() == null) {
            return "Please select a currency.";
        }
        
        if (typeComboBox.getValue() == null) {
            return "Please select an account type.";
        }
        
        try {
            BigDecimal solde = new BigDecimal(soldeField.getText().trim());
            if (solde.compareTo(BigDecimal.ZERO) < 0) {
                return "Balance cannot be negative.";
            }
        } catch (NumberFormatException e) {
            return "Invalid balance amount.";
        }
        
        return null;
    }
    
    private String parseError(SQLException e) {
        String msg = e.getMessage().toLowerCase();
        
        if (msg.contains("duplicate entry") && msg.contains("numero_compte")) {
            return "This account number already exists.";
        }
        
        if (msg.contains("foreign key")) {
            return "Invalid user selected.";
        }
        
        return "Database error: " + e.getMessage();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
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
