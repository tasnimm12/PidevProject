package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Projet;
import tn.esprit.services.ProjetServices;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ModifyProjetDialogController implements Initializable {
    
    @FXML private TextField nomField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> secteurComboBox;
    @FXML private TextField montantField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private Label errorLabel;
    
    private ProjetServices projetServices;
    private Projet projet;
    private boolean success = false;
    
    public ModifyProjetDialogController() {
        projetServices = new ProjetServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        secteurComboBox.getItems().addAll(
            "Technology", "Healthcare", "Finance", "Real Estate", 
            "Energy", "Agriculture", "Education", "Manufacturing"
        );
        
        statutComboBox.getItems().addAll("EN_COURS", "TERMINE", "ANNULE");
    }
    
    public void setProjet(Projet projet) {
        this.projet = projet;
        
        nomField.setText(projet.getNomProjet());
        descriptionField.setText(projet.getDescription());
        secteurComboBox.setValue(projet.getSecteur());
        montantField.setText(String.valueOf(projet.getMontantObjectif()));
        dateDebutPicker.setValue(projet.getDateDebut());
        dateFinPicker.setValue(projet.getDateFin());
        statutComboBox.setValue(projet.getStatutProjet());
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
            projet.setNomProjet(nomField.getText().trim());
            projet.setDescription(descriptionField.getText().trim());
            projet.setSecteur(secteurComboBox.getValue());
            projet.setMontantObjectif(Integer.parseInt(montantField.getText().trim()));
            projet.setDateDebut(dateDebutPicker.getValue());
            projet.setDateFin(dateFinPicker.getValue());
            projet.setStatutProjet(statutComboBox.getValue());
            
            projetServices.modifier(projet);
            
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
        if (nomField.getText().trim().isEmpty()) {
            return "Project name is required.";
        }
        
        if (nomField.getText().trim().length() < 3) {
            return "Project name must be at least 3 characters.";
        }
        
        if (nomField.getText().trim().length() > 100) {
            return "Project name must not exceed 100 characters.";
        }
        
        if (descriptionField.getText().trim().isEmpty()) {
            return "Description is required.";
        }
        
        if (descriptionField.getText().trim().length() > 500) {
            return "Description must not exceed 500 characters.";
        }
        
        if (secteurComboBox.getValue() == null) {
            return "Please select a sector.";
        }
        
        if (montantField.getText().trim().isEmpty()) {
            return "Target amount is required.";
        }
        
        try {
            int montant = Integer.parseInt(montantField.getText().trim());
            if (montant <= 0) {
                return "Target amount must be greater than 0.";
            }
            if (montant > 1000000000) {
                return "Target amount is too large.";
            }
        } catch (NumberFormatException e) {
            return "Invalid amount. Please enter a valid number.";
        }
        
        if (dateDebutPicker.getValue() == null) {
            return "Start date is required.";
        }
        
        if (dateFinPicker.getValue() != null && dateDebutPicker.getValue() != null) {
            if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
                return "End date cannot be before start date.";
            }
        }
        
        if (statutComboBox.getValue() == null) {
            return "Please select a status.";
        }
        
        return null;
    }
    
    private String parseError(SQLException e) {
        String msg = e.getMessage().toLowerCase();
        
        if (msg.contains("duplicate entry")) {
            return "A project with this name already exists.";
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
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
    
    public boolean isSuccess() {
        return success;
    }
}
