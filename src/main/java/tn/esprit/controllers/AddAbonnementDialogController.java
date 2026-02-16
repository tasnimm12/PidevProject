package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Abonnement;
import tn.esprit.services.AbonnementServices;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddAbonnementDialogController implements Initializable {
    
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField prixField;
    @FXML private ComboBox<String> dureeComboBox;
    @FXML private Label errorLabel;
    
    private AbonnementServices abonnementServices;
    private boolean abonnementAdded = false;
    
    public AddAbonnementDialogController() {
        abonnementServices = new AbonnementServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.getItems().addAll("Bronze", "Silver", "Gold", "Platinum");
        typeComboBox.setValue("Bronze");
        
        dureeComboBox.getItems().addAll("mensuel", "annuel");
        dureeComboBox.setValue("mensuel");
    }
    
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            java.math.BigDecimal prixMensuel = new java.math.BigDecimal(prixField.getText().trim());
            java.math.BigDecimal prixAnnuel = prixMensuel.multiply(new java.math.BigDecimal("12"));
            
            Abonnement newAbonnement = new Abonnement(
                typeComboBox.getValue(),
                prixMensuel,
                prixAnnuel,
                dureeComboBox.getValue(),
                "Plan description", // description
                "Plan advantages", // avantages
                true // actif
            );
            
            abonnementServices.ajouter(newAbonnement);
            abonnementAdded = true;
            closeDialog();
            
        } catch (SQLException e) {
            showError("Error adding abonnement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private boolean validateForm() {
        errorLabel.setVisible(false);
        
        if (typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty()) {
            showError("Type is required");
            return false;
        }
        
        if (prixField.getText().trim().isEmpty()) {
            showError("Price is required");
            return false;
        }
        
        try {
            double prix = Double.parseDouble(prixField.getText().trim());
            if (prix <= 0) {
                showError("Price must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Invalid price format");
            return false;
        }
        
        if (dureeComboBox.getValue() == null || dureeComboBox.getValue().isEmpty()) {
            showError("Duration type is required");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void closeDialog() {
        Stage stage = (Stage) typeComboBox.getScene().getWindow();
        stage.close();
    }
    
    public boolean isAbonnementAdded() {
        return abonnementAdded;
    }
}
