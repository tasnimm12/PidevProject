package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserServices;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {
    
    @FXML private Label headerUserNameLabel;
    @FXML private Label headerUserRoleLabel;
    @FXML private Label avatarLabel;
    @FXML private Label profileNameLabel;
    @FXML private Label profileEmailLabel;
    
    @FXML private TextField prenomField;
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private DatePicker dateNaissancePicker;
    
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    
    @FXML private Label errorLabel;
    
    private User currentUser;
    private UserServices userServices;
    
    public UserProfileController() {
        userServices = new UserServices();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization if needed
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        
        // Update header
        headerUserNameLabel.setText(user.getPrenom() + " " + user.getNom());
        headerUserRoleLabel.setText(capitalizeFirst(user.getRole()));
        
        // Update avatar
        String initials = user.getPrenom().substring(0, 1).toUpperCase() + 
                         user.getNom().substring(0, 1).toUpperCase();
        avatarLabel.setText(initials);
        
        // Update profile info
        profileNameLabel.setText(user.getPrenom() + " " + user.getNom());
        profileEmailLabel.setText(user.getEmail());
        
        // Populate form fields
        prenomField.setText(user.getPrenom());
        nomField.setText(user.getNom());
        emailField.setText(user.getEmail());
        telephoneField.setText(user.getTelephone());
        dateNaissancePicker.setValue(user.getDateNaissance());
    }
    
    @FXML
    private void handleSaveChanges() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Update user basic info
            currentUser.setPrenom(prenomField.getText().trim());
            currentUser.setNom(nomField.getText().trim());
            currentUser.setEmail(emailField.getText().trim());
            currentUser.setTelephone(telephoneField.getText().trim());
            currentUser.setDateNaissance(dateNaissancePicker.getValue());
            
            // Update password if provided
            if (!currentPasswordField.getText().trim().isEmpty()) {
                if (!currentPasswordField.getText().equals(currentUser.getMotDePasse())) {
                    showError("Current password is incorrect");
                    return;
                }
                
                if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                    showError("New passwords do not match");
                    return;
                }
                
                currentUser.setMotDePasse(newPasswordField.getText().trim());
            }
            
            userServices.modifier(currentUser);
            showSuccess("Profile updated successfully");
            
            // Clear password fields
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            
            // Update display
            setCurrentUser(currentUser);
            
        } catch (SQLException e) {
            showError("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        // Reset fields to original values
        setCurrentUser(currentUser);
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        errorLabel.setVisible(false);
    }
    
    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDashboard.fxml"));
            Scene scene = new Scene(loader.load());
            
            ClientDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) prenomField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Dashboard");
            
        } catch (Exception e) {
            showError("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateForm() {
        errorLabel.setVisible(false);
        
        if (prenomField.getText().trim().isEmpty()) {
            showError("First name is required");
            return false;
        }
        
        if (!prenomField.getText().trim().matches("^[a-zA-Z\\s]+$")) {
            showError("First name should contain only letters");
            return false;
        }
        
        if (nomField.getText().trim().isEmpty()) {
            showError("Last name is required");
            return false;
        }
        
        if (!nomField.getText().trim().matches("^[a-zA-Z\\s]+$")) {
            showError("Last name should contain only letters");
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showError("Email is required");
            return false;
        }
        
        if (!isValidEmail(emailField.getText().trim())) {
            showError("Invalid email format");
            return false;
        }
        
        if (telephoneField.getText().trim().isEmpty()) {
            showError("Phone is required");
            return false;
        }
        
        if (!isValidPhone(telephoneField.getText().trim())) {
            showError("Invalid phone number");
            return false;
        }
        
        if (dateNaissancePicker.getValue() == null) {
            showError("Date of birth is required");
            return false;
        }
        
        if (dateNaissancePicker.getValue().isAfter(LocalDate.now().minusYears(18))) {
            showError("You must be at least 18 years old");
            return false;
        }
        
        // Validate password change if any field is filled
        if (!currentPasswordField.getText().trim().isEmpty() || 
            !newPasswordField.getText().trim().isEmpty() || 
            !confirmPasswordField.getText().trim().isEmpty()) {
            
            if (currentPasswordField.getText().trim().isEmpty()) {
                showError("Current password is required to change password");
                return false;
            }
            
            if (newPasswordField.getText().trim().isEmpty()) {
                showError("New password is required");
                return false;
            }
            
            if (newPasswordField.getText().trim().length() < 6) {
                showError("New password must be at least 6 characters");
                return false;
            }
            
            if (confirmPasswordField.getText().trim().isEmpty()) {
                showError("Please confirm new password");
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    private boolean isValidPhone(String phone) {
        String cleanPhone = phone.replaceAll("[\\s\\-()]", "");
        return cleanPhone.matches("^\\+?[0-9]{8,15}$");
    }
    
    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
    
    @FXML
    private void handleMyAccountsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientComptes.fxml"));
            StackPane root = loader.load();
            ClientComptesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to accounts: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleMyExpensesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDepenses.fxml"));
            StackPane root = loader.load();
            ClientDepensesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to expenses: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleMyCreditsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientCredits.fxml"));
            StackPane root = loader.load();
            ClientCreditsController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to credits: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleMyRemboursementsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientRemboursements.fxml"));
            StackPane root = loader.load();
            ClientRemboursementsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to remboursements: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleMyAbonnementClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientAbonnement.fxml"));
            StackPane root = loader.load();
            ClientAbonnementController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to abonnement: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleMyAssurancesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientAssurances.fxml"));
            StackPane root = loader.load();
            ClientAssurancesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to assurances: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            StackPane root = loader.load();
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error during logout: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
