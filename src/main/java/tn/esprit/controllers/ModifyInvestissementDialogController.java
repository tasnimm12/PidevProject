package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Investissement;
import tn.esprit.entities.Projet;
import tn.esprit.services.InvestissementServices;
import tn.esprit.services.ProjetServices;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ModifyInvestissementDialogController implements Initializable {
    
    @FXML private ComboBox<Projet> projetComboBox;
    @FXML private TextField montantField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> modePaiementComboBox;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private Label errorLabel;
    
    private InvestissementServices investissementServices;
    private ProjetServices projetServices;
    private Investissement investissement;
    private boolean success = false;
    
    public ModifyInvestissementDialogController() {
        investissementServices = new InvestissementServices();
        projetServices = new ProjetServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modePaiementComboBox.getItems().addAll(
            "Bank Transfer", "Credit Card", "Debit Card", "Cash", "Check", "PayPal"
        );
        
        statutComboBox.getItems().addAll("CONFIRME", "EN_ATTENTE", "ANNULE");
        
        loadProjets();
    }
    
    private void loadProjets() {
        try {
            List<Projet> projets = projetServices.afficher();
            projetComboBox.getItems().addAll(projets);
            
            projetComboBox.setCellFactory(lv -> new ListCell<Projet>() {
                @Override
                protected void updateItem(Projet item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNomProjet());
                }
            });
            
            projetComboBox.setButtonCell(new ListCell<Projet>() {
                @Override
                protected void updateItem(Projet item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNomProjet());
                }
            });
            
        } catch (SQLException e) {
            showError("Error loading projects: " + e.getMessage());
        }
    }
    
    public void setInvestissement(Investissement investissement) {
        this.investissement = investissement;
        
        montantField.setText(String.valueOf(investissement.getMontantInvesti()));
        datePicker.setValue(investissement.getDateInves());
        modePaiementComboBox.setValue(investissement.getModePaiement());
        statutComboBox.setValue(investissement.getStatutInvestissement());
        
        // Select the corresponding project
        for (Projet p : projetComboBox.getItems()) {
            if (p.getIdProjet() == investissement.getIdProjet()) {
                projetComboBox.setValue(p);
                break;
            }
        }
    }
    
    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);
        
        String error = validateFields();
        if (error != null) {
            showError(error);
            return;
        }
        
        try {
            Projet selectedProjet = projetComboBox.getValue();
            investissement.setIdProjet(selectedProjet.getIdProjet());
            investissement.setMontantInvesti(Integer.parseInt(montantField.getText().trim()));
            investissement.setDateInves(datePicker.getValue());
            investissement.setModePaiement(modePaiementComboBox.getValue());
            investissement.setStatutInvestissement(statutComboBox.getValue());
            
            investissementServices.modifier(investissement);
            
            success = true;
            closeDialog();
            
        } catch (SQLException e) {
            String errorMsg = parseError(e);
            showError(errorMsg);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showError("Invalid amount format. Please enter a valid number.");
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private String validateFields() {
        if (projetComboBox.getValue() == null) {
            return "Please select a project.";
        }
        
        if (montantField.getText().trim().isEmpty()) {
            return "Investment amount is required.";
        }
        
        try {
            int montant = Integer.parseInt(montantField.getText().trim());
            if (montant <= 0) {
                return "Investment amount must be greater than 0.";
            }
            if (montant > 1000000000) {
                return "Investment amount is too large.";
            }
        } catch (NumberFormatException e) {
            return "Invalid amount. Please enter a valid number.";
        }
        
        if (datePicker.getValue() == null) {
            return "Investment date is required.";
        }
        
        if (modePaiementComboBox.getValue() == null) {
            return "Please select a payment mode.";
        }
        
        if (statutComboBox.getValue() == null) {
            return "Please select a status.";
        }
        
        return null;
    }
    
    private String parseError(SQLException e) {
        String msg = e.getMessage().toLowerCase();
        
        if (msg.contains("foreign key")) {
            return "Invalid project selected.";
        }
        
        if (msg.contains("data too long")) {
            return "One or more fields exceed the maximum length.";
        }
        
        if (msg.contains("data truncation")) {
            return "Invalid data format. Please check all fields.";
        }
        
        return "Database error: " + e.getMessage();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void closeDialog() {
        Stage stage = (Stage) montantField.getScene().getWindow();
        stage.close();
    }
    
    public boolean isSuccess() {
        return success;
    }
}
