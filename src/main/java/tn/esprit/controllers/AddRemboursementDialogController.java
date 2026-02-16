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
import java.time.LocalDate;
import java.util.*;

public class AddRemboursementDialogController implements Initializable {
    
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> creditComboBox;
    @FXML private ComboBox<String> userComboBox;
    @FXML private ComboBox<String> compteComboBox;
    @FXML private TextField montantField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;
    
    private RemboursementServices remboursementServices;
    private CreditServices creditServices;
    private UserServices userServices;
    private CompteBancaireServices compteServices;
    private Map<String, Integer> userMap, creditMap;
    private Map<String, Long> compteMap;
    private Runnable onSaveCallback;
    
    public AddRemboursementDialogController() {
        remboursementServices = new RemboursementServices();
        creditServices = new CreditServices();
        userServices = new UserServices();
        compteServices = new CompteBancaireServices();
        userMap = new HashMap<>();
        creditMap = new HashMap<>();
        compteMap = new HashMap<>();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.getItems().addAll("CREDIT_PAYMENT", "ABONNEMENT_REFUND", "ASSURANCE_REFUND");
        statusComboBox.getItems().addAll("EN_ATTENTE", "COMPLETE", "ECHOUE");
        statusComboBox.setValue("COMPLETE");
        datePicker.setValue(LocalDate.now());
        
        typeComboBox.setOnAction(e -> {
            creditComboBox.setDisable(!"CREDIT_PAYMENT".equals(typeComboBox.getValue()));
        });
        
        userComboBox.setOnAction(e -> loadUserComptes());
        
        loadUsers();
        loadCredits();
    }
    
    private void loadUsers() {
        try {
            List<User> users = userServices.afficher();
            for (User user : users) {
                String display = user.getPrenom() + " " + user.getNom();
                userComboBox.getItems().add(display);
                userMap.put(display, user.getIdUser());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadCredits() {
        try {
            List<Credit> credits = creditServices.afficher();
            for (Credit credit : credits) {
                if ("EN_COURS".equals(credit.getStatutCredit())) {
                    String display = "Credit #" + credit.getIdCredit() + " - " + credit.getMontantDemande();
                    creditComboBox.getItems().add(display);
                    creditMap.put(display, credit.getIdCredit());
                }
            }
        } catch (SQLException e) {
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
                String display = compte.getNumeroCompte() + " - " + compte.getTitulaire();
                compteComboBox.getItems().add(display);
                compteMap.put(display, compte.getId());
            }
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
            Integer creditId = creditComboBox.getValue() != null ? 
                creditMap.get(creditComboBox.getValue()) : null;
            int userId = userMap.get(userComboBox.getValue());
            long compteId = compteMap.get(compteComboBox.getValue());
            
            Remboursement remb = new Remboursement(
                creditId,
                userId,
                compteId,
                typeComboBox.getValue(),
                new BigDecimal(montantField.getText().trim()),
                datePicker.getValue(),
                statusComboBox.getValue(),
                descriptionField.getText().trim()
            );
            
            remboursementServices.ajouter(remb);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            closeDialog();
            
        } catch (SQLException e) {
            showError("Error adding remboursement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        if (typeComboBox.getValue() == null) {
            errors.append("• Type is required\n");
        }
        
        if ("CREDIT_PAYMENT".equals(typeComboBox.getValue()) && creditComboBox.getValue() == null) {
            errors.append("• Credit is required for payment type\n");
        }
        
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
