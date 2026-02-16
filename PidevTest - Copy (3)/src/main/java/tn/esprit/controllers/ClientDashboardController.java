package tn.esprit.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientDashboardController implements Initializable {
    
    @FXML
    private Label userNameLabel;
    
    @FXML
    private Label userRoleLabel;
    
    @FXML
    private MenuButton userMenuButton;
    
    @FXML
    private Label totalBalanceLabel;
    
    @FXML
    private Label totalExpensesLabel;
    
    @FXML
    private Label availableBalanceLabel;
    
    @FXML
    private Label totalSavingsLabel;
    
    @FXML
    private TableView<Depense> transactionsTable;
    
    private User currentUser;
    private CompteBancaireServices compteBancaireServices;
    private DepenseServices depenseServices;
    private NumberFormat currencyFormat;
    
    public ClientDashboardController() {
        compteBancaireServices = new CompteBancaireServices();
        depenseServices = new DepenseServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTransactionsTable();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        userNameLabel.setText(user.getPrenom() + " " + user.getNom());
        userRoleLabel.setText(capitalizeFirst(user.getRole()));
        loadDashboardData();
    }
    
    private void loadDashboardData() {
        try {
            // Get user's bank accounts
            List<CompteBancaire> comptes = compteBancaireServices.getByUserId(currentUser.getIdUser());
            
            // Calculate totals
            BigDecimal totalBalance = BigDecimal.ZERO;
            BigDecimal totalExpenses = BigDecimal.ZERO;
            
            for (CompteBancaire compte : comptes) {
                if (compte.isActif()) {
                    totalBalance = totalBalance.add(compte.getSolde());
                    
                    // Get expenses for this account
                    List<Depense> depenses = depenseServices.getByCompteId(compte.getId());
                    for (Depense depense : depenses) {
                        totalExpenses = totalExpenses.add(depense.getMontant());
                    }
                }
            }
            
            // Update labels
            totalBalanceLabel.setText(currencyFormat.format(totalBalance));
            totalExpensesLabel.setText(currencyFormat.format(totalExpenses));
            
            // Calculate available balance (assuming total balance - total expenses)
            BigDecimal availableBalance = totalBalance.subtract(totalExpenses);
            availableBalanceLabel.setText(currencyFormat.format(availableBalance));
            
            // Mock savings calculation (you can implement proper logic)
            BigDecimal savings = totalBalance.multiply(new BigDecimal("0.15"));
            totalSavingsLabel.setText(currencyFormat.format(savings));
            
            // Load recent transactions
            loadTransactions();
            
        } catch (SQLException e) {
            showError("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupTransactionsTable() {
        // Transaction details column
        TableColumn<Depense, String> detailsCol = new TableColumn<>("Transaction details");
        detailsCol.setCellValueFactory(data -> {
            String category = data.getValue().getCategorie();
            String desc = data.getValue().getDescription();
            return new SimpleStringProperty(category + "\n" + (desc != null ? desc : ""));
        });
        detailsCol.setPrefWidth(200);
        
        // Transaction ID column
        TableColumn<Depense, String> idCol = new TableColumn<>("Transaction ID");
        idCol.setCellValueFactory(data -> 
            new SimpleStringProperty("#" + String.format("%05d", data.getValue().getId())));
        idCol.setPrefWidth(100);
        
        // Date column
        TableColumn<Depense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy\nhh:mm a");
            return new SimpleStringProperty(data.getValue().getDateDepense().format(formatter));
        });
        dateCol.setPrefWidth(120);
        
        // Amount column
        TableColumn<Depense, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> 
            new SimpleStringProperty("- " + currencyFormat.format(data.getValue().getMontant())));
        amountCol.setPrefWidth(100);
        amountCol.setStyle("-fx-text-fill: #f56565; -fx-font-weight: 600;");
        
        // Action column
        TableColumn<Depense, String> actionCol = new TableColumn<>("");
        actionCol.setCellValueFactory(data -> new SimpleStringProperty("⋮"));
        actionCol.setPrefWidth(30);
        
        transactionsTable.getColumns().clear();
        transactionsTable.getColumns().addAll(detailsCol, idCol, dateCol, amountCol, actionCol);
    }
    
    private void loadTransactions() {
        try {
            ObservableList<Depense> allTransactions = FXCollections.observableArrayList();
            
            // Get all user's accounts and their transactions
            List<CompteBancaire> comptes = compteBancaireServices.getByUserId(currentUser.getIdUser());
            
            for (CompteBancaire compte : comptes) {
                List<Depense> depenses = depenseServices.getByCompteId(compte.getId());
                allTransactions.addAll(depenses);
            }
            
            // Sort by date (most recent first)
            allTransactions.sort((d1, d2) -> d2.getDateDepense().compareTo(d1.getDateDepense()));
            
            // Show only recent transactions
            if (allTransactions.size() > 10) {
                allTransactions = FXCollections.observableArrayList(allTransactions.subList(0, 10));
            }
            
            transactionsTable.setItems(allTransactions);
            
        } catch (SQLException e) {
            showError("Error loading transactions: " + e.getMessage());
            e.printStackTrace();
        }
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
    private void handleUserAssurancesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientAssurances.fxml"));
            Scene scene = new Scene(loader.load());
            
            ClientAssurancesController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - My Assurances");
            
        } catch (Exception e) {
            showError("Error opening assurances: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleProjectsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientProjets.fxml"));
            Scene scene = new Scene(loader.load());
            
            ClientProjetsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Investment Projects");
            
        } catch (Exception e) {
            showError("Error navigating to projects: " + e.getMessage());
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
