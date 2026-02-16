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
import tn.esprit.entities.Abonnement;
import tn.esprit.entities.User;
import tn.esprit.entities.UserAbonnement;
import tn.esprit.services.AbonnementServices;
import tn.esprit.services.UserAbonnementServices;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientAbonnementController implements Initializable {
    
    @FXML private Label headerUserNameLabel;
    @FXML private Label headerUserRoleLabel;
    @FXML private StackPane iconContainer;
    @FXML private Label iconLabel;
    @FXML private Label typeLabel;
    @FXML private Label priceLabel;
    @FXML private Label durationLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private HBox statusBadge;
    @FXML private FlowPane plansContainer;
    
    private User currentUser;
    private AbonnementServices abonnementServices;
    private UserAbonnementServices userAbonnementServices;
    private NumberFormat currencyFormat;
    private UserAbonnement currentSubscription;
    
    public ClientAbonnementController() {
        abonnementServices = new AbonnementServices();
        userAbonnementServices = new UserAbonnementServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        
        headerUserNameLabel.setText(user.getPrenom() + " " + user.getNom());
        headerUserRoleLabel.setText(capitalizeFirst(user.getRole()));
        
        loadUserAbonnement();
        loadAvailablePlans();
    }
    
    private void loadUserAbonnement() {
        try {
            currentSubscription = userAbonnementServices.getActiveByUserId(currentUser.getIdUser());
            
            if (currentSubscription != null && currentSubscription.getAbonnement() != null) {
                displayUserAbonnement(currentSubscription);
            } else {
                displayNoAbonnement();
            }
            
        } catch (SQLException e) {
            showError("Error loading abonnement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void displayUserAbonnement(UserAbonnement userAbon) {
        Abonnement abon = userAbon.getAbonnement();
        
        String color = getTypeColor(abon.getTypeAbonnement());
        iconContainer.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 50;");
        iconLabel.setText(abon.getTypeAbonnement().substring(0, 1).toUpperCase());
        
        typeLabel.setText(abon.getTypeAbonnement().toUpperCase() + " Plan");
        
        String priceText = "mensuel".equals(abon.getDuree()) ? 
            currencyFormat.format(abon.getPrixMensuel()) + "/month" :
            currencyFormat.format(abon.getPrixAnnuel()) + "/year";
        priceLabel.setText(priceText);
        
        durationLabel.setText(capitalizeFirst(abon.getDuree()));
        startDateLabel.setText(userAbon.getDateDebut().toString());
        endDateLabel.setText(userAbon.getDateFin().toString());
        
        if (LocalDate.now().isBefore(userAbon.getDateFin())) {
            statusBadge.setVisible(true);
        }
    }
    
    private void displayNoAbonnement() {
        iconContainer.setStyle("-fx-background-color: #a0aec0; -fx-background-radius: 50;");
        iconLabel.setText("?");
        typeLabel.setText("No Active Abonnement");
        priceLabel.setText(currencyFormat.format(0));
        durationLabel.setText("0 months");
        startDateLabel.setText("N/A");
        endDateLabel.setText("N/A");
        statusBadge.setVisible(false);
    }
    
    private void loadAvailablePlans() {
        try {
            List<Abonnement> allPlans = abonnementServices.afficher();
            
            for (Abonnement plan : allPlans) {
                plansContainer.getChildren().add(createPlanCard(plan));
            }
            
        } catch (SQLException e) {
            showError("Error loading plans: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private VBox createPlanCard(Abonnement plan) {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(35));
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 2); -fx-cursor: hand;");
        
        String color = getTypeColor(plan.getTypeAbonnement());
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 15, 0, 0, 3); -fx-cursor: hand; -fx-scale-x: 1.03; -fx-scale-y: 1.03;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 2); -fx-cursor: hand;"));
        
        // Icon
        StackPane icon = new StackPane();
        icon.setPrefSize(80, 80);
        icon.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 40;");
        Label iconText = new Label(plan.getTypeAbonnement().substring(0, 1).toUpperCase());
        iconText.setStyle("-fx-text-fill: white; -fx-font-size: 36; -fx-font-weight: bold;");
        icon.getChildren().add(iconText);
        
        // Type
        Label type = new Label(plan.getTypeAbonnement().toUpperCase());
        type.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        
        // Price
        String priceText = "mensuel".equals(plan.getDuree()) ? 
            currencyFormat.format(plan.getPrixMensuel()) + "/mo" :
            currencyFormat.format(plan.getPrixAnnuel()) + "/yr";
        Label price = new Label(priceText);
        price.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        // Duration badge
        Label durationBadge = new Label(capitalizeFirst(plan.getDuree()));
        durationBadge.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                              "-fx-padding: 5 15; -fx-background-radius: 12; -fx-font-size: 12; -fx-font-weight: 600;");
        
        // Subscribe button
        Button subscribeBtn = new Button("Subscribe");
        subscribeBtn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                             "-fx-background-radius: 20; -fx-padding: 12 40; -fx-font-size: 14; " +
                             "-fx-font-weight: 600; -fx-cursor: hand;");
        subscribeBtn.setOnAction(e -> handleSubscribe(plan));
        
        card.getChildren().addAll(icon, type, price, durationBadge, subscribeBtn);
        
        return card;
    }
    
    private String getTypeColor(String type) {
        if (type == null) return "#667eea";
        switch (type.toLowerCase()) {
            case "bronze": return "#cd7f32";
            case "silver": return "#c0c0c0";
            case "gold": return "#ffd700";
            case "platinum": return "#e5e4e2";
            default: return "#667eea";
        }
    }
    
    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDashboard.fxml"));
            Scene scene = new Scene(loader.load());
            
            ClientDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Dashboard");
            
        } catch (Exception e) {
            showError("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Login");
            
        } catch (Exception e) {
            showError("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleSubscribe(Abonnement plan) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Subscribe to Plan");
        confirmAlert.setHeaderText("Subscribe to " + plan.getTypeAbonnement().toUpperCase() + " Plan?");
        
        String priceText = "mensuel".equals(plan.getDuree()) ? 
            currencyFormat.format(plan.getPrixMensuel()) + "/month" :
            currencyFormat.format(plan.getPrixAnnuel()) + "/year";
        confirmAlert.setContentText("Price: " + priceText + "\nBilling: " + capitalizeFirst(plan.getDuree()));
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Cancel current subscription if exists
                    if (currentSubscription != null) {
                        currentSubscription.setStatut("annule");
                        userAbonnementServices.modifier(currentSubscription);
                    }
                    
                    // Create new subscription
                    LocalDate startDate = LocalDate.now();
                    LocalDate endDate = "mensuel".equals(plan.getDuree()) ? 
                        startDate.plusMonths(1) : startDate.plusYears(1);
                    
                    UserAbonnement newSubscription = new UserAbonnement(
                        currentUser.getIdUser(),
                        plan.getIdAbonnement(),
                        startDate,
                        endDate,
                        "actif",
                        false
                    );
                    
                    userAbonnementServices.ajouter(newSubscription);
                    showSuccess("Successfully subscribed to " + plan.getTypeAbonnement() + " plan!");
                    
                    // Reload
                    loadUserAbonnement();
                    
                } catch (SQLException e) {
                    showError("Error subscribing: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
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
