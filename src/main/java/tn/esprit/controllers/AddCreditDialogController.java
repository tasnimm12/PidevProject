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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class AddCreditDialogController implements Initializable {
    
    @FXML private ComboBox<String> userComboBox;
    @FXML private ComboBox<String> compteComboBox;
    @FXML private TextField montantField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Label interestInfoLabel;
    @FXML private Label totalLabel;
    @FXML private Label mensualiteLabel;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusComboBox;
    
    private CreditServices creditServices;
    private UserServices userServices;
    private CompteBancaireServices compteServices;
    private UserAbonnementServices abonnementServices;
    private Map<String, Integer> userMap;
    private Map<String, Long> compteMap;
    private NumberFormat currencyFormat;
    private Runnable onSaveCallback;
    
    public AddCreditDialogController() {
        creditServices = new CreditServices();
        userServices = new UserServices();
        compteServices = new CompteBancaireServices();
        abonnementServices = new UserAbonnementServices();
        userMap = new HashMap<>();
        compteMap = new HashMap<>();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.getItems().addAll("3M", "6M", "12M", "24M", "36M");
        statusComboBox.getItems().addAll("EN_ATTENTE", "ACCEPTE", "REFUSE");
        statusComboBox.setValue("EN_ATTENTE");
        datePicker.setValue(LocalDate.now());
        
        loadUsers();
        
        // Update calculations when values change
        userComboBox.setOnAction(e -> {
            loadUserComptes();
            updateCalculations();
        });
        montantField.textProperty().addListener((obs, old, newVal) -> updateCalculations());
        typeComboBox.setOnAction(e -> updateCalculations());
    }
    
    private void loadUsers() {
        try {
            List<User> users = userServices.afficher();
            for (User user : users) {
                String display = user.getPrenom() + " " + user.getNom() + " (" + user.getEmail() + ")";
                userComboBox.getItems().add(display);
                userMap.put(display, user.getIdUser());
            }
        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadUserComptes() {
        compteComboBox.getItems().clear();
        compteMap.clear();
        
        if (userComboBox.getValue() == null) return;
        
        try {
            int userId = userMap.get(userComboBox.getValue());
            List<CompteBancaire> comptes = compteServices.getByUserId(userId);
            
            for (CompteBancaire compte : comptes) {
                if (compte.isActif()) {
                    String display = compte.getNumeroCompte() + " - " + compte.getTitulaire() + 
                                   " (" + currencyFormat.format(compte.getSolde()) + ")";
                    compteComboBox.getItems().add(display);
                    compteMap.put(display, compte.getId());
                }
            }
        } catch (SQLException e) {
            showError("Error loading accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateCalculations() {
        if (userComboBox.getValue() == null || montantField.getText().trim().isEmpty() || 
            typeComboBox.getValue() == null) {
            return;
        }
        
        try {
            int userId = userMap.get(userComboBox.getValue());
            BigDecimal montant = new BigDecimal(montantField.getText().trim());
            String type = typeComboBox.getValue();
            int durationMonths = Integer.parseInt(type.replace("M", ""));
            
            // Get user's subscription to calculate interest rate
            UserAbonnement userAbon = abonnementServices.getActiveByUserId(userId);
            String subscriptionType = null;
            if (userAbon != null && userAbon.getAbonnement() != null) {
                subscriptionType = userAbon.getAbonnement().getTypeAbonnement();
            }
            
            BigDecimal tauxInteret = creditServices.calculateInterestRate(subscriptionType);
            BigDecimal montantTotal = creditServices.calculateMontantTotal(montant, tauxInteret, durationMonths);
            BigDecimal mensualite = creditServices.calculateMensualite(montantTotal, durationMonths);
            
            // Update labels
            String subscriptionInfo = subscriptionType != null ? 
                subscriptionType.toUpperCase() + " subscription" : "No subscription";
            interestInfoLabel.setText("Interest Rate: " + tauxInteret + "% (" + subscriptionInfo + ")");
            totalLabel.setText(currencyFormat.format(montantTotal));
            mensualiteLabel.setText(currencyFormat.format(mensualite));
            
        } catch (NumberFormatException e) {
            // Invalid number, ignore
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    @FXML
    private void handleAdd() {
        if (!validateFields()) return;
        
        try {
            int userId = userMap.get(userComboBox.getValue());
            long compteId = compteMap.get(compteComboBox.getValue());
            BigDecimal montant = new BigDecimal(montantField.getText().trim());
            String type = typeComboBox.getValue();
            int durationMonths = Integer.parseInt(type.replace("M", ""));
            
            // Calculate interest based on subscription
            UserAbonnement userAbon = abonnementServices.getActiveByUserId(userId);
            String subscriptionType = userAbon != null && userAbon.getAbonnement() != null ? 
                userAbon.getAbonnement().getTypeAbonnement() : null;
            
            BigDecimal tauxInteret = creditServices.calculateInterestRate(subscriptionType);
            BigDecimal montantTotal = creditServices.calculateMontantTotal(montant, tauxInteret, durationMonths);
            BigDecimal mensualite = creditServices.calculateMensualite(montantTotal, durationMonths);
            
            LocalDate dateDebut = "ACCEPTE".equals(statusComboBox.getValue()) ? datePicker.getValue() : null;
            LocalDate dateFin = dateDebut != null ? dateDebut.plusMonths(durationMonths) : null;
            
            Credit credit = new Credit(
                userId,
                compteId,
                montant,
                type,
                tauxInteret,
                montantTotal,
                montantTotal, // montant_restant initially equals montant_total
                datePicker.getValue(),
                dateDebut,
                dateFin,
                statusComboBox.getValue(),
                mensualite
            );
            
            creditServices.ajouter(credit);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            closeDialog();
            
        } catch (SQLException e) {
            showError("Error adding credit: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showError("Invalid amount format");
        }
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        if (userComboBox.getValue() == null) {
            errors.append("• User is required\n");
        }
        
        if (compteComboBox.getValue() == null) {
            errors.append("• Bank account is required\n");
        }
        
        if (montantField.getText().trim().isEmpty()) {
            errors.append("• Amount is required\n");
        } else {
            try {
                BigDecimal amount = new BigDecimal(montantField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.append("• Amount must be positive\n");
                } else if (amount.compareTo(new BigDecimal("100000")) > 0) {
                    errors.append("• Amount cannot exceed 100,000\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Invalid amount format\n");
            }
        }
        
        if (typeComboBox.getValue() == null) {
            errors.append("• Credit duration is required\n");
        }
        
        if (datePicker.getValue() == null) {
            errors.append("• Request date is required\n");
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
