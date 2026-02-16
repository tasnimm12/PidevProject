package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Assurance;
import tn.esprit.entities.User;
import tn.esprit.services.AssuranceServices;
import tn.esprit.services.UserServices;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AddAssuranceDialogController implements Initializable {
    
    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField compagnieField;
    @FXML private TextField numeroPoliceField;
    @FXML private TextField montantCouvertureField;
    @FXML private TextField franchiseField;
    @FXML private TextField primeAnnuelleField;
    @FXML private TextField primeMensuelleField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateEcheancePicker;
    @FXML private ComboBox<String> modePaiementComboBox;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private CheckBox renouvellementAutoCheckBox;
    @FXML private TextArea garantiesTextArea;
    @FXML private Label errorLabel;
    
    private AssuranceServices assuranceServices;
    private UserServices userServices;
    private boolean saved = false;
    
    public AddAssuranceDialogController() {
        assuranceServices = new AssuranceServices();
        userServices = new UserServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize combo boxes
        typeComboBox.getItems().addAll("VIE", "SANTE", "AUTO", "HABITATION", 
                                       "RESPONSABILITE_CIVILE", "SCOLAIRE", "VOYAGE", "PROFESSIONNELLE");
        
        modePaiementComboBox.getItems().addAll("MENSUEL", "TRIMESTRIEL", "SEMESTRIEL", "ANNUEL");
        
        statutComboBox.getItems().addAll("ACTIF", "EXPIRE", "RESILIE", "SUSPENDU");
        statutComboBox.setValue("ACTIF");
        
        // Load users
        loadUsers();
        
        // Set up user combo box display
        userComboBox.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getPrenom() + " " + user.getNom() + " (" + user.getEmail() + ")" : "";
            }
            
            @Override
            public User fromString(String string) {
                return null;
            }
        });
    }
    
    private void loadUsers() {
        try {
            List<User> users = userServices.afficher();
            userComboBox.getItems().addAll(users);
        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSave() {
        if (validateFields()) {
            try {
                // Handle garanties field - use null if empty
                String garanties = garantiesTextArea.getText().trim();
                if (garanties.isEmpty()) {
                    garanties = null;
                }
                
                Assurance assurance = new Assurance(
                    userComboBox.getValue().getIdUser(),
                    typeComboBox.getValue(),
                    compagnieField.getText().trim(),
                    numeroPoliceField.getText().trim(),
                    new BigDecimal(montantCouvertureField.getText().trim()),
                    new BigDecimal(franchiseField.getText().trim()),
                    new BigDecimal(primeAnnuelleField.getText().trim()),
                    new BigDecimal(primeMensuelleField.getText().trim()),
                    dateDebutPicker.getValue(),
                    dateEcheancePicker.getValue(),
                    modePaiementComboBox.getValue(),
                    statutComboBox.getValue(),
                    renouvellementAutoCheckBox.isSelected(),
                    garanties
                );
                
                assuranceServices.ajouter(assurance);
                saved = true;
                closeDialog();
                
            } catch (SQLException e) {
                String errorMsg = e.getMessage();
                if (errorMsg.contains("Duplicate entry")) {
                    if (errorMsg.contains("numero_police")) {
                        showError("❌ Policy Number already exists!\n\nThis policy number is already in the database.\nPlease use a different policy number.");
                    } else {
                        showError("❌ Duplicate Entry Error!\n\n" + errorMsg);
                    }
                } else if (errorMsg.contains("Data truncation") || errorMsg.contains("Incorrect") || errorMsg.contains("Invalid")) {
                    // Parse which field has the issue
                    String specificError = "❌ Invalid Data Format!\n\n";
                    
                    if (errorMsg.contains("type_assurance")) {
                        specificError += "Insurance Type field has invalid data!\n\n";
                        specificError += "Valid types are:\n";
                        specificError += "• VIE\n• SANTE\n• AUTO\n• HABITATION\n";
                        specificError += "• RESPONSABILITE_CIVILE\n• SCOLAIRE\n• VOYAGE\n• PROFESSIONNELLE\n\n";
                        specificError += "Please select from the dropdown list.";
                    } else if (errorMsg.contains("mode_paiement")) {
                        specificError += "Payment Mode field has invalid data!\n\n";
                        specificError += "Valid payment modes are:\n";
                        specificError += "• MENSUEL\n• TRIMESTRIEL\n• SEMESTRIEL\n• ANNUEL\n\n";
                        specificError += "Please select from the dropdown list.";
                    } else if (errorMsg.contains("statut")) {
                        specificError += "Status field has invalid data!\n\n";
                        specificError += "Valid statuses are:\n";
                        specificError += "• ACTIF\n• EXPIRE\n• RESILIE\n• SUSPENDU\n\n";
                        specificError += "Please select from the dropdown list.";
                    } else if (errorMsg.contains("compagnie")) {
                        specificError += "Company Name is too long!\n\n";
                        specificError += "Maximum length: 150 characters\n";
                        specificError += "Current length: " + compagnieField.getText().length() + " characters\n\n";
                        specificError += "Please shorten the company name.";
                    } else if (errorMsg.contains("numero_police")) {
                        specificError += "Policy Number is too long!\n\n";
                        specificError += "Maximum length: 100 characters\n";
                        specificError += "Current length: " + numeroPoliceField.getText().length() + " characters\n\n";
                        specificError += "Please shorten the policy number.";
                    } else if (errorMsg.contains("montant_couverture") || errorMsg.contains("franchise") || 
                              errorMsg.contains("prime_annuelle") || errorMsg.contains("prime_mensuelle")) {
                        specificError += "Numeric field has invalid format!\n\n";
                        specificError += "Please check these fields:\n";
                        specificError += "• Coverage Amount: e.g., 50000.00\n";
                        specificError += "• Franchise: e.g., 500.00\n";
                        specificError += "• Annual Premium: e.g., 1200.00\n";
                        specificError += "• Monthly Premium: e.g., 100.00\n\n";
                        specificError += "- Use only numbers and decimal point\n";
                        specificError += "- Maximum 2 decimal places\n";
                        specificError += "- No currency symbols ($, €, etc.)\n";
                        specificError += "- No commas or spaces";
                    } else if (errorMsg.contains("date")) {
                        specificError += "Date field has invalid format!\n\n";
                        specificError += "Please check:\n";
                        specificError += "• Start Date: Must be selected\n";
                        specificError += "• Expiry Date: Must be selected\n";
                        specificError += "• Expiry Date must be after Start Date\n\n";
                        specificError += "Use the calendar picker to select dates.";
                    } else if (errorMsg.contains("JSON") || errorMsg.contains("garanties_incluses")) {
                        specificError += "Guarantees field has invalid format!\n\n";
                        specificError += "The Guarantees field can be:\n";
                        specificError += "• Left empty (recommended)\n";
                        specificError += "• Plain text description\n";
                        specificError += "• Valid JSON format (advanced)\n\n";
                        specificError += "If you're not sure, just leave it empty or use plain text.";
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
                    showError("❌ Invalid User Selection!\n\nThe selected user doesn't exist or has been deleted.\nPlease select a valid user from the list.");
                } else {
                    showError("❌ Database Error!\n\n" + errorMsg + "\n\nPlease check your input and try again.");
                }
            } catch (NumberFormatException e) {
                showError("❌ Invalid Number Format!\n\nPlease check these numeric fields:\n\n" +
                         "• Coverage Amount: Enter numbers only (e.g., 50000.00)\n" +
                         "• Franchise: Enter numbers only (e.g., 500.00)\n" +
                         "• Annual Premium: Enter numbers only (e.g., 1200.00)\n" +
                         "• Monthly Premium: Enter numbers only (e.g., 100.00)\n\n" +
                         "Tips:\n" +
                         "- Don't use currency symbols ($, €)\n" +
                         "- Don't use commas (,)\n" +
                         "- Use period (.) for decimals");
            }
        }
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        errors.append("⚠️ Please fix the following errors:\n\n");
        int errorCount = 0;
        
        if (userComboBox.getValue() == null) {
            errorCount++;
            errors.append("• User: Please select a user\n");
        }
        
        if (typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty()) {
            errorCount++;
            errors.append("• Type: Please select an insurance type\n");
        }
        
        if (compagnieField.getText().trim().isEmpty()) {
            errorCount++;
            errors.append("• Company: Company name is required\n");
        } else if (compagnieField.getText().trim().length() < 2) {
            errorCount++;
            errors.append("• Company: Must be at least 2 characters\n");
        }
        
        if (numeroPoliceField.getText().trim().isEmpty()) {
            errorCount++;
            errors.append("• Policy Number: Policy number is required\n");
        } else if (numeroPoliceField.getText().trim().length() < 3) {
            errorCount++;
            errors.append("• Policy Number: Must be at least 3 characters\n");
        }
        
        if (montantCouvertureField.getText().trim().isEmpty()) {
            errorCount++;
            errors.append("• Coverage Amount: This field is required\n");
        } else {
            try {
                BigDecimal coverage = new BigDecimal(montantCouvertureField.getText().trim());
                if (coverage.compareTo(BigDecimal.ZERO) <= 0) {
                    errorCount++;
                    errors.append("• Coverage Amount: Must be greater than 0\n");
                } else if (coverage.compareTo(new BigDecimal("100000000")) > 0) {
                    errorCount++;
                    errors.append("• Coverage Amount: Maximum value is 100,000,000\n");
                }
            } catch (NumberFormatException e) {
                errorCount++;
                errors.append("• Coverage Amount: Must be a valid number (e.g., 50000.00)\n");
            }
        }
        
        if (franchiseField.getText().trim().isEmpty()) {
            errorCount++;
            errors.append("• Franchise: This field is required\n");
        } else {
            try {
                BigDecimal franchise = new BigDecimal(franchiseField.getText().trim());
                if (franchise.compareTo(BigDecimal.ZERO) < 0) {
                    errorCount++;
                    errors.append("• Franchise: Cannot be negative\n");
                }
            } catch (NumberFormatException e) {
                errorCount++;
                errors.append("• Franchise: Must be a valid number (e.g., 500.00)\n");
            }
        }
        
        if (primeAnnuelleField.getText().trim().isEmpty()) {
            errorCount++;
            errors.append("• Annual Premium: This field is required\n");
        } else {
            try {
                BigDecimal primeAnnuelle = new BigDecimal(primeAnnuelleField.getText().trim());
                if (primeAnnuelle.compareTo(BigDecimal.ZERO) <= 0) {
                    errorCount++;
                    errors.append("• Annual Premium: Must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errorCount++;
                errors.append("• Annual Premium: Must be a valid number (e.g., 1200.00)\n");
            }
        }
        
        if (primeMensuelleField.getText().trim().isEmpty()) {
            errorCount++;
            errors.append("• Monthly Premium: This field is required\n");
        } else {
            try {
                BigDecimal primeMensuelle = new BigDecimal(primeMensuelleField.getText().trim());
                if (primeMensuelle.compareTo(BigDecimal.ZERO) <= 0) {
                    errorCount++;
                    errors.append("• Monthly Premium: Must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errorCount++;
                errors.append("• Monthly Premium: Must be a valid number (e.g., 100.00)\n");
            }
        }
        
        if (dateDebutPicker.getValue() == null) {
            errorCount++;
            errors.append("• Start Date: Please select a start date\n");
        }
        
        if (dateEcheancePicker.getValue() == null) {
            errorCount++;
            errors.append("• Expiry Date: Please select an expiry date\n");
        }
        
        if (dateDebutPicker.getValue() != null && dateEcheancePicker.getValue() != null) {
            if (dateEcheancePicker.getValue().isBefore(dateDebutPicker.getValue())) {
                errorCount++;
                errors.append("• Dates: Expiry date must be after start date\n");
            }
            if (dateEcheancePicker.getValue().equals(dateDebutPicker.getValue())) {
                errorCount++;
                errors.append("• Dates: Expiry date cannot be the same as start date\n");
            }
        }
        
        if (modePaiementComboBox.getValue() == null || modePaiementComboBox.getValue().isEmpty()) {
            errorCount++;
            errors.append("• Payment Mode: Please select a payment mode\n");
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
        Stage stage = (Stage) compagnieField.getScene().getWindow();
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
