package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.CompteBancaire;
import tn.esprit.entities.Depense;
import tn.esprit.services.CompteBancaireServices;
import tn.esprit.services.DepenseServices;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AddDepenseDialogController implements Initializable {
    
    @FXML private ComboBox<String> compteComboBox;
    @FXML private TextArea descriptionField;
    @FXML private TextField montantField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> modeComboBox;
    
    private DepenseServices depenseServices;
    private CompteBancaireServices compteServices;
    private Map<String, Integer> compteMap;
    private Runnable onSaveCallback;
    
    public AddDepenseDialogController() {
        depenseServices = new DepenseServices();
        compteServices = new CompteBancaireServices();
        compteMap = new HashMap<>();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Category options
        categoryComboBox.getItems().addAll(
            "Subscription", "Insurance", "Investment", "Transfer", "Utility", 
            "Food", "Transport", "Shopping", "Healthcare", "Entertainment", "Other"
        );
        
        // Payment mode options
        modeComboBox.getItems().addAll(
            "Auto Debit", "Manual", "Card", "Cash", "Transfer", "Check", "Mobile Payment"
        );
        
        // Default date to today
        datePicker.setValue(LocalDate.now());
        
        loadComptes();
    }
    
    private void loadComptes() {
        try {
            List<CompteBancaire> comptes = compteServices.afficher();
            for (CompteBancaire compte : comptes) {
                String display = compte.getNumeroCompte() + " - " + compte.getTitulaire();
                compteComboBox.getItems().add(display);
                compteMap.put(display, (int) compte.getId());
            }
        } catch (SQLException e) {
            showError("Error loading bank accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    @FXML
    private void handleAdd() {
        if (!validateFields()) {
            return;
        }
        
        try {
            Integer compteId = compteMap.get(compteComboBox.getValue());
            
            Depense depense = new Depense(
                descriptionField.getText().trim(),
                new BigDecimal(montantField.getText().trim()),
                datePicker.getValue(),
                categoryComboBox.getValue(),
                modeComboBox.getValue(),
                compteId
            );
            
            depenseServices.ajouter(depense);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            closeDialog();
            
        } catch (SQLException e) {
            String errorMsg = parseErrorMessage(e);
            showError(errorMsg);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showError("Invalid amount format. Please enter a valid number.");
        }
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        if (compteComboBox.getValue() == null) {
            errors.append("• Bank account is required\n");
        }
        
        if (descriptionField.getText().trim().isEmpty()) {
            errors.append("• Description is required\n");
        } else if (descriptionField.getText().trim().length() < 5) {
            errors.append("• Description must be at least 5 characters\n");
        }
        
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
            errors.append("• Expense date is required\n");
        } else if (datePicker.getValue().isAfter(LocalDate.now())) {
            errors.append("• Expense date cannot be in the future\n");
        }
        
        if (categoryComboBox.getValue() == null) {
            errors.append("• Category is required\n");
        }
        
        if (modeComboBox.getValue() == null) {
            errors.append("• Payment mode is required\n");
        }
        
        if (errors.length() > 0) {
            showError("Please fix the following errors:\n\n" + errors.toString());
            return false;
        }
        
        return true;
    }
    
    private String parseErrorMessage(SQLException e) {
        String message = e.getMessage().toLowerCase();
        
        if (message.contains("foreign key") && message.contains("compte")) {
            return "❌ Invalid bank account!\n\nThe selected account does not exist.";
        } else if (message.contains("data too long")) {
            return "❌ Data too long!\n\nPlease shorten your input.";
        }
        
        return "Error adding expense: " + e.getMessage();
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) descriptionField.getScene().getWindow();
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
