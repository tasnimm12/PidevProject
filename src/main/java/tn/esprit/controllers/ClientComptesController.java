package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.CompteBancaire;
import tn.esprit.entities.User;
import tn.esprit.services.CompteBancaireServices;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientComptesController implements Initializable {
    
    @FXML private Label userNameLabel;
    @FXML private FlowPane comptesContainer;
    
    private User currentUser;
    private CompteBancaireServices compteServices;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    
    public ClientComptesController() {
        compteServices = new CompteBancaireServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userNameLabel.setText(user.getPrenom() + " " + user.getNom());
            loadMyAccounts();
        }
    }
    
    private void loadMyAccounts() {
        try {
            List<CompteBancaire> comptes = compteServices.getByUserId(currentUser.getIdUser());
            displayAccounts(comptes);
        } catch (SQLException e) {
            showError("Error loading accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void displayAccounts(List<CompteBancaire> comptes) {
        comptesContainer.getChildren().clear();
        
        if (comptes.isEmpty()) {
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(80));
            
            Label icon = new Label("🏦");
            icon.setStyle("-fx-font-size: 60;");
            
            Label message = new Label("You don't have any bank accounts yet.");
            message.setStyle("-fx-font-size: 18; -fx-text-fill: #a0aec0;");
            
            Label hint = new Label("Click 'Add New Account' to create your first account");
            hint.setStyle("-fx-font-size: 14; -fx-text-fill: #cbd5e0;");
            
            emptyState.getChildren().addAll(icon, message, hint);
            comptesContainer.getChildren().add(emptyState);
            return;
        }
        
        for (CompteBancaire compte : comptes) {
            comptesContainer.getChildren().add(createAccountCard(compte));
        }
    }
    
    private VBox createAccountCard(CompteBancaire compte) {
        VBox card = new VBox(20);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(380);
        card.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #667eea 0%, #764ba2 100%); " +
                     "-fx-background-radius: 20; -fx-padding: 35; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 15, 0, 0, 5);");
        
        // Card chip (visual decoration)
        HBox chipRow = new HBox();
        chipRow.setAlignment(Pos.TOP_LEFT);
        StackPane chip = new StackPane();
        chip.setPrefSize(50, 40);
        chip.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-background-radius: 8;");
        chipRow.getChildren().add(chip);
        
        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        
        // Account number
        Label numeroLabel = new Label(formatAccountNumber(compte.getNumeroCompte()));
        numeroLabel.setStyle("-fx-font-size: 20; -fx-font-family: 'Courier New'; -fx-text-fill: white; " +
                           "-fx-font-weight: 600; -fx-letter-spacing: 2px;");
        
        // Account holder and type
        HBox detailsRow = new HBox();
        detailsRow.setSpacing(40);
        detailsRow.setAlignment(Pos.CENTER_LEFT);
        
        VBox holderBox = new VBox(3);
        Label holderTitle = new Label("ACCOUNT HOLDER");
        holderTitle.setStyle("-fx-font-size: 9; -fx-text-fill: rgba(255,255,255,0.7); -fx-font-weight: 600;");
        Label holderName = new Label(compte.getTitulaire());
        holderName.setStyle("-fx-font-size: 14; -fx-text-fill: white; -fx-font-weight: 600;");
        holderBox.getChildren().addAll(holderTitle, holderName);
        
        VBox typeBox = new VBox(3);
        Label typeTitle = new Label("TYPE");
        typeTitle.setStyle("-fx-font-size: 9; -fx-text-fill: rgba(255,255,255,0.7); -fx-font-weight: 600;");
        Label typeName = new Label(compte.getTypeCompte());
        typeName.setStyle("-fx-font-size: 14; -fx-text-fill: white; -fx-font-weight: 600;");
        typeBox.getChildren().addAll(typeTitle, typeName);
        
        detailsRow.getChildren().addAll(holderBox, typeBox);
        
        // Balance display
        Label balanceLabel = new Label("Current Balance");
        balanceLabel.setStyle("-fx-font-size: 11; -fx-text-fill: rgba(255,255,255,0.8);");
        
        Label soldeLabel = new Label(currencyFormat.format(compte.getSolde()) + " " + compte.getDevise());
        soldeLabel.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Status badge
        Label statusLabel = new Label(compte.isActif() ? "● ACTIVE" : "● INACTIVE");
        statusLabel.setStyle("-fx-font-size: 10; -fx-text-fill: " + 
                           (compte.isActif() ? "#48bb78" : "#f56565") + 
                           "; -fx-font-weight: 600;");
        
        card.getChildren().addAll(chipRow, spacer1, numeroLabel, detailsRow, balanceLabel, soldeLabel, statusLabel);
        
        return card;
    }
    
    private String formatAccountNumber(String numero) {
        if (numero == null || numero.length() < 12) return numero;
        // Format like: **** **** **** 1234
        int visibleDigits = 4;
        int len = numero.length();
        StringBuilder formatted = new StringBuilder();
        
        for (int i = 0; i < len - visibleDigits; i++) {
            if (i > 0 && i % 4 == 0) formatted.append(" ");
            formatted.append("*");
        }
        formatted.append(" ");
        formatted.append(numero.substring(len - visibleDigits));
        
        return formatted.toString();
    }
    
    @FXML
    private void handleAddAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientAddCompteDialog.fxml"));
            VBox dialogContent = loader.load();
            
            ClientAddCompteDialogController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Bank Account");
            dialogStage.setScene(new Scene(dialogContent));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            if (controller.isSuccess()) {
                loadMyAccounts();
                showSuccess("Bank account created successfully!");
            }
            
        } catch (Exception e) {
            showError("Error opening add account dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBackToDashboard() {
        navigateToDashboard();
    }
    
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDashboard.fxml"));
            Scene scene = new Scene(loader.load());
            
            ClientDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Dashboard");
        } catch (Exception e) {
            showError("Error navigating: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyExpensesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDepenses.fxml"));
            StackPane root = loader.load();
            ClientDepensesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to expenses: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyCreditsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientCredits.fxml"));
            StackPane root = loader.load();
            ClientCreditsController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to credits: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyRemboursementsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientRemboursements.fxml"));
            StackPane root = loader.load();
            ClientRemboursementsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to remboursements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyAbonnementClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientAbonnement.fxml"));
            StackPane root = loader.load();
            ClientAbonnementController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to abonnement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyAssurancesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientAssurances.fxml"));
            StackPane root = loader.load();
            ClientAssurancesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to assurances: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            StackPane root = loader.load();
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
