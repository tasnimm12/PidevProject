package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Assurance;
import tn.esprit.entities.ContratAssurance;
import tn.esprit.entities.User;
import tn.esprit.services.ContratAssuranceServices;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

public class SubscribeAssuranceDialogController implements Initializable {
    
    @FXML private Label insuranceInfoLabel;
    @FXML private Label insurancePremiumLabel;
    @FXML private TextField numeroContratField;
    @FXML private DatePicker dateSignaturePicker;
    @FXML private TextField dureeContratField;
    @FXML private TextField plafondAnnuelField;
    @FXML private TextField tauxRemboursementField;
    @FXML private TextField delaiCarenceField;
    @FXML private TextArea clauseBeneficiaireTextArea;
    @FXML private Label errorLabel;
    
    private ContratAssuranceServices contratServices;
    private Assurance assurance;
    private User user;
    private boolean subscribed = false;
    private NumberFormat currencyFormat;
    
    public SubscribeAssuranceDialogController() {
        contratServices = new ContratAssuranceServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set default signature date to today
        dateSignaturePicker.setValue(LocalDate.now());
        
        // Set default duration to 12 months
        dureeContratField.setText("12");
    }
    
    public void setAssuranceAndUser(Assurance assurance, User user) {
        this.assurance = assurance;
        this.user = user;
        
        // Display insurance info
        insuranceInfoLabel.setText(formatTypeLabel(assurance.getTypeAssurance()) + " - " + assurance.getCompagnie());
        insurancePremiumLabel.setText("Premium: " + currencyFormat.format(assurance.getPrimeMensuelle()) + "/month | " +
                                     currencyFormat.format(assurance.getPrimeAnnuelle()) + "/year");
        
        // Generate default contract number
        String contractNum = "CNT-" + assurance.getTypeAssurance().substring(0, 3) + "-" + 
                           user.getIdUser() + "-" + System.currentTimeMillis();
        numeroContratField.setText(contractNum);
    }
    
