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
import tn.esprit.entities.Projet;
import tn.esprit.entities.User;
import tn.esprit.services.ProjetServices;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientProjetsController implements Initializable {
    
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private MenuButton userMenuButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private ComboBox<String> secteurFilterComboBox;
    @FXML private FlowPane projetsContainer;
    
    private User currentUser;
    private ProjetServices projetServices;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    private List<Projet> allProjets;
    
    public ClientProjetsController() {
        projetServices = new ProjetServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilters();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userNameLabel.setText(capitalizeFirst(user.getPrenom()) + " " + capitalizeFirst(user.getNom()));
            userRoleLabel.setText(capitalizeFirst(user.getRole()));
            loadProjets();
        }
    }
    
    private void setupFilters() {
        statusFilterComboBox.getItems().addAll("All Status", "EN_COURS", "TERMINE");
        statusFilterComboBox.setValue("All Status");
        statusFilterComboBox.setOnAction(e -> filterAndDisplayProjets());
        
        secteurFilterComboBox.getItems().addAll("All Sectors", "Technology", "Healthcare", "Finance", 
                                                "Real Estate", "Energy", "Agriculture");
        secteurFilterComboBox.setValue("All Sectors");
        secteurFilterComboBox.setOnAction(e -> filterAndDisplayProjets());
        
        searchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayProjets());
    }
    
    private void loadProjets() {
        try {
            allProjets = projetServices.afficher();
            filterAndDisplayProjets();
        } catch (SQLException e) {
            showError("Error loading projects: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filterAndDisplayProjets() {
        if (allProjets == null) return;
        
        String search = searchField.getText().toLowerCase();
        String statusFilter = statusFilterComboBox.getValue();
        String secteurFilter = secteurFilterComboBox.getValue();
        
        List<Projet> filtered = allProjets.stream()
            .filter(p -> search.isEmpty() || p.getNomProjet().toLowerCase().contains(search) ||
                        p.getDescription().toLowerCase().contains(search))
            .filter(p -> "All Status".equals(statusFilter) || p.getStatutProjet().equals(statusFilter))
            .filter(p -> "All Sectors".equals(secteurFilter) || p.getSecteur().equals(secteurFilter))
            .collect(java.util.stream.Collectors.toList());
        
        displayProjetCards(filtered);
    }
    
    private void displayProjetCards(List<Projet> projets) {
        projetsContainer.getChildren().clear();
        
        if (projets.isEmpty()) {
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(80));
            
            Label icon = new Label("📊");
            icon.setStyle("-fx-font-size: 60;");
            
            Label message = new Label("No projects found");
            message.setStyle("-fx-font-size: 18; -fx-text-fill: #a0aec0;");
            
            emptyState.getChildren().addAll(icon, message);
            projetsContainer.getChildren().add(emptyState);
            return;
        }
        
        for (Projet projet : projets) {
            projetsContainer.getChildren().add(createProjetCard(projet));
        }
    }
    
    private VBox createProjetCard(Projet projet) {
        VBox card = new VBox(20);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(350);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                     "-fx-padding: 30; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0, 0, 2);");
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        StackPane icon = new StackPane();
        icon.setPrefSize(50, 50);
        icon.setStyle("-fx-background-color: #667eea; -fx-background-radius: 25;");
        Label iconLabel = new Label("P");
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");
        icon.getChildren().add(iconLabel);
        
        VBox titleBox = new VBox(5);
        Label nameLabel = new Label(projet.getNomProjet());
        nameLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        nameLabel.setWrapText(true);
        Label secteurLabel = new Label(projet.getSecteur());
        secteurLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #a0aec0;");
        titleBox.getChildren().addAll(nameLabel, secteurLabel);
        
        header.getChildren().addAll(icon, titleBox);
        
        // Description
        Label descLabel = new Label(projet.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #718096;");
        descLabel.setMaxHeight(60);
        
        // Target Amount
        Label targetLabel = new Label("Target: " + currencyFormat.format(projet.getMontantObjectif()));
        targetLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        // Status badge
        Label statusLabel = new Label(formatStatus(projet.getStatutProjet()));
        statusLabel.setStyle("-fx-background-color: " + getStatusColor(projet.getStatutProjet()) + 
                           "; -fx-text-fill: white; -fx-padding: 6 14; -fx-background-radius: 10; " +
                           "-fx-font-size: 11; -fx-font-weight: bold;");
        
        // Dates
        HBox datesBox = new HBox(20);
        datesBox.setAlignment(Pos.CENTER_LEFT);
        
        if (projet.getDateDebut() != null) {
            VBox startBox = new VBox(3);
            Label startLbl = new Label("Start Date");
            startLbl.setStyle("-fx-font-size: 10; -fx-text-fill: #a0aec0;");
            Label startVal = new Label(projet.getDateDebut().format(dateFormat));
            startVal.setStyle("-fx-font-size: 12; -fx-text-fill: #2d3748; -fx-font-weight: 600;");
            startBox.getChildren().addAll(startLbl, startVal);
            datesBox.getChildren().add(startBox);
        }
        
        if (projet.getDateFin() != null) {
            VBox endBox = new VBox(3);
            Label endLbl = new Label("End Date");
            endLbl.setStyle("-fx-font-size: 10; -fx-text-fill: #a0aec0;");
            Label endVal = new Label(projet.getDateFin().format(dateFormat));
            endVal.setStyle("-fx-font-size: 12; -fx-text-fill: #2d3748; -fx-font-weight: 600;");
            endBox.getChildren().addAll(endLbl, endVal);
            datesBox.getChildren().add(endBox);
        }
        
        Separator separator = new Separator();
        
        // Invest button
        Button investBtn = new Button("View Details");
        investBtn.setMaxWidth(Double.MAX_VALUE);
        investBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                         "-fx-font-size: 14; -fx-font-weight: 600; -fx-padding: 12 20; " +
                         "-fx-background-radius: 10; -fx-cursor: hand;");
        investBtn.setOnAction(e -> showInfo("Investment feature - coming soon!"));
        
        card.getChildren().addAll(header, descLabel, targetLabel, statusLabel, datesBox, separator, investBtn);
        
        return card;
    }
    
    private String formatStatus(String status) {
        if (status == null) return "";
        return status.replace("_", " ");
    }
    
    private String getStatusColor(String status) {
        if (status == null) return "#a0aec0";
        switch (status) {
            case "EN_COURS": return "#48bb78";
            case "TERMINE": return "#4299e1";
            case "ANNULE": return "#f56565";
            default: return "#a0aec0";
        }
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
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
    private void handleMyAbonnementClick() {
        handleUserAbonnementClick();
    }

    @FXML
    private void handleMyAssurancesClick() {
        handleUserAssurancesClick();
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
}
