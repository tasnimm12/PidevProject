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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientAssurancesController implements Initializable {
    
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private MenuButton userMenuButton;
    @FXML private VBox myAssurancesContainer;
    @FXML private Label noActiveLabel;
    @FXML private FlowPane availableAssurancesContainer;
    
    private User currentUser;
    private AssuranceServices assuranceServices;
    private tn.esprit.services.ContratAssuranceServices contratServices;
    private CompteBancaireServices compteServices;
    private RemboursementServices remboursementServices;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    
    public ClientAssurancesController() {
        assuranceServices = new AssuranceServices();
        contratServices = new tn.esprit.services.ContratAssuranceServices();
        compteServices = new CompteBancaireServices();
        remboursementServices = new RemboursementServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userNameLabel.setText(capitalizeFirst(user.getPrenom()) + " " + capitalizeFirst(user.getNom()));
            userRoleLabel.setText(capitalizeFirst(user.getRole()));
            loadAssurances();
        }
    }
    
    private void loadAssurances() {
        try {
            // Load user's active contracts
            List<tn.esprit.entities.ContratAssurance> myContracts = contratServices.afficher().stream()
                .filter(c -> c.getUtilisateurId() == currentUser.getIdUser() && "ACTIF".equals(c.getStatut()))
                .toList();
            
            myAssurancesContainer.getChildren().clear();
            
            if (myContracts.isEmpty()) {
                noActiveLabel.setVisible(true);
            } else {
                noActiveLabel.setVisible(false);
                for (tn.esprit.entities.ContratAssurance contract : myContracts) {
                    try {
                        Assurance a = assuranceServices.getById(contract.getAssuranceId());
                        if (a != null) {
                            myAssurancesContainer.getChildren().add(createMyContractCard(contract, a));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            // Load all available assurances (those not owned by anyone or with ACTIF status)
            List<Assurance> allAssurances = assuranceServices.afficher().stream()
                .filter(a -> "ACTIF".equals(a.getStatut()))
                .toList();
            availableAssurancesContainer.getChildren().clear();
            
            for (Assurance a : allAssurances) {
                availableAssurancesContainer.getChildren().add(createAvailableAssuranceCard(a));
            }
            
        } catch (SQLException e) {
            showError("Error loading assurances: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private VBox createMyContractCard(tn.esprit.entities.ContratAssurance contract, Assurance a) {
        VBox card = new VBox(20);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 2);");
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        StackPane typeIcon = new StackPane();
        typeIcon.setPrefSize(60, 60);
        typeIcon.setStyle("-fx-background-color: " + getTypeColor(a.getTypeAssurance()) + 
                         "; -fx-background-radius: 30;");
        Label iconLabel = new Label(a.getTypeAssurance().substring(0, 1));
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28; -fx-font-weight: bold;");
        typeIcon.getChildren().add(iconLabel);
        
        VBox titleBox = new VBox(5);
        Label typeLabel = new Label(formatTypeLabel(a.getTypeAssurance()));
        typeLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        Label companyLabel = new Label(a.getCompagnie());
        companyLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #a0aec0;");
        titleBox.getChildren().addAll(typeLabel, companyLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(a.getStatut());
        statusLabel.setStyle("-fx-background-color: " + getStatusColor(a.getStatut()) + 
                           "; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 12; " +
                           "-fx-font-size: 12; -fx-font-weight: bold;");
        
        header.getChildren().addAll(typeIcon, titleBox, spacer, statusLabel);
        
        // Details Grid
        GridPane details = new GridPane();
        details.setHgap(30);
        details.setVgap(15);
        
        int row = 0;
        
        details.add(createDetailLabel("Contract Number"), 0, row);
        details.add(createDetailValue(contract.getNumeroContrat()), 1, row++);
        
        if (a.getNumeroPolice() != null) {
            details.add(createDetailLabel("Policy Number"), 0, row);
            details.add(createDetailValue(a.getNumeroPolice()), 1, row++);
        }
        
        details.add(createDetailLabel("Coverage Amount"), 0, row);
        details.add(createDetailValue(currencyFormat.format(a.getMontantCouverture())), 1, row++);
        
        details.add(createDetailLabel("Monthly Premium"), 0, row);
        details.add(createDetailValue(currencyFormat.format(a.getPrimeMensuelle())), 1, row++);
        
        details.add(createDetailLabel("Payment Mode"), 0, row);
        details.add(createDetailValue(formatPaymentMode(a.getModePaiement())), 1, row++);
        
        if (contract.getDateSignature() != null) {
            details.add(createDetailLabel("Signed"), 0, row);
            details.add(createDetailValue(contract.getDateSignature().format(dateFormat)), 1, row++);
        }
        
        if (contract.getDateFinContrat() != null) {
            details.add(createDetailLabel("Expires"), 0, row);
            details.add(createDetailValue(contract.getDateFinContrat().format(dateFormat)), 1, row++);
        }
        
        details.add(createDetailLabel("Auto Renewal"), 0, row);
        details.add(createDetailValue(a.isRenouvellementAuto() ? "Yes" : "No"), 1, row++);
        
        // Cancel button
        Button cancelBtn = new Button("Cancel Insurance");
        cancelBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: 600; " +
                          "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-size: 13;");
        cancelBtn.setOnAction(e -> handleCancelInsurance(contract, a));
        
        HBox buttonBox = new HBox(cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        card.getChildren().addAll(header, new Separator(), details, buttonBox);
        
        return card;
    }
    
    private VBox createAvailableAssuranceCard(Assurance a) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2);");
        
        // Header with icon
        StackPane typeIcon = new StackPane();
        typeIcon.setPrefSize(50, 50);
        typeIcon.setStyle("-fx-background-color: " + getTypeColor(a.getTypeAssurance()) + 
                         "; -fx-background-radius: 25;");
        Label iconLabel = new Label(a.getTypeAssurance().substring(0, 1));
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");
        typeIcon.getChildren().add(iconLabel);
        
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        Label typeLabel = new Label(formatTypeLabel(a.getTypeAssurance()));
        typeLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        Label companyLabel = new Label(a.getCompagnie());
        companyLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #a0aec0;");
        
        header.getChildren().addAll(typeIcon, typeLabel, companyLabel);
        
        // Key details
        VBox details = new VBox(8);
        details.setAlignment(Pos.CENTER_LEFT);
        
        Label coverageLabel = new Label("Coverage: " + currencyFormat.format(a.getMontantCouverture()));
        coverageLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #2d3748; -fx-font-weight: 600;");
        
        Label premiumLabel = new Label("Premium: " + currencyFormat.format(a.getPrimeMensuelle()) + "/mo");
        premiumLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #667eea; -fx-font-weight: 600;");
        
        details.getChildren().addAll(coverageLabel, premiumLabel);
        
        // Subscribe button
        Button subscribeBtn = new Button("Subscribe");
        subscribeBtn.setMaxWidth(Double.MAX_VALUE);
        subscribeBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                            "-fx-font-size: 13; -fx-font-weight: 600; -fx-padding: 10 20; " +
                            "-fx-background-radius: 8; -fx-cursor: hand;");
        subscribeBtn.setOnAction(e -> handleSubscribe(a));
        
        card.getChildren().addAll(header, new Separator(), details, subscribeBtn);
        
        return card;
    }
    
    private void handleSubscribe(Assurance assurance) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SubscribeAssuranceDialog.fxml"));
            VBox dialogRoot = loader.load();
            SubscribeAssuranceDialogController dialogController = loader.getController();
            dialogController.setAssuranceAndUser(assurance, currentUser);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Subscribe to Insurance");
            dialogStage.initOwner(userNameLabel.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot, 500, 600));
            dialogStage.showAndWait();
            
            if (dialogController.isSubscribed()) {
                loadAssurances(); // Reload to show new subscription
                showInfo("Successfully subscribed to " + assurance.getTypeAssurance() + " insurance!");
            }
            
        } catch (Exception e) {
            showError("Error opening subscription dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Label createDetailLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13; -fx-text-fill: #a0aec0; -fx-font-weight: 600;");
        return label;
    }
    
    private Label createDetailValue(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14; -fx-text-fill: #2d3748; -fx-font-weight: 500;");
        return label;
    }
    
    private String formatTypeLabel(String type) {
        if (type == null) return "";
        return type.replace("_", " ");
    }
    
    private String formatPaymentMode(String mode) {
        if (mode == null) return "";
        return capitalizeFirst(mode.toLowerCase());
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    private String getTypeColor(String type) {
        if (type == null) return "#667eea";
        switch (type) {
            case "VIE": return "#9f7aea";
            case "SANTE": return "#48bb78";
            case "AUTO": return "#4299e1";
            case "HABITATION": return "#ed8936";
            case "RESPONSABILITE_CIVILE": return "#f56565";
            case "SCOLAIRE": return "#38b2ac";
            case "VOYAGE": return "#667eea";
            case "PROFESSIONNELLE": return "#805ad5";
            default: return "#667eea";
        }
    }
    
    private String getStatusColor(String status) {
        if (status == null) return "#a0aec0";
        switch (status) {
            case "ACTIF": return "#48bb78";
            case "EXPIRE": return "#f56565";
            case "RESILIE": return "#e53e3e";
            case "SUSPENDU": return "#ed8936";
            default: return "#a0aec0";
        }
    }
    
    @FXML
    private void handleBackToDashboard() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleUserProfileClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserProfile.fxml"));
            Scene scene = new Scene(loader.load());
            
            UserProfileController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Profile");
            
        } catch (Exception e) {
            showError("Error opening profile: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleUserAbonnementClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientAbonnement.fxml"));
            Scene scene = new Scene(loader.load());
            
            ClientAbonnementController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Abonnement");
            
        } catch (Exception e) {
            showError("Error opening abonnement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyAccountsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientComptes.fxml"));
            Scene scene = new Scene(loader.load());
            ClientComptesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Accounts");
        } catch (Exception e) {
            showError("Error navigating to accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyExpensesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDepenses.fxml"));
            Scene scene = new Scene(loader.load());
            ClientDepensesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Expenses");
        } catch (Exception e) {
            showError("Error navigating to expenses: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyCreditsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientCredits.fxml"));
            Scene scene = new Scene(loader.load());
            ClientCreditsController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Credits");
        } catch (Exception e) {
            showError("Error navigating to credits: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyRemboursementsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientRemboursements.fxml"));
            Scene scene = new Scene(loader.load());
            ClientRemboursementsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Remboursements");
        } catch (Exception e) {
            showError("Error navigating to remboursements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Login");
        } catch (Exception e) {
            showError("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
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
            showError("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleCancelInsurance(tn.esprit.entities.ContratAssurance contract, Assurance insurance) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Insurance");
        confirm.setHeaderText("Are you sure you want to cancel this insurance?");
        confirm.setContentText("Your insurance will be cancelled immediately.\n" +
                              "A refund of 40% will be processed to your bank account.\n" +
                              "This action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Cancel contract
                    contract.setStatut("RESILIE");
                    contratServices.modifier(contract);
                    
                    // Process refund (40% of premium)
                    BigDecimal refundAmount = insurance.getPrimeMensuelle()
                        .multiply(new BigDecimal("0.4"));
                    
                    // Get user's first active account
                    List<CompteBancaire> comptes = compteServices.getByUserId(currentUser.getIdUser());
                    CompteBancaire compte = comptes.stream()
                        .filter(CompteBancaire::isActif)
                        .findFirst()
                        .orElse(null);
                    
                    if (compte != null) {
                        // Add refund to account
                        compte.setSolde(compte.getSolde().add(refundAmount));
                        compteServices.modifier(compte);
                        
                        // Create remboursement record
                        Remboursement remb = new Remboursement(
                            null, // No credit_id
                            currentUser.getIdUser(),
                            compte.getId(),
                            "ASSURANCE_REFUND",
                            refundAmount,
                            LocalDate.now(),
                            "COMPLETE",
                            "Refund for cancelled insurance: " + insurance.getTypeAssurance() + " - " + insurance.getCompagnie(),
                            contract.getId()
                        );
                        remboursementServices.ajouter(remb);
                        
                        showInfo("Insurance cancelled successfully!\n" +
                               "Refund of " + currencyFormat.format(refundAmount) + 
                               " has been added to your account.");
                    } else {
                        showInfo("Insurance cancelled successfully!\n" +
                               "Note: No active bank account found for refund processing.");
                    }
                    
                    loadAssurances();
                    
                } catch (SQLException e) {
                    showError("Error cancelling insurance: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
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
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
