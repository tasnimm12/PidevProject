package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Abonnement;
import tn.esprit.services.AbonnementServices;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifyAbonnementDialogController implements Initializable {
    
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField prixField;
    @FXML private ComboBox<String> dureeComboBox;
    @FXML private Label errorLabel;
    
    private AbonnementServices abonnementServices;
    private Abonnement abonnementToModify;
    private boolean abonnementModified = false;
    
    public ModifyAbonnementDialogController() {
        abonnementServices = new AbonnementServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.getItems().addAll("Bronze", "Silver", "Gold", "Platinum");
        dureeComboBox.getItems().addAll("mensuel", "annuel");
    }
    
    public void setAbonnement(Abonnement abon) {
        this.abonnementToModify = abon;
        
        typeComboBox.setValue(abon.getTypeAbonnement());
        prixField.setText(String.valueOf(abon.getPrixMensuel()));
        dureeComboBox.setValue(abon.getDuree());
    }
    
    @FXML
    private void handleUpdate() {
        if (!validateForm()) {
            return;
        }
        
        try {
            java.math.BigDecimal prixMensuel = new java.math.BigDecimal(prixField.getText().trim());
            java.math.BigDecimal prixAnnuel = prixMensuel.multiply(new java.math.BigDecimal("12"));
            
            abonnementToModify.setTypeAbonnement(typeComboBox.getValue());
            abonnementToModify.setPrixMensuel(prixMensuel);
            abonnementToModify.setPrixAnnuel(prixAnnuel);
            abonnementToModify.setDuree(dureeComboBox.getValue());
            
            abonnementServices.modifier(abonnementToModify);
            abonnementModified = true;
            closeDialog();
            
        } catch (SQLException e) {
            showError("Error updating abonnement: " + e.getMessage());
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
    
    public boolean isAbonnementModified() {
        return abonnementModified;
    }
}
