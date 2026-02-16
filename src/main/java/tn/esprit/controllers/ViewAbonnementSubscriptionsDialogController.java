package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.Abonnement;
import tn.esprit.entities.User;
import tn.esprit.entities.UserAbonnement;
import tn.esprit.services.UserAbonnementServices;
import tn.esprit.services.UserServices;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ViewAbonnementSubscriptionsDialogController implements Initializable {
    
    @FXML private Label abonnementTitleLabel;
    @FXML private Label abonnementInfoLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label countLabel;
    @FXML private VBox subscriptionsContainer;
    
    private Abonnement abonnement;
    private UserAbonnementServices userAbonnementServices;
    private UserServices userServices;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    private List<UserAbonnement> allSubscriptions;
    
    public ViewAbonnementSubscriptionsDialogController() {
        userAbonnementServices = new UserAbonnementServices();
        userServices = new UserServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusFilter.getItems().addAll("All Status", "actif", "annule", "expire");
        statusFilter.setValue("All Status");
        statusFilter.setOnAction(e -> filterAndDisplay());
        
        searchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplay());
    }
    
    public void setAbonnement(Abonnement abonnement) {
        this.abonnement = abonnement;
        
        abonnementTitleLabel.setText(capitalizeFirst(abonnement.getTypeAbonnement()) + " Plan Subscriptions");
        
        String price = "mensuel".equals(abonnement.getDuree()) ? 
            currencyFormat.format(abonnement.getPrixMensuel()) + "/month" :
            currencyFormat.format(abonnement.getPrixAnnuel()) + "/year";
        abonnementInfoLabel.setText("Price: " + price + " | Duration: " + capitalizeFirst(abonnement.getDuree()));
        
        loadSubscriptions();
    }
    
    private void loadSubscriptions() {
        try {
            allSubscriptions = userAbonnementServices.getByAbonnementId(abonnement.getIdAbonnement());
            filterAndDisplay();
        } catch (SQLException e) {
            showError("Error loading subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filterAndDisplay() {
        if (allSubscriptions == null) return;
        
        String search = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        
        List<UserAbonnement> filtered = allSubscriptions.stream()
            .filter(s -> "All Status".equals(status) || s.getStatut().equals(status))
            .collect(Collectors.toList());
        
        // Filter by user name/email if search provided
        if (!search.isEmpty()) {
            filtered = filtered.stream()
                .filter(s -> {
                    try {
                        User user = userServices.getById(s.getIdUser());
                        return user != null && 
                               (user.getNom().toLowerCase().contains(search) ||
                                user.getPrenom().toLowerCase().contains(search) ||
                                user.getEmail().toLowerCase().contains(search));
                    } catch (SQLException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        }
        
        displaySubscriptions(filtered);
        countLabel.setText(filtered.size() + " subscription" + (filtered.size() != 1 ? "s" : ""));
    }
    
    private void displaySubscriptions(List<UserAbonnement> subscriptions) {
        subscriptionsContainer.getChildren().clear();
        
        if (subscriptions.isEmpty()) {
            Label noData = new Label("No subscriptions found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            subscriptionsContainer.getChildren().add(noData);
            return;
        }
        
        for (UserAbonnement subscription : subscriptions) {
            subscriptionsContainer.getChildren().add(createSubscriptionRow(subscription));
        }
    }
    
    private HBox createSubscriptionRow(UserAbonnement subscription) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 5, 0, 0, 1);");
        
        // Get user info
        User user = null;
        try {
            user = userServices.getById(subscription.getIdUser());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // User info
        VBox userInfo = new VBox(5);
        userInfo.setPrefWidth(250);
        if (user != null) {
            Label nameLabel = new Label(user.getPrenom() + " " + user.getNom());
            nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: 600; -fx-text-fill: #2d3748;");
            Label emailLabel = new Label(user.getEmail());
            emailLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #718096;");
            userInfo.getChildren().addAll(nameLabel, emailLabel);
        }
        
        // Dates
        VBox datesInfo = new VBox(5);
        datesInfo.setPrefWidth(180);
        Label startLabel = new Label("Start: " + subscription.getDateDebut().format(dateFormat));
        startLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #4a5568;");
        Label endLabel = new Label("End: " + subscription.getDateFin().format(dateFormat));
        endLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #4a5568;");
        datesInfo.getChildren().addAll(startLabel, endLabel);
        
        // Status
        Label statusLabel = new Label(subscription.getStatut().toUpperCase());
        statusLabel.setPrefWidth(100);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setStyle("-fx-background-color: " + getStatusColor(subscription.getStatut()) + 
                           "; -fx-text-fill: white; -fx-padding: 6 12; -fx-background-radius: 8; " +
                           "-fx-font-size: 11; -fx-font-weight: 600;");
        
        // Auto-renewal badge
        Label renewalLabel = new Label(subscription.isRenouvellementAuto() ? "AUTO-RENEW" : "MANUAL");
        renewalLabel.setPrefWidth(100);
        renewalLabel.setAlignment(Pos.CENTER);
        renewalLabel.setStyle("-fx-background-color: " + (subscription.isRenouvellementAuto() ? "#e6fffa" : "#f7fafc") + 
                             "; -fx-text-fill: " + (subscription.isRenouvellementAuto() ? "#319795" : "#a0aec0") + 
                             "; -fx-padding: 6 12; -fx-background-radius: 8; -fx-font-size: 10; -fx-font-weight: 600;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Cancel button (only for active subscriptions)
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                         "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 12; -fx-font-weight: 600;");
        cancelBtn.setDisable(!"actif".equals(subscription.getStatut()));
        cancelBtn.setOnAction(e -> handleCancelSubscription(subscription));
        
        row.getChildren().addAll(userInfo, datesInfo, statusLabel, renewalLabel, spacer, cancelBtn);
        
        return row;
    }
    
    private void handleCancelSubscription(UserAbonnement subscription) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Subscription");
        confirm.setHeaderText("Cancel this subscription?");
        confirm.setContentText("The subscription will be marked as cancelled.\nThis action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    subscription.setStatut("annule");
                    userAbonnementServices.modifier(subscription);
                    
                    loadSubscriptions();
                    showSuccess("Subscription cancelled successfully!");
                    
                } catch (SQLException e) {
                    showError("Error cancelling subscription: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    private String getStatusColor(String status) {
        if (status == null) return "#a0aec0";
        switch (status.toLowerCase()) {
            case "actif": return "#48bb78";
            case "annule": return "#f56565";
            case "expire": return "#ed8936";
            default: return "#a0aec0";
        }
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) subscriptionsContainer.getScene().getWindow();
        stage.close();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
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
