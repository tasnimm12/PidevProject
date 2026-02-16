package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.*;
import tn.esprit.services.*;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

public class ModifyCreditDialogController implements Initializable {
    
    @FXML private ComboBox<String> userComboBox;
    @FXML private ComboBox<String> compteComboBox;
    @FXML private TextField montantRestantField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea motifField;
    @FXML private Label totalLabel;
    @FXML private Label mensualiteLabel;
    
    private CreditServices creditServices;
    private UserServices userServices;
    private CompteBancaireServices compteServices;
    private NumberFormat currencyFormat;
    private Credit credit;
    private Runnable onSaveCallback;
    
    public ModifyCreditDialogController() {
        creditServices = new CreditServices();
        userServices = new UserServices();
        compteServices = new CompteBancaireServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusComboBox.getItems().addAll("EN_ATTENTE", "ACCEPTE", "REFUSE", "EN_COURS", "TERMINE", "EN_RETARD");
    }
    
    public void setCredit(Credit credit) {
        this.credit = credit;
        
        // Load user
        try {
            User user = userServices.getById(credit.getUserId());
            if (user != null) {
                userComboBox.getItems().add(user.getPrenom() + " " + user.getNom() + " (" + user.getEmail() + ")");
                userComboBox.setValue(user.getPrenom() + " " + user.getNom() + " (" + user.getEmail() + ")");
            }
            
            CompteBancaire compte = compteServices.afficher().stream()
                .filter(c -> c.getId() == credit.getCompteId())
                .findFirst().orElse(null);
            if (compte != null) {
                String display = compte.getNumeroCompte() + " - " + compte.getTitulaire();
                compteComboBox.getItems().add(display);
                compteComboBox.setValue(display);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Fill fields
        montantRestantField.setText(credit.getMontantRestant().toString());
        statusComboBox.setValue(credit.getStatutCredit());
        motifField.setText(credit.getMotifRefus());
        totalLabel.setText(currencyFormat.format(credit.getMontantTotal()));
        mensualiteLabel.setText(currencyFormat.format(credit.getMensualite()));
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    @FXML
    private void handleSave() {
        if (!validateFields()) return;
        
        try {
            credit.setMontantRestant(new BigDecimal(montantRestantField.getText().trim()));
            credit.setStatutCredit(statusComboBox.getValue());
            credit.setMotifRefus(motifField.getText().trim().isEmpty() ? null : motifField.getText().trim());
            
            // If status changed to ACCEPTE, set dates
            if ("ACCEPTE".equals(statusComboBox.getValue()) && credit.getDateDebut() == null) {
                credit.setDateDebut(LocalDate.now());
                int months = Integer.parseInt(credit.getTypeCredit().replace("M", ""));
                credit.setDateFin(LocalDate.now().plusMonths(months));
            }
            
            creditServices.modifier(credit);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            closeDialog();
            
        } catch (SQLException e) {
            showError("Error updating credit: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        if (montantRestantField.getText().trim().isEmpty()) {
            errors.append("• Remaining amount is required\n");
        } else {
            try {
                BigDecimal amount = new BigDecimal(montantRestantField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    errors.append("• Remaining amount cannot be negative\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Invalid amount format\n");
            }
        }
        
        if (statusComboBox.getValue() == null) {
            errors.append("• Status is required\n");
        }
        
        if ("REFUSE".equals(statusComboBox.getValue()) && motifField.getText().trim().isEmpty()) {
            errors.append("• Refusal reason is required when refusing a credit\n");
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
        Stage stage = (Stage) montantRestantField.getScene().getWindow();
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
