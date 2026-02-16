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
import tn.esprit.entities.*;
import tn.esprit.services.*;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ClientRemboursementsController implements Initializable {
    
    @FXML private Label headerUserNameLabel;
    @FXML private Label headerUserRoleLabel;
    @FXML private Label totalRefundsLabel;
    @FXML private Label totalPaymentsLabel;
    @FXML private Label totalRemboursementsLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private VBox remboursementsContainer;
    @FXML private Label noDataLabel;
    
    private User currentUser;
    private RemboursementServices remboursementServices;
    private List<Remboursement> allRemboursements;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    
    public ClientRemboursementsController() {
        remboursementServices = new RemboursementServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeFilter.getItems().addAll("All Types", "CREDIT_PAYMENT", "ABONNEMENT_REFUND", "ASSURANCE_REFUND");
        typeFilter.setValue("All Types");
        
        statusFilter.getItems().addAll("All Status", "EN_ATTENTE", "COMPLETE", "ECHOUE");
        statusFilter.setValue("All Status");
        
        sortComboBox.getItems().addAll("Date (Newest)", "Date (Oldest)", "Amount (High)", "Amount (Low)");
        sortComboBox.setValue("Date (Newest)");
        
        // Add listeners
        searchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayRemboursements());
        typeFilter.setOnAction(e -> filterAndDisplayRemboursements());
        statusFilter.setOnAction(e -> filterAndDisplayRemboursements());
        sortComboBox.setOnAction(e -> filterAndDisplayRemboursements());
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        headerUserNameLabel.setText(user.getPrenom() + " " + user.getNom());
        headerUserRoleLabel.setText(user.getRole());
        loadRemboursements();
    }
    
    private void loadRemboursements() {
        try {
            allRemboursements = remboursementServices.getByUserId(currentUser.getIdUser());
            updateSummary();
            filterAndDisplayRemboursements();
        } catch (SQLException e) {
            showError("Error loading remboursements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateSummary() {
        BigDecimal totalRefunds = BigDecimal.ZERO;
        BigDecimal totalPayments = BigDecimal.ZERO;
        
        for (Remboursement remb : allRemboursements) {
            if ("COMPLETE".equals(remb.getStatut())) {
                if (remb.getTypeRemboursement().contains("REFUND")) {
                    totalRefunds = totalRefunds.add(remb.getMontant());
                } else if ("CREDIT_PAYMENT".equals(remb.getTypeRemboursement())) {
                    totalPayments = totalPayments.add(remb.getMontant());
                }
            }
        }
        
        totalRefundsLabel.setText(currencyFormat.format(totalRefunds));
        totalPaymentsLabel.setText(currencyFormat.format(totalPayments));
        totalRemboursementsLabel.setText(currencyFormat.format(totalRefunds.add(totalPayments)));
    }
    
    private void filterAndDisplayRemboursements() {
        if (allRemboursements == null) return;
        
        String search = searchField.getText().toLowerCase();
        String type = typeFilter.getValue();
        String status = statusFilter.getValue();
        String sort = sortComboBox.getValue();
        
        List<Remboursement> filtered = allRemboursements.stream()
            .filter(r -> "All Types".equals(type) || r.getTypeRemboursement().equals(type))
            .filter(r -> "All Status".equals(status) || r.getStatut().equals(status))
            .filter(r -> search.isEmpty() || 
                    r.getDescription().toLowerCase().contains(search) ||
                    r.getTypeRemboursement().toLowerCase().contains(search))
            .collect(Collectors.toList());
        
        // Sort
        if ("Date (Newest)".equals(sort)) {
            filtered.sort((a, b) -> b.getDateRemboursement().compareTo(a.getDateRemboursement()));
        } else if ("Date (Oldest)".equals(sort)) {
            filtered.sort((a, b) -> a.getDateRemboursement().compareTo(b.getDateRemboursement()));
        } else if ("Amount (High)".equals(sort)) {
            filtered.sort((a, b) -> b.getMontant().compareTo(a.getMontant()));
        } else if ("Amount (Low)".equals(sort)) {
            filtered.sort((a, b) -> a.getMontant().compareTo(b.getMontant()));
        }
        
        displayRemboursements(filtered);
    }
    
    private void displayRemboursements(List<Remboursement> remboursements) {
        remboursementsContainer.getChildren().clear();
        
        if (remboursements.isEmpty()) {
            noDataLabel.setVisible(true);
        } else {
            noDataLabel.setVisible(false);
            for (Remboursement remb : remboursements) {
                remboursementsContainer.getChildren().add(createRemboursementCard(remb));
            }
        }
    }
    
    private VBox createRemboursementCard(Remboursement remb) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 3);");
        
        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Icon based on type
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(50, 50);
        String iconColor = remb.getTypeRemboursement().contains("REFUND") ? "#48bb78" : "#4299e1";
        iconContainer.setStyle("-fx-background-color: " + iconColor + "; -fx-background-radius: 25;");
        Label icon = new Label(remb.getTypeRemboursement().contains("REFUND") ? "↓" : "↑");
        icon.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");
        iconContainer.getChildren().add(icon);
        
        VBox titleBox = new VBox(5);
        Label typeLabel = new Label(formatType(remb.getTypeRemboursement()));
        typeLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: black;");
        Label dateLabel = new Label(remb.getDateRemboursement().format(dateFormat));
        dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: grey;");
        titleBox.getChildren().addAll(typeLabel, dateLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        VBox amountBox = new VBox(5);
        amountBox.setAlignment(Pos.CENTER_RIGHT);
        Label amountLabel = new Label(currencyFormat.format(remb.getMontant()));
        amountLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + iconColor + ";");
        Label statusLabel = new Label(remb.getStatut());
        statusLabel.setStyle("-fx-background-color: " + getStatusColor(remb.getStatut()) + 
                           "; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 10; " +
                           "-fx-font-size: 11; -fx-font-weight: 600;");
        amountBox.getChildren().addAll(amountLabel, statusLabel);
        
        header.getChildren().addAll(iconContainer, titleBox, spacer, amountBox);
        
        // Description
        if (remb.getDescription() != null && !remb.getDescription().isEmpty()) {
            Label descLabel = new Label(remb.getDescription());
            descLabel.setWrapText(true);
            descLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #4a5568; -fx-padding: 10 0 0 0;");
            card.getChildren().addAll(header, new Separator(), descLabel);
        } else {
            card.getChildren().add(header);
        }
        
        return card;
    }
    
    private String formatType(String type) {
        if (type == null) return "";
        switch (type) {
            case "CREDIT_PAYMENT":
                return "Credit Payment";
            case "ABONNEMENT_REFUND":
                return "Subscription Refund";
            case "ASSURANCE_REFUND":
                return "Insurance Refund";
            default:
                return type.replace("_", " ");
        }
    }
    
    private String getStatusColor(String status) {
        if (status == null) return "grey";
        switch (status) {
            case "EN_ATTENTE":
                return "orange";
            case "COMPLETE":
                return "green";
            case "ECHOUE":
                return "red";
            default:
                return "grey";
        }
    }
    
    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDashboard.fxml"));
            StackPane root = loader.load();
            ClientDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) remboursementsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyAccountsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientComptes.fxml"));
            StackPane root = loader.load();
            ClientComptesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) remboursementsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to accounts: " + e.getMessage());
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
            
            Stage stage = (Stage) remboursementsContainer.getScene().getWindow();
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
            
            Stage stage = (Stage) remboursementsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to credits: " + e.getMessage());
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
            
            Stage stage = (Stage) remboursementsContainer.getScene().getWindow();
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
            
            Stage stage = (Stage) remboursementsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to assurances: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Logout");
            confirm.setHeaderText("Are you sure you want to logout?");
            confirm.setContentText("You will be returned to the login screen.");
            
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                StackPane root = loader.load();
                Stage stage = (Stage) remboursementsContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
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
}
