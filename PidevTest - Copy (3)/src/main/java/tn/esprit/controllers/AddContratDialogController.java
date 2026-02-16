package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Assurance;
import tn.esprit.entities.ContratAssurance;
import tn.esprit.services.AssuranceServices;
import tn.esprit.services.ContratAssuranceServices;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AddContratDialogController implements Initializable {
    
    @FXML private ComboBox<Assurance> assuranceComboBox;
    @FXML private TextField numeroContratField;
    @FXML private DatePicker dateSignaturePicker;
    @FXML private DatePicker dateFinContratPicker;
    @FXML private TextField dureeContratField;
    @FXML private TextField plafondAnnuelField;
    @FXML private TextField tauxRemboursementField;
    @FXML private TextField delaiCarenceField;
    @FXML private TextField conseillerField;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private TextArea conditionsTextArea;
    @FXML private TextArea exclusionsTextArea;
    @FXML private TextArea clauseBeneficiaireTextArea;
    @FXML private TextField contactsField;
    @FXML private Label errorLabel;
    
    private ContratAssuranceServices contratServices;
    private AssuranceServices assuranceServices;
    private boolean saved = false;
    
    public AddContratDialogController() {
        contratServices = new ContratAssuranceServices();
        assuranceServices = new AssuranceServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize status combo box
        statutComboBox.getItems().addAll("ACTIF", "EXPIRE", "RESILIE", "EN_ATTENTE");
        statutComboBox.setValue("EN_ATTENTE");
        
        // Load assurances
        loadAssurances();
        
        // Set up assurance combo box display
        assuranceComboBox.setConverter(new javafx.util.StringConverter<Assurance>() {
            @Override
            public String toString(Assurance a) {
                return a != null ? a.getTypeAssurance() + " - " + a.getCompagnie() + " (" + a.getNumeroPolice() + ")" : "";
            }
            
            @Override
            public Assurance fromString(String string) {
                return null;
            }
        });
    }
    
    private void loadAssurances() {
        try {
            List<Assurance> assurances = assuranceServices.afficher();
            assuranceComboBox.getItems().addAll(assurances);
        } catch (SQLException e) {
            showError("Error loading assurances: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSave() {
        if (validateFields()) {
            try {
                Assurance selectedAssurance = assuranceComboBox.getValue();
                Integer duree = dureeContratField.getText().trim().isEmpty() ? null : Integer.parseInt(dureeContratField.getText().trim());
                Integer delai = delaiCarenceField.getText().trim().isEmpty() ? null : Integer.parseInt(delaiCarenceField.getText().trim());
                BigDecimal plafond = plafondAnnuelField.getText().trim().isEmpty() ? null : new BigDecimal(plafondAnnuelField.getText().trim());
                BigDecimal taux = tauxRemboursementField.getText().trim().isEmpty() ? null : new BigDecimal(tauxRemboursementField.getText().trim());
                
                ContratAssurance contrat = new ContratAssurance(
                    selectedAssurance.getId(),
                    selectedAssurance.getUtilisateurId(), // utilisateur_id
                    numeroContratField.getText().trim(),
                    dateSignaturePicker.getValue(),
                    dateFinContratPicker.getValue(),
                    duree,
                    conditionsTextArea.getText().trim(),
                    exclusionsTextArea.getText().trim(),
                    plafond,
                    taux,
                    delai,
                    clauseBeneficiaireTextArea.getText().trim(),
                    null, // document
                    null, // amendements
                    conseillerField.getText().trim(),
                    contactsField.getText().trim(),
                    statutComboBox.getValue()
                );
                
                contratServices.ajouter(contrat);
                saved = true;
                closeDialog();
                
            } catch (SQLException e) {
                String errorMsg = e.getMessage();
                if (errorMsg.contains("Duplicate entry")) {
                    if (errorMsg.contains("numero_contrat")) {
                        showError("❌ Contract Number already exists!\n\nThis contract number is already in the database.\nPlease use a different contract number.");
                    } else {
                        showError("❌ Duplicate Entry Error!\n\n" + errorMsg);
                    }
                } else if (errorMsg.contains("Data truncation") || errorMsg.contains("Incorrect") || errorMsg.contains("Invalid")) {
                    // Parse which field has the issue
                    String specificError = "❌ Invalid Data Format!\n\n";
                    
                    if (errorMsg.contains("statut")) {
                        specificError += "Status field has invalid data!\n\n";
                        specificError += "Valid statuses are:\n";
                        specificError += "• ACTIF\n• EXPIRE\n• RESILIE\n• EN_ATTENTE\n\n";
                        specificError += "Please select from the dropdown list.";
                    } else if (errorMsg.contains("numero_contrat")) {
                        specificError += "Contract Number is too long!\n\n";
                        specificError += "Maximum length: 100 characters\n";
                        specificError += "Current length: " + numeroContratField.getText().length() + " characters\n\n";
                        specificError += "Please shorten the contract number.";
                    } else if (errorMsg.contains("conseiller_attribue")) {
                        specificError += "Adviser Name is too long!\n\n";
                        specificError += "Maximum length: 150 characters\n";
                        if (!conseillerField.getText().trim().isEmpty()) {
                            specificError += "Current length: " + conseillerField.getText().length() + " characters\n\n";
                        }
                        specificError += "Please shorten the adviser name.";
                    } else if (errorMsg.contains("duree_contrat")) {
                        specificError += "Duration field has invalid format!\n\n";
                        specificError += "Please enter:\n";
                        specificError += "• A whole number (no decimals)\n";
                        specificError += "• Duration in months\n";
                        specificError += "• Example: 12 (for 12 months)\n";
                        specificError += "• Maximum: 600 months\n\n";
                        specificError += "Don't include text like 'months' - just the number.";
                    } else if (errorMsg.contains("plafond_annuel") || errorMsg.contains("taux_remboursement")) {
                        specificError += "Numeric field has invalid format!\n\n";
                        specificError += "Please check these fields:\n";
                        specificError += "• Annual Cap: e.g., 50000.00\n";
                        specificError += "• Refund Rate: e.g., 80.5 (percentage)\n\n";
                        specificError += "- Use only numbers and decimal point\n";
                        specificError += "- Maximum 2 decimal places\n";
                        specificError += "- No currency symbols ($, €, %)\n";
                        specificError += "- No commas or spaces";
                    } else if (errorMsg.contains("delai_carence")) {
                        specificError += "Waiting Period has invalid format!\n\n";
                        specificError += "Please enter:\n";
                        specificError += "• A whole number (no decimals)\n";
                        specificError += "• Waiting period in days\n";
                        specificError += "• Example: 30 (for 30 days)\n";
                        specificError += "• Maximum: 365 days\n\n";
                        specificError += "Don't include text like 'days' - just the number.";
                    } else if (errorMsg.contains("date")) {
                        specificError += "Date field has invalid format!\n\n";
                        specificError += "Please check:\n";
                        specificError += "• Signature Date: Must be selected\n";
                        specificError += "• End Date: Must be selected\n";
                        specificError += "• End Date must be after Signature Date\n\n";
                        specificError += "Use the calendar picker to select dates.";
                    } else if (errorMsg.contains("conditions_particulieres")) {
                        specificError += "Special Conditions text is too long!\n\n";
                        specificError += "Maximum length: 65,535 characters\n";
                        specificError += "Current length: " + conditionsTextArea.getText().length() + " characters\n\n";
                        specificError += "Please shorten the special conditions text.";
                    } else if (errorMsg.contains("exclusions")) {
                        specificError += "Exclusions text is too long!\n\n";
                        specificError += "Maximum length: 65,535 characters\n";
                        specificError += "Current length: " + exclusionsTextArea.getText().length() + " characters\n\n";
                        specificError += "Please shorten the exclusions text.";
                    } else if (errorMsg.contains("JSON") || errorMsg.contains("amendements")) {
                        specificError += "Amendements field has invalid format!\n\n";
                        specificError += "This field is optional and can be:\n";
                        specificError += "• Left empty (recommended)\n";
                        specificError += "• Plain text description\n";
                        specificError += "• Valid JSON format (advanced)\n\n";
                        specificError += "If you're not sure, just leave it empty.";
                    } else {
                        specificError += "One or more fields contain invalid data.\n\n";
                        specificError += "Common issues:\n";
                        specificError += "• Text too long for the field\n";
                        specificError += "• Invalid characters or format\n";
                        specificError += "• Required dropdown not selected\n\n";
                        specificError += "Technical error:\n" + errorMsg;
                    }
                    
                    showError(specificError);
                } else if (errorMsg.contains("foreign key") || errorMsg.contains("constraint")) {
                    showError("❌ Invalid Assurance Selection!\n\nThe selected assurance doesn't exist or has been deleted.\nPlease select a valid assurance from the list.");
                } else {
                    showError("❌ Database Error!\n\n" + errorMsg + "\n\nPlease check your input and try again.");
                }
            } catch (NumberFormatException e) {
                showError("❌ Invalid Number Format!\n\nPlease check these numeric fields:\n\n" +
                         "• Duration: Enter whole numbers only (e.g., 12)\n" +
                         "• Annual Cap: Enter numbers only (e.g., 50000.00)\n" +
                         "• Refund Rate: Enter numbers only (e.g., 80.5)\n" +
                         "• Waiting Period: Enter whole numbers only (e.g., 30)\n\n" +
                         "Tips:\n" +
                         "- Don't use currency symbols ($, €, %)\n" +
                         "- Don't use commas (,)\n" +
                         "- Use period (.) for decimals where applicable");
            }
        }
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        errors.append("⚠️ Please fix the following errors:\n\n");
        int errorCount = 0;
        
        if (assuranceComboBox.getValue() == null) {
            errorCount++;
            errors.append("• Assurance: Please select an assurance policy\n");
        }
        
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
        
        if (dateFinContratPicker.getValue() == null) {
            errorCount++;
            errors.append("• End Date: Please select an end date\n");
        }
        
        if (dateSignaturePicker.getValue() != null && dateFinContratPicker.getValue() != null) {
            if (dateFinContratPicker.getValue().isBefore(dateSignaturePicker.getValue())) {
                errorCount++;
                errors.append("• Dates: End date must be after signature date\n");
            }
            if (dateFinContratPicker.getValue().equals(dateSignaturePicker.getValue())) {
                errorCount++;
                errors.append("• Dates: End date cannot be the same as signature date\n");
            }
        }
        
        if (!dureeContratField.getText().trim().isEmpty()) {
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
                } else if (plafond.compareTo(new BigDecimal("1000000000")) > 0) {
                    errorCount++;
                    errors.append("• Annual Cap: Maximum value is 1,000,000,000\n");
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
        
        if (statutComboBox.getValue() == null || statutComboBox.getValue().isEmpty()) {
            errorCount++;
            errors.append("• Status: Please select a status\n");
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
    
    public boolean isSaved() {
        return saved;
    }
}