    @FXML
    private void handleSubscribe() {
        if (validateFields()) {
            try {
                // Check user has active bank account with sufficient balance
                double premium = assurance.getPrimeMensuelle().doubleValue();
                
                tn.esprit.services.CompteBancaireServices compteServices = new tn.esprit.services.CompteBancaireServices();
                java.util.List<tn.esprit.entities.CompteBancaire> userAccounts = compteServices.getByUserId(user.getIdUser());
                
                tn.esprit.entities.CompteBancaire activeAccount = userAccounts.stream()
                    .filter(c -> c.isActif() && c.getSolde().compareTo(BigDecimal.valueOf(premium)) >= 0)
                    .findFirst()
                    .orElse(null);
                
                if (activeAccount == null) {
                    showError("❌ Insufficient funds!\n\nPlease ensure you have an active bank account with balance >= " + 
                            currencyFormat.format(premium) + "\n\nGo to 'My Accounts' to create or fund your account.");
                    return;
                }
                
                // Calculate end date based on duration
                LocalDate startDate = dateSignaturePicker.getValue();
                int duration = Integer.parseInt(dureeContratField.getText().trim());
                LocalDate endDate = startDate.plusMonths(duration);
                
                // Create contract
                Integer duree = dureeContratField.getText().trim().isEmpty() ? null : Integer.parseInt(dureeContratField.getText().trim());
                Integer delai = delaiCarenceField.getText().trim().isEmpty() ? null : Integer.parseInt(delaiCarenceField.getText().trim());
                BigDecimal plafond = plafondAnnuelField.getText().trim().isEmpty() ? null : new BigDecimal(plafondAnnuelField.getText().trim());
                BigDecimal taux = tauxRemboursementField.getText().trim().isEmpty() ? null : new BigDecimal(tauxRemboursementField.getText().trim());
                
                ContratAssurance contrat = new ContratAssurance(
                    assurance.getId(),
                    user.getIdUser(), // utilisateur_id
                    numeroContratField.getText().trim(),
                    startDate,
                    endDate,
                    duree,
                    null, // conditions_particulieres
                    null, // exclusions
                    plafond,
                    taux,
                    delai,
                    clauseBeneficiaireTextArea.getText().trim().isEmpty() ? null : clauseBeneficiaireTextArea.getText().trim(),
                    null, // document_contrat
                    null, // amendements
                    null, // conseiller_attribue
                    null, // contacts
                    "ACTIF" // status
                );
                
                contratServices.ajouter(contrat);
                
                // Deduct premium from account balance
                activeAccount.setSolde(activeAccount.getSolde().subtract(BigDecimal.valueOf(premium)));
                compteServices.modifier(activeAccount);
                
                // Create expense record
                tn.esprit.services.DepenseServices depenseServices = new tn.esprit.services.DepenseServices();
                tn.esprit.entities.Depense depense = new tn.esprit.entities.Depense(
                    "Insurance Subscription: " + assurance.getTypeAssurance() + " - " + assurance.getCompagnie(),
                    BigDecimal.valueOf(premium),
                    LocalDate.now(),
                    "Insurance",
                    "Auto Debit",
                    activeAccount.getId()
                );
                depenseServices.ajouter(depense);
                
                subscribed = true;
                closeDialog();
                
            } catch (SQLException e) {
                String errorMsg = e.getMessage();
                if (errorMsg.contains("Duplicate entry")) {
                    showError("❌ Contract Number already exists!\n\nPlease use a different contract number.");
                } else {
                    showError("❌ Error creating contract!\n\n" + errorMsg);
                }
            } catch (NumberFormatException e) {
                showError("❌ Invalid Number Format!\n\nPlease check that Duration, Annual Cap, Refund Rate, and Waiting Period contain valid numbers.");
            }
        }
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        errors.append("⚠️ Please fix the following errors:\n\n");
        int errorCount = 0;
        
        if (numeroContratField.getText().trim().isEmpty()) {
            errorCount++;
            errors.append("• Contract Number: Contract number is required\n");
        } else if (numeroContratField.getText().trim().length() < 3) {
            errorCount++;
            errors.append("• Contract Number: Must be at least 3 characters\n");
        }
        
        if (dateSignaturePicker.getValue() == null) {
            errorCount++;
            errors.append("• Signature Date: Please select a signature date\n");
        }
        
        if (dureeContratField.getText().trim().isEmpty()) {
            errorCount++;
            errors.append("• Duration: Contract duration is required\n");
        } else {
            try {
                int duree = Integer.parseInt(dureeContratField.getText().trim());
                if (duree <= 0) {
                    errorCount++;
                    errors.append("• Duration: Must be greater than 0 months\n");
                } else if (duree > 600) {
                    errorCount++;
                    errors.append("• Duration: Maximum duration is 600 months (50 years)\n");
                }
            } catch (NumberFormatException e) {
                errorCount++;
                errors.append("• Duration: Must be a valid whole number (e.g., 12)\n");
            }
        }
        
        if (!plafondAnnuelField.getText().trim().isEmpty()) {
            try {
                BigDecimal plafond = new BigDecimal(plafondAnnuelField.getText().trim());
                if (plafond.compareTo(BigDecimal.ZERO) < 0) {
                    errorCount++;
                    errors.append("• Annual Cap: Cannot be negative\n");
                }
            } catch (NumberFormatException e) {
                errorCount++;
                errors.append("• Annual Cap: Must be a valid number (e.g., 50000.00)\n");
            }
        }
        
        if (!tauxRemboursementField.getText().trim().isEmpty()) {
            try {
                BigDecimal taux = new BigDecimal(tauxRemboursementField.getText().trim());
                if (taux.compareTo(BigDecimal.ZERO) < 0 || taux.compareTo(new BigDecimal("100")) > 0) {
                    errorCount++;
                    errors.append("• Refund Rate: Must be between 0 and 100\n");
                }
            } catch (NumberFormatException e) {
                errorCount++;
                errors.append("• Refund Rate: Must be a valid number (e.g., 80.5)\n");
            }
        }
        
        if (!delaiCarenceField.getText().trim().isEmpty()) {
            try {
                int delai = Integer.parseInt(delaiCarenceField.getText().trim());
                if (delai < 0) {
                    errorCount++;
                    errors.append("• Waiting Period: Cannot be negative\n");
                } else if (delai > 365) {
                    errorCount++;
                    errors.append("• Waiting Period: Maximum waiting period is 365 days\n");
                }
            } catch (NumberFormatException e) {
                errorCount++;
                errors.append("• Waiting Period: Must be a valid whole number (e.g., 30)\n");
            }
        }
        
        if (errorCount > 0) {
            showError(errors.toString());
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) numeroContratField.getScene().getWindow();
        stage.close();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private String formatTypeLabel(String type) {
        if (type == null) return "";
        return type.replace("_", " ");
    }
    
    public boolean isSubscribed() {
        return subscribed;
    }
}
