package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Remboursement;
import tn.esprit.services.RemboursementServices;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifyRemboursementDialogController implements Initializable {
    
    @FXML private TextField typeField;
    @FXML private TextField montantField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;
    
    private RemboursementServices remboursementServices;
    private Remboursement remboursement;
    private Runnable onSaveCallback;
    
    public ModifyRemboursementDialogController() {
        remboursementServices = new RemboursementServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusComboBox.getItems().addAll("EN_ATTENTE", "COMPLETE", "ECHOUE");
    }
    
    public void setRemboursement(Remboursement remb) {
        this.remboursement = remb;
        
        typeField.setText(remb.getTypeRemboursement());
        montantField.setText(remb.getMontant().toString());
        datePicker.setValue(remb.getDateRemboursement());
        statusComboBox.setValue(remb.getStatut());
        descriptionField.setText(remb.getDescription());
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    @FXML
    private void handleSave() {
        if (!validateFields()) return;
        
        try {
            remboursement.setMontant(new BigDecimal(montantField.getText().trim()));
            remboursement.setDateRemboursement(datePicker.getValue());
            remboursement.setStatut(statusComboBox.getValue());
            remboursement.setDescription(descriptionField.getText().trim());
            
            remboursementServices.modifier(remboursement);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            closeDialog();
            
        } catch (SQLException e) {
            showError("Error updating remboursement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        if (montantField.getText().trim().isEmpty()) {
            errors.append("• Amount is required\n");
        } else {
            try {
                BigDecimal amount = new BigDecimal(montantField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.append("• Amount must be positive\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Invalid amount format\n");
            }
        }
        
        if (datePicker.getValue() == null) {
            errors.append("• Date is required\n");
        }
        
        if (statusComboBox.getValue() == null) {
            errors.append("• Status is required\n");
        }
        
        if (errors.length() > 0) {
            showError("Please fix the following errors:\n\n" + errors.toString());
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) montantField.getScene().getWindow();
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
