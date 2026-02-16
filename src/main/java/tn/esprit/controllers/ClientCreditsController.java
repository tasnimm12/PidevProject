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

public class ClientCreditsController implements Initializable {
    
    @FXML private Label headerUserNameLabel;
    @FXML private Label headerUserRoleLabel;
    @FXML private ComboBox<String> compteComboBox;
    @FXML private TextField montantField;
    @FXML private ComboBox<String> durationComboBox;
    @FXML private Label subscriptionInfoLabel;
    @FXML private Label interestRateLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Label monthlyPaymentLabel;
    @FXML private VBox creditsContainer;
    
    private User currentUser;
    private CompteBancaireServices compteServices;
    private CreditServices creditServices;
    private UserAbonnementServices abonnementServices;
    private Map<String, Long> compteMap;
    private NumberFormat currencyFormat;
    private UserAbonnement userSubscription;
    
    public ClientCreditsController() {
        compteServices = new CompteBancaireServices();
        creditServices = new CreditServices();
        abonnementServices = new UserAbonnementServices();
        compteMap = new HashMap<>();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        durationComboBox.getItems().addAll("3M - 3 Months", "6M - 6 Months", "12M - 1 Year", "24M - 2 Years", "36M - 3 Years");
        
        montantField.textProperty().addListener((obs, old, newVal) -> updateCalculations());
        durationComboBox.setOnAction(e -> updateCalculations());
    }
    
    public void setUser(User user) {
        this.currentUser = user;
        headerUserNameLabel.setText(user.getPrenom() + " " + user.getNom());
        headerUserRoleLabel.setText(user.getRole());
        loadUserComptes();
        loadUserCredits();
        loadSubscriptionInfo();
    }
    
