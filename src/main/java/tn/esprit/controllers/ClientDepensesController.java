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
import tn.esprit.entities.Depense;
import tn.esprit.entities.User;
import tn.esprit.services.CompteBancaireServices;
import tn.esprit.services.DepenseServices;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientDepensesController implements Initializable {
    
    @FXML private Label headerUserNameLabel;
    @FXML private Label headerUserRoleLabel;
    @FXML private Label totalExpensesLabel;
    @FXML private Label monthExpensesLabel;
    @FXML private Label countLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> modeFilter;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private VBox expensesContainer;
    
    private User currentUser;
    private DepenseServices depenseServices;
    private CompteBancaireServices compteServices;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    private List<Depense> allExpenses;
    
    public ClientDepensesController() {
        depenseServices = new DepenseServices();
        compteServices = new CompteBancaireServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryFilter.getItems().addAll("All Categories", "Subscription", "Insurance", "Investment", "Transfer", "Other");
        categoryFilter.setValue("All Categories");
        categoryFilter.setOnAction(e -> filterAndDisplay());
        
        modeFilter.getItems().addAll("All Modes", "Auto Debit", "Manual", "Card", "Cash", "Transfer");
        modeFilter.setValue("All Modes");
        modeFilter.setOnAction(e -> filterAndDisplay());
        
        sortComboBox.getItems().addAll("Date (Newest)", "Date (Oldest)", "Amount (High)", "Amount (Low)");
        sortComboBox.setValue("Date (Newest)");
        sortComboBox.setOnAction(e -> filterAndDisplay());
        
        searchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplay());
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        
        headerUserNameLabel.setText(user.getPrenom() + " " + user.getNom());
        headerUserRoleLabel.setText(capitalizeFirst(user.getRole()));
        
        loadExpenses();
    }
    
    private void loadExpenses() {
        try {
            // Get all user's bank accounts
            List<CompteBancaire> userAccounts = compteServices.getByUserId(currentUser.getIdUser());
            
            // Get all expenses for these accounts
            allExpenses = depenseServices.afficher().stream()
                .filter(d -> userAccounts.stream().anyMatch(a -> a.getId() == d.getCompteId()))
                .collect(Collectors.toList());
            
            updateStats();
            filterAndDisplay();
            
        } catch (SQLException e) {
            showError("Error loading expenses: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateStats() {
        if (allExpenses == null || allExpenses.isEmpty()) {
            totalExpensesLabel.setText(currencyFormat.format(0));
            monthExpensesLabel.setText(currencyFormat.format(0));
            countLabel.setText("0");
            return;
        }
        
        BigDecimal total = allExpenses.stream()
            .map(Depense::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        BigDecimal monthTotal = allExpenses.stream()
            .filter(d -> !d.getDateDepense().isBefore(startOfMonth))
            .map(Depense::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        totalExpensesLabel.setText(currencyFormat.format(total));
        monthExpensesLabel.setText(currencyFormat.format(monthTotal));
        countLabel.setText(String.valueOf(allExpenses.size()));
    }
    
    private void filterAndDisplay() {
        if (allExpenses == null) return;
        
        String search = searchField.getText().toLowerCase();
        String category = categoryFilter.getValue();
        String mode = modeFilter.getValue();
        String sort = sortComboBox.getValue();
        
        List<Depense> filtered = allExpenses.stream()
            .filter(d -> "All Categories".equals(category) || d.getCategorie().equals(category))
            .filter(d -> "All Modes".equals(mode) || d.getModePaiement().equals(mode))
            .filter(d -> search.isEmpty() || d.getDescription().toLowerCase().contains(search))
            .collect(Collectors.toList());
        
        // Sort
        if ("Date (Newest)".equals(sort)) {
            filtered.sort((a, b) -> b.getDateDepense().compareTo(a.getDateDepense()));
        } else if ("Date (Oldest)".equals(sort)) {
            filtered.sort((a, b) -> a.getDateDepense().compareTo(b.getDateDepense()));
        } else if ("Amount (High)".equals(sort)) {
            filtered.sort((a, b) -> b.getMontant().compareTo(a.getMontant()));
        } else if ("Amount (Low)".equals(sort)) {
            filtered.sort((a, b) -> a.getMontant().compareTo(b.getMontant()));
        }
        
        displayExpenses(filtered);
    }
    
    private void displayExpenses(List<Depense> expenses) {
        expensesContainer.getChildren().clear();
        
        if (expenses.isEmpty()) {
            Label noData = new Label("No expenses found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            expensesContainer.getChildren().add(noData);
            return;
        }
        
        for (Depense expense : expenses) {
            expensesContainer.getChildren().add(createExpenseRow(expense));
        }
    }
    
    private HBox createExpenseRow(Depense expense) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 5, 0, 0, 1);");
        
        // Date
        VBox dateBox = new VBox(3);
        dateBox.setPrefWidth(100);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Label dateLabel = new Label(expense.getDateDepense().format(dateFormat));
        dateLabel.setStyle("-fx-font-size: 12; -fx-font-weight: 600; -fx-text-fill: #4a5568;");
        dateBox.getChildren().add(dateLabel);
        
        // Description
        VBox descBox = new VBox(5);
        descBox.setPrefWidth(350);
        Label descLabel = new Label(expense.getDescription());
        descLabel.setStyle("-fx-font-size: 14; -fx-font-weight: 600; -fx-text-fill: #2d3748;");
        descLabel.setWrapText(true);
        descBox.getChildren().add(descLabel);
        
        // Category badge
        Label categoryLabel = new Label(expense.getCategorie());
        categoryLabel.setPrefWidth(120);
        categoryLabel.setAlignment(Pos.CENTER);
        categoryLabel.setStyle("-fx-background-color: #edf2f7; -fx-text-fill: #4a5568; " +
                              "-fx-padding: 6 12; -fx-background-radius: 8; -fx-font-size: 11; -fx-font-weight: 600;");
        
        // Mode badge
        Label modeLabel = new Label(expense.getModePaiement());
        modeLabel.setPrefWidth(100);
        modeLabel.setAlignment(Pos.CENTER);
        modeLabel.setStyle("-fx-background-color: #e6fffa; -fx-text-fill: #319795; " +
                          "-fx-padding: 6 12; -fx-background-radius: 8; -fx-font-size: 11; -fx-font-weight: 600;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Amount
        Label amountLabel = new Label("- " + currencyFormat.format(expense.getMontant()));
        amountLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #f56565;");
        amountLabel.setPrefWidth(150);
        amountLabel.setAlignment(Pos.CENTER_RIGHT);
        
        row.getChildren().addAll(dateBox, descBox, categoryLabel, modeLabel, spacer, amountLabel);
        
        return row;
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
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Client Dashboard");
            
        } catch (Exception e) {
            showError("Error navigating to dashboard: " + e.getMessage());
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
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Accounts");
        } catch (Exception e) {
            showError("Error navigating to accounts: " + e.getMessage());
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
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
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
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Remboursements");
        } catch (Exception e) {
            showError("Error navigating to remboursements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyAbonnementClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientAbonnement.fxml"));
            Scene scene = new Scene(loader.load());
            ClientAbonnementController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Abonnement");
        } catch (Exception e) {
            showError("Error navigating to abonnement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleMyAssurancesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientAssurances.fxml"));
            Scene scene = new Scene(loader.load());
            ClientAssurancesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) headerUserNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Assurances");
        } catch (Exception e) {
            showError("Error navigating to assurances: " + e.getMessage());
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
}