    private void loadUserComptes() {
        try {
            List<CompteBancaire> comptes = compteServices.getByUserId(currentUser.getIdUser());
            
            for (CompteBancaire compte : comptes) {
                if (compte.isActif()) {
                    String display = compte.getNumeroCompte() + " - " + currencyFormat.format(compte.getSolde());
                    compteComboBox.getItems().add(display);
                    compteMap.put(display, compte.getId());
                }
            }
        } catch (SQLException e) {
            showError("Error loading accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadSubscriptionInfo() {
        try {
            userSubscription = abonnementServices.getActiveByUserId(currentUser.getIdUser());
            
            if (userSubscription != null && userSubscription.getAbonnement() != null) {
                String type = userSubscription.getAbonnement().getTypeAbonnement();
                subscriptionInfoLabel.setText("Your Subscription: " + type.toUpperCase() + " ✓");
                subscriptionInfoLabel.setStyle(subscriptionInfoLabel.getStyle() + "-fx-text-fill: green;");
                
                BigDecimal rate = creditServices.calculateInterestRate(type);
                interestRateLabel.setText("Interest Rate: " + rate + "% (Special rate for " + type + " members)");
            } else {
                subscriptionInfoLabel.setText("Your Subscription: None");
                subscriptionInfoLabel.setStyle(subscriptionInfoLabel.getStyle() + "-fx-text-fill: orange;");
                
                BigDecimal rate = creditServices.calculateInterestRate(null);
                interestRateLabel.setText("Interest Rate: " + rate + "% (Subscribe to get better rates!)");
                
                // Show subscription prompt
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Get Better Rates!");
                info.setHeaderText("Subscribe to reduce your interest rate");
                info.setContentText("Subscribe to an abonnement to get:\n" +
                    "• Basic: 12% rate\n" +
                    "• Premium: 10% rate\n" +
                    "• Gold: 8% rate\n\n" +
                    "Without subscription: 15% rate");
                info.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void updateCalculations() {
        if (montantField.getText().trim().isEmpty() || durationComboBox.getValue() == null) {
            return;
        }
        
        try {
            BigDecimal montant = new BigDecimal(montantField.getText().trim());
            String durationStr = durationComboBox.getValue().substring(0, durationComboBox.getValue().indexOf(" "));
            int durationMonths = Integer.parseInt(durationStr.replace("M", ""));
            
            String subscriptionType = userSubscription != null && userSubscription.getAbonnement() != null ? 
                userSubscription.getAbonnement().getTypeAbonnement() : null;
            
            BigDecimal tauxInteret = creditServices.calculateInterestRate(subscriptionType);
            BigDecimal montantTotal = creditServices.calculateMontantTotal(montant, tauxInteret, durationMonths);
            BigDecimal mensualite = creditServices.calculateMensualite(montantTotal, durationMonths);
            
            totalAmountLabel.setText("Total to Repay: " + currencyFormat.format(montantTotal));
            monthlyPaymentLabel.setText("Monthly Payment: " + currencyFormat.format(mensualite));
            
        } catch (NumberFormatException e) {
            // Invalid number, ignore
        }
    }
    
    private void loadUserCredits() {
        try {
            List<Credit> credits = creditServices.getByUserId(currentUser.getIdUser());
            creditsContainer.getChildren().clear();
            
            if (credits.isEmpty()) {
                Label noData = new Label("No credit requests yet");
                noData.setStyle("-fx-font-size: 16; -fx-text-fill: grey;");
                creditsContainer.getChildren().add(noData);
            } else {
                for (Credit credit : credits) {
                    creditsContainer.getChildren().add(createCreditCard(credit));
                }
            }
        } catch (SQLException e) {
            showError("Error loading credits: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private VBox createCreditCard(Credit credit) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 3);");
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label amountLabel = new Label(currencyFormat.format(credit.getMontantDemande()));
        amountLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: blue;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(credit.getStatutCredit());
        statusLabel.setStyle("-fx-background-color: " + getStatusColor(credit.getStatutCredit()) + 
                           "; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 15; -fx-font-weight: 600;");
        
        header.getChildren().addAll(amountLabel, spacer, statusLabel);
        
        Label durationLabel = new Label("Duration: " + credit.getTypeCredit() + " | Rate: " + credit.getTauxInteret() + "%");
        durationLabel.setStyle("-fx-font-size: 13; -fx-text-fill: grey; -fx-font-weight: 600;");
        
        Label totalLabel = new Label("Total to Pay: " + currencyFormat.format(credit.getMontantTotal()));
        totalLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
        
        Label remainingLabel = new Label("Remaining: " + currencyFormat.format(credit.getMontantRestant()));
        remainingLabel.setStyle("-fx-font-size: 14; -fx-text-fill: red; -fx-font-weight: 600;");
        
        Label monthlyLabel = new Label("Monthly Payment: " + currencyFormat.format(credit.getMensualite()));
        monthlyLabel.setStyle("-fx-font-size: 13; -fx-text-fill: green; -fx-font-weight: 600;");
        
        Label dateLabel = new Label("Requested: " + credit.getDateDemande().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: grey;");
        
        if (credit.getMotifRefus() != null) {
            Label motifLabel = new Label("Reason: " + credit.getMotifRefus());
            motifLabel.setWrapText(true);
            motifLabel.setStyle("-fx-font-size: 12; -fx-text-fill: red; -fx-font-style: italic;");
            card.getChildren().addAll(header, durationLabel, totalLabel, remainingLabel, monthlyLabel, dateLabel, motifLabel);
        } else {
            card.getChildren().addAll(header, durationLabel, totalLabel, remainingLabel, monthlyLabel, dateLabel);
        }
        
        return card;
    }
    
    private String getStatusColor(String status) {
        if (status == null) return "grey";
        switch (status) {
            case "EN_ATTENTE": return "orange";
            case "ACCEPTE": return "green";
            case "REFUSE": return "red";
            case "EN_COURS": return "blue";
            case "TERMINE": return "purple";
            case "EN_RETARD": return "darkred";
            default: return "grey";
        }
    }
    
    @FXML
    private void handleRequestCredit() {
        if (!validateRequest()) return;
        
        // Check subscription requirement
        if (userSubscription == null || userSubscription.getAbonnement() == null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("No Subscription");
            confirm.setHeaderText("Higher Interest Rate");
            confirm.setContentText("Without a subscription, you'll get a 15% interest rate.\n\n" +
                "Do you want to proceed anyway?");
            
            Optional<ButtonType> result = confirm.showAndWait();
            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return;
            }
        }
        
        try {
            long compteId = compteMap.get(compteComboBox.getValue());
            BigDecimal montant = new BigDecimal(montantField.getText().trim());
            String durationStr = durationComboBox.getValue().substring(0, durationComboBox.getValue().indexOf(" "));
            int durationMonths = Integer.parseInt(durationStr.replace("M", ""));
            
            String subscriptionType = userSubscription != null && userSubscription.getAbonnement() != null ? 
                userSubscription.getAbonnement().getTypeAbonnement() : null;
            
            BigDecimal tauxInteret = creditServices.calculateInterestRate(subscriptionType);
            BigDecimal montantTotal = creditServices.calculateMontantTotal(montant, tauxInteret, durationMonths);
            BigDecimal mensualite = creditServices.calculateMensualite(montantTotal, durationMonths);
            
            Credit credit = new Credit(
                currentUser.getIdUser(),
                compteId,
                montant,
                durationStr,
                tauxInteret,
                montantTotal,
                montantTotal,
                java.time.LocalDate.now(),
                null,
                null,
                "EN_ATTENTE",
                mensualite
            );
            
            creditServices.ajouter(credit);
            
            showSuccess("Credit request submitted successfully!\n\nYour request is pending approval.");
            loadUserCredits();
            clearForm();
            
        } catch (SQLException e) {
            showError("Error requesting credit: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateRequest() {
        StringBuilder errors = new StringBuilder();
        
        if (compteComboBox.getValue() == null) {
            errors.append("• Please select a bank account\n");
        }
        
        if (montantField.getText().trim().isEmpty()) {
            errors.append("• Please enter loan amount\n");
        } else {
            try {
                BigDecimal amount = new BigDecimal(montantField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.append("• Amount must be positive\n");
                } else if (amount.compareTo(new BigDecimal("100000")) > 0) {
                    errors.append("• Maximum loan amount is 100,000\n");
                } else if (amount.compareTo(new BigDecimal("500")) < 0) {
                    errors.append("• Minimum loan amount is 500\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Invalid amount format\n");
            }
        }
        
        if (durationComboBox.getValue() == null) {
            errors.append("• Please select repayment duration\n");
        }
        
        if (errors.length() > 0) {
            showError("Please fix the following:\n\n" + errors.toString());
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        compteComboBox.setValue(null);
        montantField.clear();
        durationComboBox.setValue(null);
    }
    
    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientDashboard.fxml"));
            StackPane root = loader.load();
            ClientDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) creditsContainer.getScene().getWindow();
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
            
            Stage stage = (Stage) creditsContainer.getScene().getWindow();
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
            
            Stage stage = (Stage) creditsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showError("Error navigating to expenses: " + e.getMessage());
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
            
            Stage stage = (Stage) creditsContainer.getScene().getWindow();
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
            
            Stage stage = (Stage) creditsContainer.getScene().getWindow();
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
            
            Stage stage = (Stage) creditsContainer.getScene().getWindow();
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
                Stage stage = (Stage) creditsContainer.getScene().getWindow();
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
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
