package tn.esprit.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminDashboardController implements Initializable {
    
    // Navigation buttons
    @FXML private Button dashboardBtn;
    @FXML private Button usersBtn;
    @FXML private Button abonnementsBtn;
    @FXML private Button assurancesBtn;
    @FXML private Button contratsBtn;
    @FXML private Button projetsBtn;
    @FXML private Button investissementsBtn;
    @FXML private Button comptesBtn;
    @FXML private Button depensesBtn;
    @FXML private Button creditsBtn;
    @FXML private Button remboursementsBtn;
    
    // Views
    @FXML private VBox dashboardView;
    @FXML private VBox usersView;
    @FXML private VBox abonnementsView;
    @FXML private VBox assurancesView;
    @FXML private VBox contratsView;
    @FXML private VBox projetsView;
    @FXML private VBox investissementsView;
    @FXML private VBox comptesView;
    @FXML private VBox depensesView;
    @FXML private VBox creditsView;
    @FXML private VBox remboursementsView;
    
    // Dashboard labels
    @FXML private Label adminNameLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalSubscriptionsLabel;
    @FXML private Label totalInsuranceLabel;
    
    // Users management
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilterComboBox;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private FlowPane usersCardsContainer;
    
    // Abonnements management
    @FXML private TextField abonSearchField;
    @FXML private ComboBox<String> abonTypeFilter;
    @FXML private ComboBox<String> abonSortComboBox;
    @FXML private FlowPane abonnementsCardsContainer;
    
    // Assurances management
    @FXML private TextField assurSearchField;
    @FXML private ComboBox<String> assurTypeFilter;
    @FXML private ComboBox<String> assurStatusFilter;
    @FXML private ComboBox<String> assurSortComboBox;
    @FXML private FlowPane assurancesCardsContainer;
    
    // Contrats management
    @FXML private TextField contratSearchField;
    @FXML private ComboBox<String> contratStatusFilter;
    @FXML private ComboBox<String> contratSortComboBox;
    @FXML private FlowPane contratsCardsContainer;
    
    // Projets management
    @FXML private TextField projetSearchField;
    @FXML private ComboBox<String> projetStatusFilter;
    @FXML private ComboBox<String> projetSortComboBox;
    @FXML private FlowPane projetsCardsContainer;
    
    // Investissements management
    @FXML private TextField invesSearchField;
    @FXML private ComboBox<String> invesStatusFilter;
    @FXML private ComboBox<String> invesSortComboBox;
    @FXML private FlowPane investissementsCardsContainer;
    
    // Comptes management
    @FXML private TextField compteSearchField;
    @FXML private ComboBox<String> compteTypeFilter;
    @FXML private ComboBox<String> compteSortComboBox;
    @FXML private FlowPane comptesCardsContainer;
    
    // Depenses management
    @FXML private TextField depenseSearchField;
    @FXML private ComboBox<String> depenseCatFilter;
    @FXML private ComboBox<String> depenseSortComboBox;
    @FXML private FlowPane depensesCardsContainer;
    
    // Credits management
    @FXML private TextField creditSearchField;
    @FXML private ComboBox<String> creditStatusFilter;
    @FXML private ComboBox<String> creditSortComboBox;
    @FXML private FlowPane creditsCardsContainer;
    
    // Remboursements management
    @FXML private TextField remboursementSearchField;
    @FXML private ComboBox<String> remboursementTypeFilter;
    @FXML private ComboBox<String> remboursementSortComboBox;
    @FXML private FlowPane remboursementsCardsContainer;
    
    // Tables
    @FXML private TableView activityTable;
    
    private User currentUser;
    private UserServices userServices;
    private AbonnementServices abonnementServices;
    private AssuranceServices assuranceServices;
    private tn.esprit.services.ContratAssuranceServices contratAssuranceServices;
    private ProjetServices projetServices;
    private tn.esprit.services.InvestissementServices investissementServices;
    private tn.esprit.services.CompteBancaireServices compteBancaireServices;
    private tn.esprit.services.DepenseServices depenseServices;
    private tn.esprit.services.CreditServices creditServices;
    private tn.esprit.services.RemboursementServices remboursementServices;
    private NumberFormat currencyFormat;
    
    private List<User> allUsers;
    private List<tn.esprit.entities.Abonnement> allAbonnements;
    private List<tn.esprit.entities.Assurance> allAssurances;
    private List<tn.esprit.entities.ContratAssurance> allContrats;
    private List<Projet> allProjets;
    private List<tn.esprit.entities.Investissement> allInvestissements;
    private List<tn.esprit.entities.CompteBancaire> allComptes;
    private List<tn.esprit.entities.Depense> allDepenses;
    private List<tn.esprit.entities.Credit> allCredits;
    private List<tn.esprit.entities.Remboursement> allRemboursements;
    
    public AdminDashboardController() {
        userServices = new UserServices();
        abonnementServices = new AbonnementServices();
        assuranceServices = new AssuranceServices();
        contratAssuranceServices = new tn.esprit.services.ContratAssuranceServices();
        projetServices = new ProjetServices();
        investissementServices = new tn.esprit.services.InvestissementServices();
        compteBancaireServices = new tn.esprit.services.CompteBancaireServices();
        depenseServices = new tn.esprit.services.DepenseServices();
        creditServices = new tn.esprit.services.CreditServices();
        remboursementServices = new tn.esprit.services.RemboursementServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUsersData();
        updateDashboardStats();
        setupFilters();
        setupSearchListener();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        adminNameLabel.setText(user.getPrenom() + " " + user.getNom());
        loadUsersData();
        updateDashboardStats();
    }
    
    // View Switching Methods
    @FXML
    private void showDashboard() {
        switchView(dashboardView);
        setActiveButton(dashboardBtn);
    }
    
    @FXML
    private void showUsers() {
        switchView(usersView);
        setActiveButton(usersBtn);
        loadUsersCards();
    }
    
    @FXML
    private void showAbonnements() {
        switchView(abonnementsView);
        setActiveButton(abonnementsBtn);
        loadAbonnementsCards();
    }
    
    @FXML
    private void showAssurances() {
        switchView(assurancesView);
        setActiveButton(assurancesBtn);
        loadAssurancesCards();
    }
    
    @FXML
    private void showContrats() {
        switchView(contratsView);
        setActiveButton(contratsBtn);
        loadContratsCards();
    }
    
    @FXML
    private void showProjets() {
        switchView(projetsView);
        setActiveButton(projetsBtn);
        loadProjetsCards();
    }
    
    @FXML
    private void showInvestissements() {
        switchView(investissementsView);
        setActiveButton(investissementsBtn);
        loadInvestissementsCards();
    }
    
    @FXML
    private void showComptes() {
        switchView(comptesView);
        setActiveButton(comptesBtn);
        loadComptesCards();
    }
    
    @FXML
    private void showDepenses() {
        switchView(depensesView);
        setActiveButton(depensesBtn);
        loadDepensesCards();
    }
    
    @FXML
    private void showCredits() {
        switchView(creditsView);
        setActiveButton(creditsBtn);
        loadCreditsCards();
    }
    
    @FXML
    private void showRemboursements() {
        switchView(remboursementsView);
        setActiveButton(remboursementsBtn);
        loadRemboursementsCards();
    }
    
    private void switchView(VBox viewToShow) {
        dashboardView.setVisible(false);
        usersView.setVisible(false);
        abonnementsView.setVisible(false);
        assurancesView.setVisible(false);
        contratsView.setVisible(false);
        projetsView.setVisible(false);
        investissementsView.setVisible(false);
        comptesView.setVisible(false);
        depensesView.setVisible(false);
        creditsView.setVisible(false);
        remboursementsView.setVisible(false);
        
        viewToShow.setVisible(true);
    }
    
    private void setActiveButton(Button activeBtn) {
        dashboardBtn.getStyleClass().remove("nav-button-active");
        usersBtn.getStyleClass().remove("nav-button-active");
        abonnementsBtn.getStyleClass().remove("nav-button-active");
        assurancesBtn.getStyleClass().remove("nav-button-active");
        contratsBtn.getStyleClass().remove("nav-button-active");
        projetsBtn.getStyleClass().remove("nav-button-active");
        investissementsBtn.getStyleClass().remove("nav-button-active");
        comptesBtn.getStyleClass().remove("nav-button-active");
        depensesBtn.getStyleClass().remove("nav-button-active");
        creditsBtn.getStyleClass().remove("nav-button-active");
        remboursementsBtn.getStyleClass().remove("nav-button-active");
        
        if (!activeBtn.getStyleClass().contains("nav-button-active")) {
            activeBtn.getStyleClass().add("nav-button-active");
        }
    }
    
    // Setup Filters
    private void setupFilters() {
        // Role filter
        roleFilterComboBox.getItems().addAll("All Roles", "admin", "client", "organisateur");
        roleFilterComboBox.setValue("All Roles");
        roleFilterComboBox.setOnAction(e -> filterAndDisplayUsers());
        
        // Status filter
        statusFilterComboBox.getItems().addAll("All Status", "actif", "desactive");
        statusFilterComboBox.setValue("All Status");
        statusFilterComboBox.setOnAction(e -> filterAndDisplayUsers());
        
        // Sort options
        sortComboBox.getItems().addAll("Name (A-Z)", "Name (Z-A)", "Email (A-Z)", "Newest First", "Oldest First");
        sortComboBox.setValue("Name (A-Z)");
        sortComboBox.setOnAction(e -> filterAndDisplayUsers());
    }
    
    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAndDisplayUsers();
        });
    }
    
    @FXML
    private void handleResetFilters() {
        searchField.clear();
        roleFilterComboBox.setValue("All Roles");
        statusFilterComboBox.setValue("All Status");
        sortComboBox.setValue("Name (A-Z)");
        filterAndDisplayUsers();
    }
    
    // Load Users Data
    private void loadUsersData() {
        try {
            allUsers = userServices.afficher();
        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadUsersCards() {
        filterAndDisplayUsers();
    }
    
    private void filterAndDisplayUsers() {
        if (allUsers == null) return;
        
        List<User> filteredUsers = allUsers.stream()
            .filter(user -> {
                // Search filter
                String searchText = searchField.getText().toLowerCase();
                if (!searchText.isEmpty()) {
                    String fullName = (user.getPrenom() + " " + user.getNom()).toLowerCase();
                    String email = user.getEmail().toLowerCase();
                    String phone = user.getTelephone() != null ? user.getTelephone().toLowerCase() : "";
                    
                    if (!fullName.contains(searchText) && 
                        !email.contains(searchText) && 
                        !phone.contains(searchText)) {
                        return false;
                    }
                }
                
                // Role filter
                String roleFilter = roleFilterComboBox.getValue();
                if (!"All Roles".equals(roleFilter) && !user.getRole().equals(roleFilter)) {
                    return false;
                }
                
                // Status filter
                String statusFilter = statusFilterComboBox.getValue();
                if (!"All Status".equals(statusFilter) && !user.getStatutCompte().equals(statusFilter)) {
                    return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        // Sort
        String sortOption = sortComboBox.getValue();
        switch (sortOption) {
            case "Name (A-Z)":
                filteredUsers.sort((u1, u2) -> (u1.getNom() + u1.getPrenom()).compareToIgnoreCase(u2.getNom() + u2.getPrenom()));
                break;
            case "Name (Z-A)":
                filteredUsers.sort((u1, u2) -> (u2.getNom() + u2.getPrenom()).compareToIgnoreCase(u1.getNom() + u1.getPrenom()));
                break;
            case "Email (A-Z)":
                filteredUsers.sort((u1, u2) -> u1.getEmail().compareToIgnoreCase(u2.getEmail()));
                break;
            case "Newest First":
                filteredUsers.sort((u1, u2) -> Integer.compare(u2.getIdUser(), u1.getIdUser()));
                break;
            case "Oldest First":
                filteredUsers.sort((u1, u2) -> Integer.compare(u1.getIdUser(), u2.getIdUser()));
                break;
        }
        
        displayUserCards(filteredUsers);
    }
    
    private void displayUserCards(List<User> users) {
        usersCardsContainer.getChildren().clear();
        
        for (User user : users) {
            VBox userCard = createUserCard(user);
            usersCardsContainer.getChildren().add(userCard);
        }
        
        // Show message if no users found
        if (users.isEmpty()) {
            Label noUsersLabel = new Label("No users found matching your criteria");
            noUsersLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            usersCardsContainer.getChildren().add(noUsersLabel);
        }
    }
    
    private VBox createUserCard(User user) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPrefWidth(350);
        card.setMinHeight(180);
        card.setStyle(card.getStyle() + "; -fx-padding: 20;");
        
        // User Info Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Avatar
        StackPane avatar = new StackPane();
        avatar.setPrefWidth(50);
        avatar.setPrefHeight(50);
        avatar.setStyle("-fx-background-color: #667eea; -fx-background-radius: 25;");
        Label initials = new Label(getInitials(user));
        initials.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        avatar.getChildren().add(initials);
        
        // User Info
        VBox userInfo = new VBox(5);
        Label nameLabel = new Label(user.getPrenom() + " " + user.getNom());
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: 600; -fx-text-fill: #2d3748;");
        
        Label emailLabel = new Label(user.getEmail());
        emailLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #718096;");
        
        userInfo.getChildren().addAll(nameLabel, emailLabel);
        HBox.setHgrow(userInfo, Priority.ALWAYS);
        
        // Status Badge
        Label statusBadge = new Label(user.getStatutCompte().toUpperCase());
        statusBadge.getStyleClass().add("actif".equals(user.getStatutCompte()) ? "badge-green" : "badge-red");
        
        header.getChildren().addAll(avatar, userInfo, statusBadge);
        
        // User Details
        VBox details = new VBox(8);
        
        HBox phoneRow = new HBox(10);
        phoneRow.setAlignment(Pos.CENTER_LEFT);
        Label phoneIcon = new Label("Phone:");
        phoneIcon.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12; -fx-font-weight: 600;");
        Label phoneValue = new Label(user.getTelephone() != null ? user.getTelephone() : "N/A");
        phoneValue.setStyle("-fx-text-fill: #2d3748; -fx-font-size: 12;");
        phoneRow.getChildren().addAll(phoneIcon, phoneValue);
        
        HBox roleRow = new HBox(10);
        roleRow.setAlignment(Pos.CENTER_LEFT);
        Label roleIcon = new Label("Role:");
        roleIcon.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12; -fx-font-weight: 600;");
        Label roleValue = new Label(user.getRole().toUpperCase());
        roleValue.setStyle("-fx-text-fill: #667eea; -fx-font-size: 12; -fx-font-weight: 600;");
        roleRow.getChildren().addAll(roleIcon, roleValue);
        
        HBox dobRow = new HBox(10);
        dobRow.setAlignment(Pos.CENTER_LEFT);
        Label dobIcon = new Label("Birth:");
        dobIcon.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12; -fx-font-weight: 600;");
        Label dobValue = new Label(user.getDateNaissance() != null ? 
            user.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        dobValue.setStyle("-fx-text-fill: #2d3748; -fx-font-size: 12;");
        dobRow.getChildren().addAll(dobIcon, dobValue);
        
        details.getChildren().addAll(phoneRow, roleRow, dobRow);
        
        // Separator
        Separator separator = new Separator();
        
        // Action Buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-background-radius: 8; " +
                          "-fx-padding: 8 15; -fx-font-size: 12; -fx-font-weight: 600; -fx-cursor: hand;");
        modifyBtn.setOnAction(e -> handleModifyUser(user));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; -fx-background-radius: 8; " +
                          "-fx-padding: 8 15; -fx-font-size: 12; -fx-font-weight: 600; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteUser(user));
        
        Button toggleBtn = new Button("actif".equals(user.getStatutCompte()) ? "Block" : "Unblock");
        toggleBtn.setStyle("-fx-background-color: " + ("actif".equals(user.getStatutCompte()) ? "#ed8936" : "#48bb78") + 
                          "; -fx-text-fill: white; -fx-background-radius: 8; " +
                          "-fx-padding: 8 15; -fx-font-size: 12; -fx-font-weight: 600; -fx-cursor: hand;");
        toggleBtn.setOnAction(e -> handleToggleUserStatus(user, toggleBtn));
        
        actions.getChildren().addAll(modifyBtn, toggleBtn, deleteBtn);
        
        card.getChildren().addAll(header, details, separator, actions);
        
        return card;
    }
    
    private String getInitials(User user) {
        String initials = "";
        if (user.getPrenom() != null && !user.getPrenom().isEmpty()) {
            initials += user.getPrenom().charAt(0);
        }
        if (user.getNom() != null && !user.getNom().isEmpty()) {
            initials += user.getNom().charAt(0);
        }
        return initials.toUpperCase();
    }
    
    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddUserDialog.fxml"));
            VBox dialogRoot = loader.load();
            AddUserDialogController dialogController = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Credix - Add User");
            dialogStage.initOwner(usersCardsContainer.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            if (dialogController.isUserAdded()) {
                showSuccess("User added successfully");
                loadUsersData();
                loadUsersCards();
                updateDashboardStats();
            }
        } catch (Exception e) {
            showError("Error opening add user dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleModifyUser(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyUserDialog.fxml"));
            VBox dialogRoot = loader.load();
            ModifyUserDialogController dialogController = loader.getController();
            dialogController.setUser(user);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Credix - Modify User");
            dialogStage.initOwner(usersCardsContainer.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            if (dialogController.isUserModified()) {
                showSuccess("User updated successfully");
                loadUsersData();
                loadUsersCards();
                updateDashboardStats();
            }
        } catch (Exception e) {
            showError("Error opening modify user dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete User");
        confirmAlert.setHeaderText("Are you sure you want to delete this user?");
        confirmAlert.setContentText(user.getPrenom() + " " + user.getNom() + " (" + user.getEmail() + ")");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userServices.supprimer(user.getIdUser());
                    showSuccess("User deleted successfully");
                    loadUsersData();
                    loadUsersCards();
                    updateDashboardStats();
                } catch (SQLException e) {
                    showError("Error deleting user: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void handleToggleUserStatus(User user, Button toggleBtn) {
        String newStatus = "actif".equals(user.getStatutCompte()) ? "desactive" : "actif";
        String action = "actif".equals(newStatus) ? "unblock" : "block";
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Change User Status");
        confirmAlert.setHeaderText("Are you sure you want to " + action + " this user?");
        confirmAlert.setContentText(user.getPrenom() + " " + user.getNom());
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    user.setStatutCompte(newStatus);
                    userServices.modifier(user);
                    showSuccess("User status updated successfully");
                    loadUsersData();
                    loadUsersCards();
                    updateDashboardStats();
                } catch (SQLException e) {
                    showError("Error updating user status: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void updateDashboardStats() {
        try {
            // Total Users
            List<User> users = userServices.afficher();
            totalUsersLabel.setText(String.valueOf(users.size()));
            
            // Total Subscriptions
            List<Abonnement> abonnements = abonnementServices.afficher();
            long activeSubscriptions = abonnements.stream()
                    .filter(a -> a.isActif())
                    .count();
            totalSubscriptionsLabel.setText(String.valueOf(activeSubscriptions));
            
            // Total Insurance
            List<Assurance> assurances = assuranceServices.afficher();
            long activeInsurance = assurances.stream()
                    .filter(a -> "ACTIF".equals(a.getStatut()))
                    .count();
            totalInsuranceLabel.setText(String.valueOf(activeInsurance));
            
        } catch (SQLException e) {
            showError("Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    // ============== ABONNEMENTS MANAGEMENT ==============
    
    private void loadAbonnementsCards() {
        try {
            allAbonnements = abonnementServices.afficher();
            
            // Setup filters
            if (abonTypeFilter.getItems().isEmpty()) {
                abonTypeFilter.getItems().addAll("All Types", "Bronze", "Silver", "Gold", "Platinum");
                abonTypeFilter.setValue("All Types");
                abonTypeFilter.setOnAction(e -> filterAndDisplayAbonnements());
                
                abonSortComboBox.getItems().addAll("Type A-Z", "Price: Low to High", "Price: High to Low");
                abonSortComboBox.setValue("Type A-Z");
                abonSortComboBox.setOnAction(e -> filterAndDisplayAbonnements());
                
                abonSearchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayAbonnements());
            }
            
            filterAndDisplayAbonnements();
        } catch (SQLException e) {
            showError("Error loading abonnements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filterAndDisplayAbonnements() {
        if (allAbonnements == null) return;
        
        String search = abonSearchField.getText().toLowerCase();
        String typeFilter = abonTypeFilter.getValue();
        String sortBy = abonSortComboBox.getValue();
        
        List<tn.esprit.entities.Abonnement> filtered = allAbonnements.stream()
            .filter(a -> search.isEmpty() || a.getTypeAbonnement().toLowerCase().contains(search))
            .filter(a -> "All Types".equals(typeFilter) || a.getTypeAbonnement().equals(typeFilter))
            .sorted((a1, a2) -> {
                if ("Price: Low to High".equals(sortBy)) {
                    return a1.getPrixMensuel().compareTo(a2.getPrixMensuel());
                } else if ("Price: High to Low".equals(sortBy)) {
                    return a2.getPrixMensuel().compareTo(a1.getPrixMensuel());
                } else {
                    return a1.getTypeAbonnement().compareTo(a2.getTypeAbonnement());
                }
            })
            .collect(java.util.stream.Collectors.toList());
        
        displayAbonnementCards(filtered);
    }
    
    private void displayAbonnementCards(List<tn.esprit.entities.Abonnement> abonnements) {
        abonnementsCardsContainer.getChildren().clear();
        
        if (abonnements.isEmpty()) {
            Label noData = new Label("No abonnements found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            abonnementsCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (tn.esprit.entities.Abonnement abon : abonnements) {
            abonnementsCardsContainer.getChildren().add(createAbonnementCard(abon));
        }
    }
    
    private VBox createAbonnementCard(tn.esprit.entities.Abonnement abon) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2); -fx-cursor: hand;");
        
        // Add double-click handler to view subscriptions
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleViewAbonnementSubscriptions(abon);
            }
        });
        
        // Header
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        StackPane typeIcon = new StackPane();
        typeIcon.setPrefSize(40, 40);
        String color = getAbonTypeColor(abon.getTypeAbonnement());
        typeIcon.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 20;");
        Label iconLabel = new Label(abon.getTypeAbonnement().substring(0, 1));
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        typeIcon.getChildren().add(iconLabel);
        
        VBox titleBox = new VBox(3);
        Label typeLabel = new Label(abon.getTypeAbonnement().toUpperCase());
        typeLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        Label durationLabel = new Label(capitalizeFirst(abon.getDuree()));
        durationLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #a0aec0; -fx-font-weight: 600;");
        titleBox.getChildren().addAll(typeLabel, durationLabel);
        
        header.getChildren().addAll(typeIcon, titleBox);
        
        // Price
        Label priceLabel = new Label(currencyFormat.format(abon.getPrixMensuel()) + "/month");
        priceLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        // Details
        VBox details = new VBox(5);
        if (abon.getDescription() != null && !abon.getDescription().isEmpty()) {
            Label descLabel = new Label(abon.getDescription());
            descLabel.setWrapText(true);
            descLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #718096;");
            details.getChildren().add(descLabel);
        }
        
        Separator separator = new Separator();
        
        // Actions
        HBox actions = new HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-weight: 600;");
        modifyBtn.setOnAction(e -> handleModifyAbonnement(abon));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-weight: 600;");
        deleteBtn.setOnAction(e -> handleDeleteAbonnement(abon));
        
        actions.getChildren().addAll(modifyBtn, deleteBtn);
        
        card.getChildren().addAll(header, priceLabel, details, separator, actions);
        
        return card;
    }
    
    private void handleViewAbonnementSubscriptions(tn.esprit.entities.Abonnement abon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewAbonnementSubscriptionsDialog.fxml"));
            VBox dialogRoot = loader.load();
            ViewAbonnementSubscriptionsDialogController controller = loader.getController();
            controller.setAbonnement(abon);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Subscriptions for " + abon.getTypeAbonnement());
            dialogStage.initOwner(abonnementsCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error viewing subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAddAbonnement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddAbonnementDialog.fxml"));
            VBox dialogRoot = loader.load();
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Add Abonnement");
            dialogStage.initOwner(abonnementsCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
            loadAbonnementsCards();
        } catch (Exception e) {
            showError("Error opening dialog: " + e.getMessage());
        }
    }
    
    private void handleModifyAbonnement(tn.esprit.entities.Abonnement abon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyAbonnementDialog.fxml"));
            VBox dialogRoot = loader.load();
            ModifyAbonnementDialogController controller = loader.getController();
            controller.setAbonnement(abon);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Modify Abonnement");
            dialogStage.initOwner(abonnementsCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
            loadAbonnementsCards();
        } catch (Exception e) {
            showError("Error opening dialog: " + e.getMessage());
        }
    }
    
    private void handleDeleteAbonnement(tn.esprit.entities.Abonnement abon) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Abonnement");
        confirm.setContentText("Are you sure you want to delete this abonnement?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    abonnementServices.supprimer(abon.getIdAbonnement());
                    loadAbonnementsCards();
                    showSuccess("Abonnement deleted successfully!");
                } catch (SQLException e) {
                    showError("Error deleting abonnement: " + e.getMessage());
                }
            }
        });
    }
    
    private String getAbonTypeColor(String type) {
        if (type == null) return "#667eea";
        switch (type.toLowerCase()) {
            case "bronze": return "#cd7f32";
            case "silver": return "#c0c0c0";
            case "gold": return "#ffd700";
            case "platinum": return "#e5e4e2";
            default: return "#667eea";
        }
    }
    
    // ============== ASSURANCES MANAGEMENT ==============
    
    private void loadAssurancesCards() {
        try {
            allAssurances = assuranceServices.afficher();
            
            // Setup filters
            if (assurTypeFilter.getItems().isEmpty()) {
                assurTypeFilter.getItems().addAll("All Types", "VIE", "SANTE", "AUTO", "HABITATION", 
                                                  "RESPONSABILITE_CIVILE", "SCOLAIRE", "VOYAGE", "PROFESSIONNELLE");
                assurTypeFilter.setValue("All Types");
                assurTypeFilter.setOnAction(e -> filterAndDisplayAssurances());
                
                assurStatusFilter.getItems().addAll("All Status", "ACTIF", "EXPIRE", "RESILIE", "SUSPENDU");
                assurStatusFilter.setValue("All Status");
                assurStatusFilter.setOnAction(e -> filterAndDisplayAssurances());
                
                assurSortComboBox.getItems().addAll("Type A-Z", "Company A-Z", "Premium: Low to High", "Premium: High to Low");
                assurSortComboBox.setValue("Type A-Z");
                assurSortComboBox.setOnAction(e -> filterAndDisplayAssurances());
                
                assurSearchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayAssurances());
            }
            
            filterAndDisplayAssurances();
        } catch (SQLException e) {
            showError("Error loading assurances: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filterAndDisplayAssurances() {
        if (allAssurances == null) return;
        
        String search = assurSearchField.getText().toLowerCase();
        String typeFilter = assurTypeFilter.getValue();
        String statusFilter = assurStatusFilter.getValue();
        String sortBy = assurSortComboBox.getValue();
        
        List<tn.esprit.entities.Assurance> filtered = allAssurances.stream()
            .filter(a -> search.isEmpty() || a.getTypeAssurance().toLowerCase().contains(search) || 
                        a.getCompagnie().toLowerCase().contains(search))
            .filter(a -> "All Types".equals(typeFilter) || a.getTypeAssurance().equals(typeFilter))
            .filter(a -> "All Status".equals(statusFilter) || a.getStatut().equals(statusFilter))
            .sorted((a1, a2) -> {
                if ("Company A-Z".equals(sortBy)) {
                    return a1.getCompagnie().compareTo(a2.getCompagnie());
                } else if ("Premium: Low to High".equals(sortBy)) {
                    return a1.getPrimeMensuelle().compareTo(a2.getPrimeMensuelle());
                } else if ("Premium: High to Low".equals(sortBy)) {
                    return a2.getPrimeMensuelle().compareTo(a1.getPrimeMensuelle());
                } else {
                    return a1.getTypeAssurance().compareTo(a2.getTypeAssurance());
                }
            })
            .collect(java.util.stream.Collectors.toList());
        
        displayAssuranceCards(filtered);
    }
    
    private void displayAssuranceCards(List<tn.esprit.entities.Assurance> assurances) {
        assurancesCardsContainer.getChildren().clear();
        
        if (assurances.isEmpty()) {
            Label noData = new Label("No assurances found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            assurancesCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (tn.esprit.entities.Assurance assur : assurances) {
            assurancesCardsContainer.getChildren().add(createAssuranceCard(assur));
        }
    }
    
    private VBox createAssuranceCard(tn.esprit.entities.Assurance a) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2);");
        
        // Header with icon and status
        HBox topRow = new HBox();
        topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        StackPane typeIcon = new StackPane();
        typeIcon.setPrefSize(40, 40);
        String color = getAssurTypeColor(a.getTypeAssurance());
        typeIcon.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 20;");
        Label iconLabel = new Label(a.getTypeAssurance().substring(0, 1));
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        typeIcon.getChildren().add(iconLabel);
        
        VBox titleBox = new VBox(3);
        Label typeLabel = new Label(formatAssurType(a.getTypeAssurance()));
        typeLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        Label companyLabel = new Label(a.getCompagnie());
        companyLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #a0aec0;");
        titleBox.getChildren().addAll(typeLabel, companyLabel);
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label statusLabel = new Label(a.getStatut());
        statusLabel.setStyle("-fx-background-color: " + getAssurStatusColor(a.getStatut()) + 
                           "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8; " +
                           "-fx-font-size: 9; -fx-font-weight: bold;");
        
        topRow.getChildren().addAll(typeIcon, titleBox, spacer, statusLabel);
        
        // Details
        VBox details = new VBox(6);
        details.getChildren().addAll(
            createInfoRow("Coverage:", currencyFormat.format(a.getMontantCouverture())),
            createInfoRow("Premium:", currencyFormat.format(a.getPrimeMensuelle()) + "/mo")
        );
        
        Separator separator = new Separator();
        
        // Actions
        HBox actions = new HBox(8);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        modifyBtn.setOnAction(e -> handleModifyAssurance(a));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        deleteBtn.setOnAction(e -> handleDeleteAssurance(a));
        
        actions.getChildren().addAll(modifyBtn, deleteBtn);
        
        card.getChildren().addAll(topRow, details, separator, actions);
        
        return card;
    }
    
    @FXML
    private void handleAddAssurance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddAssuranceDialog.fxml"));
            VBox dialogRoot = loader.load();
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Add Assurance");
            dialogStage.initOwner(assurancesCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
            loadAssurancesCards();
        } catch (Exception e) {
            showError("Error opening dialog: " + e.getMessage());
        }
    }
    
    private void handleModifyAssurance(tn.esprit.entities.Assurance a) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyAssuranceDialog.fxml"));
            VBox dialogRoot = loader.load();
            ModifyAssuranceDialogController controller = loader.getController();
            controller.setAssurance(a);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Modify Assurance");
            dialogStage.initOwner(assurancesCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
            loadAssurancesCards();
        } catch (Exception e) {
            showError("Error opening dialog: " + e.getMessage());
        }
    }
    
    private void handleDeleteAssurance(tn.esprit.entities.Assurance a) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Assurance");
        confirm.setContentText("Are you sure you want to delete this assurance?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    assuranceServices.supprimer(a.getId());
                    loadAssurancesCards();
                    showSuccess("Assurance deleted successfully!");
                } catch (SQLException e) {
                    showError("Error deleting assurance: " + e.getMessage());
                }
            }
        });
    }
    
    private String getAssurTypeColor(String type) {
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
    
    private String getAssurStatusColor(String status) {
        if (status == null) return "#a0aec0";
        switch (status) {
            case "ACTIF": return "#48bb78";
            case "EXPIRE": return "#f56565";
            case "RESILIE": return "#e53e3e";
            case "SUSPENDU": return "#ed8936";
            default: return "#a0aec0";
        }
    }
    
    private String formatAssurType(String type) {
        if (type == null) return "";
        return type.replace("_", " ");
    }
    
    // ============== CONTRATS MANAGEMENT ==============
    
    private void loadContratsCards() {
        try {
            allContrats = contratAssuranceServices.afficher();
            
            // Setup filters
            if (contratStatusFilter.getItems().isEmpty()) {
                contratStatusFilter.getItems().addAll("All Status", "ACTIF", "EXPIRE", "RESILIE", "EN_ATTENTE");
                contratStatusFilter.setValue("All Status");
                contratStatusFilter.setOnAction(e -> filterAndDisplayContrats());
                
                contratSortComboBox.getItems().addAll("Date: Newest First", "Date: Oldest First", "Contract # A-Z");
                contratSortComboBox.setValue("Date: Newest First");
                contratSortComboBox.setOnAction(e -> filterAndDisplayContrats());
                
                contratSearchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayContrats());
            }
            
            filterAndDisplayContrats();
        } catch (SQLException e) {
            showError("Error loading contrats: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filterAndDisplayContrats() {
        if (allContrats == null) return;
        
        String search = contratSearchField.getText().toLowerCase();
        String statusFilter = contratStatusFilter.getValue();
        String sortBy = contratSortComboBox.getValue();
        
        List<tn.esprit.entities.ContratAssurance> filtered = allContrats.stream()
            .filter(c -> search.isEmpty() || c.getNumeroContrat().toLowerCase().contains(search))
            .filter(c -> "All Status".equals(statusFilter) || c.getStatut().equals(statusFilter))
            .sorted((c1, c2) -> {
                if ("Date: Oldest First".equals(sortBy)) {
                    return c1.getDateSignature() != null && c2.getDateSignature() != null ? 
                           c1.getDateSignature().compareTo(c2.getDateSignature()) : 0;
                } else if ("Contract # A-Z".equals(sortBy)) {
                    return c1.getNumeroContrat().compareTo(c2.getNumeroContrat());
                } else {
                    return c1.getDateSignature() != null && c2.getDateSignature() != null ? 
                           c2.getDateSignature().compareTo(c1.getDateSignature()) : 0;
                }
            })
            .collect(java.util.stream.Collectors.toList());
        
        displayContratCards(filtered);
    }
    
    private void displayContratCards(List<tn.esprit.entities.ContratAssurance> contrats) {
        contratsCardsContainer.getChildren().clear();
        
        if (contrats.isEmpty()) {
            Label noData = new Label("No contrats found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            contratsCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (tn.esprit.entities.ContratAssurance contrat : contrats) {
            contratsCardsContainer.getChildren().add(createContratCard(contrat));
        }
    }
    
    private VBox createContratCard(tn.esprit.entities.ContratAssurance c) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2);");
        
        // Header
        HBox topRow = new HBox();
        topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        StackPane icon = new StackPane();
        icon.setPrefSize(40, 40);
        icon.setStyle("-fx-background-color: #667eea; -fx-background-radius: 20;");
        Label iconLabel = new Label("C");
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        icon.getChildren().add(iconLabel);
        
        VBox titleBox = new VBox(3);
        Label numLabel = new Label("Contract");
        numLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        Label contractNum = new Label(c.getNumeroContrat());
        contractNum.setStyle("-fx-font-size: 11; -fx-text-fill: #a0aec0;");
        titleBox.getChildren().addAll(numLabel, contractNum);
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label statusLabel = new Label(c.getStatut());
        statusLabel.setStyle("-fx-background-color: " + getContratStatusColor(c.getStatut()) + 
                           "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8; " +
                           "-fx-font-size: 9; -fx-font-weight: bold;");
        
        topRow.getChildren().addAll(icon, titleBox, spacer, statusLabel);
        
        // Details
        VBox details = new VBox(6);
        if (c.getDateSignature() != null) {
            details.getChildren().add(createInfoRow("Signed:", c.getDateSignature().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        }
        if (c.getDureeContrat() != null) {
            details.getChildren().add(createInfoRow("Duration:", c.getDureeContrat() + " months"));
        }
        
        Separator separator = new Separator();
        
        // Actions
        HBox actions = new HBox(8);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        modifyBtn.setOnAction(e -> handleModifyContrat(c));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        deleteBtn.setOnAction(e -> handleDeleteContrat(c));
        
        actions.getChildren().addAll(modifyBtn, deleteBtn);
        
        card.getChildren().addAll(topRow, details, separator, actions);
        
        return card;
    }
    
    @FXML
    private void handleAddContrat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddContratDialog.fxml"));
            VBox dialogRoot = loader.load();
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Add Contrat");
            dialogStage.initOwner(contratsCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
            loadContratsCards();
        } catch (Exception e) {
            showError("Error opening dialog: " + e.getMessage());
        }
    }
    
    private void handleModifyContrat(tn.esprit.entities.ContratAssurance c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyContratDialog.fxml"));
            VBox dialogRoot = loader.load();
            ModifyContratDialogController controller = loader.getController();
            controller.setContrat(c);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Modify Contrat");
            dialogStage.initOwner(contratsCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
            loadContratsCards();
        } catch (Exception e) {
            showError("Error opening dialog: " + e.getMessage());
        }
    }
    
    private void handleDeleteContrat(tn.esprit.entities.ContratAssurance c) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Contrat");
        confirm.setContentText("Are you sure you want to delete this contrat?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    contratAssuranceServices.supprimer(c.getId());
                    loadContratsCards();
                    showSuccess("Contrat deleted successfully!");
                } catch (SQLException e) {
                    showError("Error deleting contrat: " + e.getMessage());
                }
            }
        });
    }
    
    private String getContratStatusColor(String status) {
        if (status == null) return "#a0aec0";
        switch (status) {
            case "ACTIF": return "#48bb78";
            case "EXPIRE": return "#f56565";
            case "RESILIE": return "#e53e3e";
            case "EN_ATTENTE": return "#ed8936";
            default: return "#a0aec0";
        }
    }
    
    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(5);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-size: 11; -fx-text-fill: #a0aec0; -fx-font-weight: 600;");
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-font-size: 11; -fx-text-fill: #2d3748;");
        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    // ============== PROJETS MANAGEMENT ==============
    
    private void loadProjetsCards() {
        try {
            allProjets = projetServices.afficher();
            
            if (projetStatusFilter.getItems().isEmpty()) {
                projetStatusFilter.getItems().addAll("All Status", "EN_COURS", "TERMINE", "ANNULE");
                projetStatusFilter.setValue("All Status");
                projetStatusFilter.setOnAction(e -> filterAndDisplayProjets());
                
                projetSortComboBox.getItems().addAll("Name A-Z", "Amount: High to Low", "Date: Newest");
                projetSortComboBox.setValue("Name A-Z");
                projetSortComboBox.setOnAction(e -> filterAndDisplayProjets());
                
                projetSearchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayProjets());
            }
            
            filterAndDisplayProjets();
        } catch (SQLException e) {
            showError("Error loading projets: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filterAndDisplayProjets() {
        if (allProjets == null) return;
        
        String search = projetSearchField.getText().toLowerCase();
        String statusFilter = projetStatusFilter.getValue();
        String sortBy = projetSortComboBox.getValue();
        
        List<Projet> filtered = allProjets.stream()
            .filter(p -> search.isEmpty() || p.getNomProjet().toLowerCase().contains(search) ||
                        p.getSecteur().toLowerCase().contains(search))
            .filter(p -> "All Status".equals(statusFilter) || p.getStatutProjet().equals(statusFilter))
            .sorted((p1, p2) -> {
                if ("Amount: High to Low".equals(sortBy)) {
                    return Integer.compare(p2.getMontantObjectif(), p1.getMontantObjectif());
                } else if ("Date: Newest".equals(sortBy) && p1.getDateDebut() != null && p2.getDateDebut() != null) {
                    return p2.getDateDebut().compareTo(p1.getDateDebut());
                } else {
                    return p1.getNomProjet().compareTo(p2.getNomProjet());
                }
            })
            .collect(java.util.stream.Collectors.toList());
        
        displayProjetCards(filtered);
    }
    
    private void displayProjetCards(List<Projet> projets) {
        projetsCardsContainer.getChildren().clear();
        
        if (projets.isEmpty()) {
            Label noData = new Label("No projets found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            projetsCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (Projet projet : projets) {
            projetsCardsContainer.getChildren().add(createProjetCard(projet));
        }
    }
    
    private VBox createProjetCard(Projet projet) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2);");
        
        Label nameLabel = new Label(projet.getNomProjet());
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        nameLabel.setWrapText(true);
        
        Label secteurLabel = new Label(projet.getSecteur());
        secteurLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #a0aec0;");
        
        Label amountLabel = new Label(currencyFormat.format(projet.getMontantObjectif()));
        amountLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        Label statusLabel = new Label(projet.getStatutProjet());
        statusLabel.setStyle("-fx-background-color: " + getProjetStatusColor(projet.getStatutProjet()) + 
                           "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 10;");
        
        Separator separator = new Separator();
        
        HBox actions = new HBox(8);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        modifyBtn.setOnAction(e -> handleModifyProjet(projet));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        deleteBtn.setOnAction(e -> handleDeleteProjet(projet));
        
        actions.getChildren().addAll(modifyBtn, deleteBtn);
        
        card.getChildren().addAll(nameLabel, secteurLabel, amountLabel, statusLabel, separator, actions);
        
        return card;
    }
    
    @FXML
    private void handleAddProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddProjetDialog.fxml"));
            VBox dialogContent = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Project");
            dialogStage.setScene(new Scene(dialogContent));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            AddProjetDialogController controller = loader.getController();
            if (controller.isSuccess()) {
                loadProjetsCards();
                showSuccess("Project added successfully!");
            }
            
        } catch (Exception e) {
            showError("Error opening add projet dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleModifyProjet(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyProjetDialog.fxml"));
            VBox dialogContent = loader.load();
            
            ModifyProjetDialogController controller = loader.getController();
            controller.setProjet(projet);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modify Project");
            dialogStage.setScene(new Scene(dialogContent));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            if (controller.isSuccess()) {
                loadProjetsCards();
                showSuccess("Project updated successfully!");
            }
            
        } catch (Exception e) {
            showError("Error opening modify projet dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteProjet(Projet projet) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Projet");
        confirm.setContentText("Are you sure you want to delete this projet?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    projetServices.supprimer(projet.getIdProjet());
                    loadProjetsCards();
                    showSuccess("Projet deleted successfully!");
                } catch (SQLException e) {
                    showError("Error deleting projet: " + e.getMessage());
                }
            }
        });
    }
    
    private String getProjetStatusColor(String status) {
        if (status == null) return "#a0aec0";
        switch (status) {
            case "EN_COURS": return "#48bb78";
            case "TERMINE": return "#4299e1";
            case "ANNULE": return "#f56565";
            default: return "#a0aec0";
        }
    }
    
    // ============== INVESTISSEMENTS MANAGEMENT ==============
    
    private void loadInvestissementsCards() {
        try {
            allInvestissements = investissementServices.afficher();
            
            if (invesStatusFilter.getItems().isEmpty()) {
                invesStatusFilter.getItems().addAll("All Status", "CONFIRME", "EN_ATTENTE", "ANNULE");
                invesStatusFilter.setValue("All Status");
                invesStatusFilter.setOnAction(e -> filterAndDisplayInvestissements());
                
                invesSortComboBox.getItems().addAll("Date: Newest", "Amount: High to Low");
                invesSortComboBox.setValue("Date: Newest");
                invesSortComboBox.setOnAction(e -> filterAndDisplayInvestissements());
                
                invesSearchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayInvestissements());
            }
            
            filterAndDisplayInvestissements();
        } catch (SQLException e) {
            showError("Error loading investissements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filterAndDisplayInvestissements() {
        if (allInvestissements == null) return;
        
        String statusFilter = invesStatusFilter.getValue();
        String sortBy = invesSortComboBox.getValue();
        
        List<tn.esprit.entities.Investissement> filtered = allInvestissements.stream()
            .filter(i -> "All Status".equals(statusFilter) || i.getStatutInvestissement().equals(statusFilter))
            .sorted((i1, i2) -> {
                if ("Amount: High to Low".equals(sortBy)) {
                    return Integer.compare(i2.getMontantInvesti(), i1.getMontantInvesti());
                } else if (i1.getDateInves() != null && i2.getDateInves() != null) {
                    return i2.getDateInves().compareTo(i1.getDateInves());
                }
                return 0;
            })
            .collect(java.util.stream.Collectors.toList());
        
        displayInvestissementCards(filtered);
    }
    
    private void displayInvestissementCards(List<tn.esprit.entities.Investissement> investissements) {
        investissementsCardsContainer.getChildren().clear();
        
        if (investissements.isEmpty()) {
            Label noData = new Label("No investissements found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            investissementsCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (tn.esprit.entities.Investissement inves : investissements) {
            investissementsCardsContainer.getChildren().add(createInvestissementCard(inves));
        }
    }
    
    private VBox createInvestissementCard(tn.esprit.entities.Investissement inves) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2);");
        
        Label amountLabel = new Label(currencyFormat.format(inves.getMontantInvesti()));
        amountLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        card.getChildren().add(amountLabel);
        
        if (inves.getDateInves() != null) {
            Label dateLabel = new Label(inves.getDateInves().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #a0aec0;");
            card.getChildren().add(dateLabel);
        }
        
        Label modeLabel = new Label("Mode: " + inves.getModePaiement());
        modeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #718096;");
        
        Label statusLabel = new Label(inves.getStatutInvestissement());
        statusLabel.setStyle("-fx-background-color: " + getInvesStatusColor(inves.getStatutInvestissement()) + 
                           "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 10;");
        
        Separator separator = new Separator();
        
        HBox actions = new HBox(8);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        modifyBtn.setOnAction(e -> handleModifyInvestissement(inves));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        deleteBtn.setOnAction(e -> handleDeleteInvestissement(inves));
        
        actions.getChildren().addAll(modifyBtn, deleteBtn);
        
        card.getChildren().addAll(modeLabel, statusLabel, separator, actions);
        
        return card;
    }
    
    @FXML
    private void handleAddInvestissement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddInvestissementDialog.fxml"));
            VBox dialogContent = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Investment");
            dialogStage.setScene(new Scene(dialogContent));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            AddInvestissementDialogController controller = loader.getController();
            if (controller.isSuccess()) {
                loadInvestissementsCards();
                showSuccess("Investment added successfully!");
            }
            
        } catch (Exception e) {
            showError("Error opening add investissement dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleModifyInvestissement(tn.esprit.entities.Investissement inves) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyInvestissementDialog.fxml"));
            VBox dialogContent = loader.load();
            
            ModifyInvestissementDialogController controller = loader.getController();
            controller.setInvestissement(inves);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modify Investment");
            dialogStage.setScene(new Scene(dialogContent));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            if (controller.isSuccess()) {
                loadInvestissementsCards();
                showSuccess("Investment updated successfully!");
            }
            
        } catch (Exception e) {
            showError("Error opening modify investissement dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteInvestissement(tn.esprit.entities.Investissement inves) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Investissement");
        confirm.setContentText("Are you sure you want to delete this investissement?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    investissementServices.supprimer(inves.getIdInves());
                    loadInvestissementsCards();
                    showSuccess("Investissement deleted successfully!");
                } catch (SQLException e) {
                    showError("Error deleting investissement: " + e.getMessage());
                }
            }
        });
    }
    
    private String getInvesStatusColor(String status) {
        if (status == null) return "#a0aec0";
        switch (status) {
            case "CONFIRME": return "#48bb78";
            case "EN_ATTENTE": return "#ed8936";
            case "ANNULE": return "#f56565";
            default: return "#a0aec0";
        }
    }
    
    // ============== COMPTES MANAGEMENT ==============
    
    private void loadComptesCards() {
        try {
            allComptes = compteBancaireServices.afficher();
            
            if (compteTypeFilter.getItems().isEmpty()) {
                compteTypeFilter.getItems().addAll("All Types", "Checking", "Savings", "Business", "Investment");
                compteTypeFilter.setValue("All Types");
                compteTypeFilter.setOnAction(e -> filterAndDisplayComptes());
                
                compteSortComboBox.getItems().addAll("Account Number", "Balance: High to Low", "Date: Newest");
                compteSortComboBox.setValue("Account Number");
                compteSortComboBox.setOnAction(e -> filterAndDisplayComptes());
                
                compteSearchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayComptes());
            }
            
            filterAndDisplayComptes();
        } catch (SQLException e) {
            showError("Error loading bank accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filterAndDisplayComptes() {
        if (allComptes == null) return;
        
        String search = compteSearchField.getText().toLowerCase();
        String typeFilter = compteTypeFilter.getValue();
        String sortBy = compteSortComboBox.getValue();
        
        List<tn.esprit.entities.CompteBancaire> filtered = allComptes.stream()
            .filter(c -> search.isEmpty() || c.getNumeroCompte().toLowerCase().contains(search) ||
                        c.getTitulaire().toLowerCase().contains(search))
            .filter(c -> "All Types".equals(typeFilter) || c.getTypeCompte().equals(typeFilter))
            .sorted((c1, c2) -> {
                if ("Balance: High to Low".equals(sortBy)) {
                    return c2.getSolde().compareTo(c1.getSolde());
                } else if ("Date: Newest".equals(sortBy) && c1.getDateCreation() != null && c2.getDateCreation() != null) {
                    return c2.getDateCreation().compareTo(c1.getDateCreation());
                } else {
                    return c1.getNumeroCompte().compareTo(c2.getNumeroCompte());
                }
            })
            .collect(java.util.stream.Collectors.toList());
        
        displayCompteCards(filtered);
    }
    
    private void displayCompteCards(List<tn.esprit.entities.CompteBancaire> comptes) {
        comptesCardsContainer.getChildren().clear();
        
        if (comptes.isEmpty()) {
            Label noData = new Label("No bank accounts found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            comptesCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (tn.esprit.entities.CompteBancaire compte : comptes) {
            comptesCardsContainer.getChildren().add(createCompteCard(compte));
        }
    }
    
    private VBox createCompteCard(tn.esprit.entities.CompteBancaire compte) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2);");
        
        Label numeroLabel = new Label(compte.getNumeroCompte());
        numeroLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        
        Label titulaireLabel = new Label(compte.getTitulaire());
        titulaireLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #718096;");
        
        Label soldeLabel = new Label(currencyFormat.format(compte.getSolde()) + " " + compte.getDevise());
        soldeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        Label typeLabel = new Label(compte.getTypeCompte());
        typeLabel.setStyle("-fx-background-color: #e6fffa; -fx-text-fill: #319795; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 10;");
        
        Label statusLabel = new Label(compte.isActif() ? "Active" : "Inactive");
        statusLabel.setStyle("-fx-background-color: " + (compte.isActif() ? "#48bb78" : "#f56565") + 
                           "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 10;");
        
        HBox badges = new HBox(8);
        badges.getChildren().addAll(typeLabel, statusLabel);
        
        Separator separator = new Separator();
        
        HBox actions = new HBox(8);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        modifyBtn.setOnAction(e -> handleModifyCompte(compte));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        deleteBtn.setOnAction(e -> handleDeleteCompte(compte));
        
        actions.getChildren().addAll(modifyBtn, deleteBtn);
        
        card.getChildren().addAll(numeroLabel, titulaireLabel, soldeLabel, badges, separator, actions);
        
        return card;
    }
    
    @FXML
    private void handleAddCompte() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddCompteDialog.fxml"));
            VBox dialogContent = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Bank Account");
            dialogStage.setScene(new Scene(dialogContent));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            AddCompteDialogController controller = loader.getController();
            if (controller.isSuccess()) {
                loadComptesCards();
                showSuccess("Bank account added successfully!");
            }
            
        } catch (Exception e) {
            showError("Error opening add compte dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleModifyCompte(tn.esprit.entities.CompteBancaire compte) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyCompteDialog.fxml"));
            VBox dialogRoot = loader.load();
            ModifyCompteDialogController controller = loader.getController();
            controller.setCompte(compte);
            controller.setOnSaveCallback(this::loadComptesCards);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Modify Bank Account");
            dialogStage.initOwner(comptesCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error opening modify dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteCompte(tn.esprit.entities.CompteBancaire compte) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Bank Account");
        confirm.setContentText("Are you sure you want to delete this account?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    compteBancaireServices.supprimer(compte.getId());
                    loadComptesCards();
                    showSuccess("Bank account deleted successfully!");
                } catch (SQLException e) {
                    showError("Error deleting account: " + e.getMessage());
                }
            }
        });
    }
    
    // ============== DEPENSES MANAGEMENT ==============
    
    private void loadDepensesCards() {
        try {
            allDepenses = depenseServices.afficher();
            
            if (depenseCatFilter.getItems().isEmpty()) {
                depenseCatFilter.getItems().addAll("All Categories", "Food", "Transport", "Entertainment", "Shopping", "Bills", "Healthcare", "Education", "Other");
                depenseCatFilter.setValue("All Categories");
                depenseCatFilter.setOnAction(e -> filterAndDisplayDepenses());
                
                depenseSortComboBox.getItems().addAll("Date: Newest", "Amount: High to Low", "Category");
                depenseSortComboBox.setValue("Date: Newest");
                depenseSortComboBox.setOnAction(e -> filterAndDisplayDepenses());
                
                depenseSearchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayDepenses());
            }
            
            filterAndDisplayDepenses();
        } catch (SQLException e) {
            showError("Error loading expenses: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void filterAndDisplayDepenses() {
        if (allDepenses == null) return;
        
        String search = depenseSearchField.getText().toLowerCase();
        String catFilter = depenseCatFilter.getValue();
        String sortBy = depenseSortComboBox.getValue();
        
        List<tn.esprit.entities.Depense> filtered = allDepenses.stream()
            .filter(d -> search.isEmpty() || (d.getDescription() != null && d.getDescription().toLowerCase().contains(search)))
            .filter(d -> "All Categories".equals(catFilter) || d.getCategorie().equals(catFilter))
            .sorted((d1, d2) -> {
                if ("Amount: High to Low".equals(sortBy)) {
                    return d2.getMontant().compareTo(d1.getMontant());
                } else if ("Category".equals(sortBy)) {
                    return d1.getCategorie().compareTo(d2.getCategorie());
                } else if (d1.getDateDepense() != null && d2.getDateDepense() != null) {
                    return d2.getDateDepense().compareTo(d1.getDateDepense());
                }
                return 0;
            })
            .collect(java.util.stream.Collectors.toList());
        
        displayDepenseCards(filtered);
    }
    
    private void displayDepenseCards(List<tn.esprit.entities.Depense> depenses) {
        depensesCardsContainer.getChildren().clear();
        
        if (depenses.isEmpty()) {
            Label noData = new Label("No expenses found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            depensesCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (tn.esprit.entities.Depense depense : depenses) {
            depensesCardsContainer.getChildren().add(createDepenseCard(depense));
        }
    }
    
    private VBox createDepenseCard(tn.esprit.entities.Depense depense) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2);");
        
        Label montantLabel = new Label(currencyFormat.format(depense.getMontant()));
        montantLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #f56565;");
        
        if (depense.getDateDepense() != null) {
            Label dateLabel = new Label(depense.getDateDepense().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #a0aec0;");
            card.getChildren().add(dateLabel);
        }
        
        if (depense.getDescription() != null && !depense.getDescription().isEmpty()) {
            Label descLabel = new Label(depense.getDescription());
            descLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #718096;");
            descLabel.setWrapText(true);
            descLabel.setMaxHeight(40);
            card.getChildren().add(descLabel);
        }
        
        Label catLabel = new Label(depense.getCategorie());
        catLabel.setStyle("-fx-background-color: #fef5e7; -fx-text-fill: #f39c12; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 10;");
        
        Label modeLabel = new Label(depense.getModePaiement());
        modeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #718096;");
        
        HBox info = new HBox(8);
        info.getChildren().addAll(catLabel);
        
        Separator separator = new Separator();
        
        HBox actions = new HBox(8);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        modifyBtn.setOnAction(e -> handleModifyDepense(depense));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-size: 11;");
        deleteBtn.setOnAction(e -> handleDeleteDepense(depense));
        
        actions.getChildren().addAll(modifyBtn, deleteBtn);
        
        card.getChildren().addAll(montantLabel, info, modeLabel, separator, actions);
        
        return card;
    }
    
    @FXML
    private void handleAddDepense() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddDepenseDialog.fxml"));
            VBox dialogRoot = loader.load();
            AddDepenseDialogController controller = loader.getController();
            controller.setOnSaveCallback(this::loadDepensesCards);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Add Expense");
            dialogStage.initOwner(depensesCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error opening add dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleModifyDepense(tn.esprit.entities.Depense depense) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyDepenseDialog.fxml"));
            VBox dialogRoot = loader.load();
            ModifyDepenseDialogController controller = loader.getController();
            controller.setDepense(depense);
            controller.setOnSaveCallback(this::loadDepensesCards);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Modify Expense");
            dialogStage.initOwner(depensesCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error opening modify dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteDepense(tn.esprit.entities.Depense depense) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Expense");
        confirm.setContentText("Are you sure you want to delete this expense?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    depenseServices.supprimer(depense.getId());
                    loadDepensesCards();
                    showSuccess("Expense deleted successfully!");
                } catch (SQLException e) {
                    showError("Error deleting expense: " + e.getMessage());
                }
            }
        });
    }
    
    // ============== LOGOUT & ERRORS ==============
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) adminNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Login");
        } catch (Exception e) {
            showError("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ========================================
    // CREDITS MANAGEMENT
    // ========================================
    
    private void loadCreditsCards() {
        try {
            allCredits = creditServices.afficher();
            setupCreditsFilters();
            filterAndDisplayCredits();
        } catch (SQLException e) {
            showError("Error loading credits: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupCreditsFilters() {
        if (creditStatusFilter.getItems().isEmpty()) {
            creditStatusFilter.getItems().addAll("All Status", "EN_ATTENTE", "ACCEPTE", "REFUSE", "EN_COURS", "TERMINE", "EN_RETARD");
            creditStatusFilter.setValue("All Status");
            creditStatusFilter.setOnAction(e -> filterAndDisplayCredits());
            
            creditSortComboBox.getItems().addAll("Date (Newest)", "Date (Oldest)", "Amount (High)", "Amount (Low)", "Status");
            creditSortComboBox.setValue("Date (Newest)");
            creditSortComboBox.setOnAction(e -> filterAndDisplayCredits());
            
            creditSearchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayCredits());
        }
    }
    
    private void filterAndDisplayCredits() {
        if (allCredits == null) return;
        
        String search = creditSearchField.getText().toLowerCase();
        String status = creditStatusFilter.getValue();
        String sort = creditSortComboBox.getValue();
        
        List<tn.esprit.entities.Credit> filtered = allCredits.stream()
            .filter(c -> "All Status".equals(status) || c.getStatutCredit().equals(status))
            .filter(c -> search.isEmpty() || 
                    c.getMontantDemande().toString().contains(search) ||
                    c.getTypeCredit().toLowerCase().contains(search))
            .collect(Collectors.toList());
        
        // Sort
        if ("Date (Newest)".equals(sort)) {
            filtered.sort((a, b) -> b.getDateDemande().compareTo(a.getDateDemande()));
        } else if ("Date (Oldest)".equals(sort)) {
            filtered.sort((a, b) -> a.getDateDemande().compareTo(b.getDateDemande()));
        } else if ("Amount (High)".equals(sort)) {
            filtered.sort((a, b) -> b.getMontantDemande().compareTo(a.getMontantDemande()));
        } else if ("Amount (Low)".equals(sort)) {
            filtered.sort((a, b) -> a.getMontantDemande().compareTo(b.getMontantDemande()));
        } else if ("Status".equals(sort)) {
            filtered.sort((a, b) -> a.getStatutCredit().compareTo(b.getStatutCredit()));
        }
        
        displayCreditCards(filtered);
    }
    
    private void displayCreditCards(List<tn.esprit.entities.Credit> credits) {
        creditsCardsContainer.getChildren().clear();
        
        if (credits.isEmpty()) {
            Label noData = new Label("No credits found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            creditsCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (tn.esprit.entities.Credit credit : credits) {
            creditsCardsContainer.getChildren().add(createCreditCard(credit));
        }
    }
    
    private VBox createCreditCard(tn.esprit.entities.Credit credit) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2);");
        
        // Header
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label amountLabel = new Label(currencyFormat.format(credit.getMontantDemande()));
        amountLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(credit.getStatutCredit());
        statusLabel.setStyle("-fx-background-color: " + getCreditStatusColor(credit.getStatutCredit()) + 
                           "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 10; -fx-font-weight: 600;");
        
        header.getChildren().addAll(amountLabel, spacer, statusLabel);
        
        // Type and Duration
        Label typeLabel = new Label("Type: " + credit.getTypeCredit() + " | Rate: " + credit.getTauxInteret() + "%");
        typeLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #718096; -fx-font-weight: 600;");
        
        // Total to pay
        Label totalLabel = new Label("Total to Pay: " + currencyFormat.format(credit.getMontantTotal()));
        totalLabel.setStyle("-fx-font-size: 14; -fx-font-weight: 600; -fx-text-fill: #2d3748;");
        
        // Remaining
        Label remainingLabel = new Label("Remaining: " + currencyFormat.format(credit.getMontantRestant()));
        remainingLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #f56565; -fx-font-weight: 600;");
        
        // Date
        Label dateLabel = new Label("Requested: " + credit.getDateDemande().toString());
        dateLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #a0aec0;");
        
        Separator separator = new Separator();
        
        // Actions
        HBox actions = new HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-weight: 600;");
        modifyBtn.setOnAction(e -> handleModifyCredit(credit));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-weight: 600;");
        deleteBtn.setOnAction(e -> handleDeleteCredit(credit));
        
        actions.getChildren().addAll(modifyBtn, deleteBtn);
        
        card.getChildren().addAll(header, typeLabel, totalLabel, remainingLabel, dateLabel, separator, actions);
        
        return card;
    }
    
    private String getCreditStatusColor(String status) {
        if (status == null) return "#a0aec0";
        switch (status) {
            case "EN_ATTENTE": return "#ed8936";
            case "ACCEPTE": return "#48bb78";
            case "REFUSE": return "#f56565";
            case "EN_COURS": return "#4299e1";
            case "TERMINE": return "#9f7aea";
            case "EN_RETARD": return "#fc8181";
            default: return "#a0aec0";
        }
    }
    
    @FXML
    private void handleAddCredit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddCreditDialog.fxml"));
            VBox dialogRoot = loader.load();
            AddCreditDialogController controller = loader.getController();
            controller.setOnSaveCallback(this::loadCreditsCards);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Add Credit");
            dialogStage.initOwner(creditsCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error opening add dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleModifyCredit(tn.esprit.entities.Credit credit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyCreditDialog.fxml"));
            VBox dialogRoot = loader.load();
            ModifyCreditDialogController controller = loader.getController();
            controller.setCredit(credit);
            controller.setOnSaveCallback(this::loadCreditsCards);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Modify Credit");
            dialogStage.initOwner(creditsCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error opening modify dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteCredit(tn.esprit.entities.Credit credit) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Credit");
        confirm.setContentText("Are you sure you want to delete this credit?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    creditServices.supprimer(credit.getIdCredit());
                    loadCreditsCards();
                    showSuccess("Credit deleted successfully!");
                } catch (SQLException e) {
                    showError("Error deleting credit: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    // ========================================
    // REMBOURSEMENTS MANAGEMENT
    // ========================================
    
    private void loadRemboursementsCards() {
        try {
            allRemboursements = remboursementServices.afficher();
            setupRemboursementsFilters();
            filterAndDisplayRemboursements();
        } catch (SQLException e) {
            showError("Error loading remboursements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupRemboursementsFilters() {
        if (remboursementTypeFilter.getItems().isEmpty()) {
            remboursementTypeFilter.getItems().addAll("All Types", "CREDIT_PAYMENT", "ABONNEMENT_REFUND", "ASSURANCE_REFUND");
            remboursementTypeFilter.setValue("All Types");
            remboursementTypeFilter.setOnAction(e -> filterAndDisplayRemboursements());
            
            remboursementSortComboBox.getItems().addAll("Date (Newest)", "Date (Oldest)", "Amount (High)", "Amount (Low)");
            remboursementSortComboBox.setValue("Date (Newest)");
            remboursementSortComboBox.setOnAction(e -> filterAndDisplayRemboursements());
            
            remboursementSearchField.textProperty().addListener((obs, old, newVal) -> filterAndDisplayRemboursements());
        }
    }
    
    private void filterAndDisplayRemboursements() {
        if (allRemboursements == null) return;
        
        String search = remboursementSearchField.getText().toLowerCase();
        String type = remboursementTypeFilter.getValue();
        String sort = remboursementSortComboBox.getValue();
        
        List<tn.esprit.entities.Remboursement> filtered = allRemboursements.stream()
            .filter(r -> "All Types".equals(type) || r.getTypeRemboursement().equals(type))
            .filter(r -> search.isEmpty() || 
                    r.getDescription().toLowerCase().contains(search) ||
                    r.getMontant().toString().contains(search))
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
        
        displayRemboursementCards(filtered);
    }
    
    private void displayRemboursementCards(List<tn.esprit.entities.Remboursement> remboursements) {
        remboursementsCardsContainer.getChildren().clear();
        
        if (remboursements.isEmpty()) {
            Label noData = new Label("No remboursements found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            remboursementsCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (tn.esprit.entities.Remboursement remb : remboursements) {
            remboursementsCardsContainer.getChildren().add(createRemboursementCard(remb));
        }
    }
    
    private VBox createRemboursementCard(tn.esprit.entities.Remboursement remb) {
        VBox card = new VBox(15);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 2);");
        
        // Header
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label amountLabel = new Label(currencyFormat.format(remb.getMontant()));
        amountLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: " + 
                           (remb.getTypeRemboursement().contains("REFUND") ? "#48bb78" : "#667eea") + ";");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(remb.getStatut());
        statusLabel.setStyle("-fx-background-color: " + getRemboursementStatusColor(remb.getStatut()) + 
                           "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 10; -fx-font-weight: 600;");
        
        header.getChildren().addAll(amountLabel, spacer, statusLabel);
        
        // Type
        Label typeLabel = new Label(formatRemboursementType(remb.getTypeRemboursement()));
        typeLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #718096; -fx-font-weight: 600;");
        
        // Description
        Label descLabel = new Label(remb.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #4a5568;");
        descLabel.setMaxHeight(40);
        
        // Date
        Label dateLabel = new Label("Date: " + remb.getDateRemboursement().toString());
        dateLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #a0aec0;");
        
        Separator separator = new Separator();
        
        // Actions
        HBox actions = new HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button modifyBtn = new Button("Modify");
        modifyBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-weight: 600;");
        modifyBtn.setOnAction(e -> handleModifyRemboursement(remb));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                          "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-weight: 600;");
        deleteBtn.setOnAction(e -> handleDeleteRemboursement(remb));
        
        actions.getChildren().addAll(modifyBtn, deleteBtn);
        
        card.getChildren().addAll(header, typeLabel, descLabel, dateLabel, separator, actions);
        
        return card;
    }
    
    private String getRemboursementStatusColor(String status) {
        if (status == null) return "#a0aec0";
        switch (status) {
            case "EN_ATTENTE": return "#ed8936";
            case "COMPLETE": return "#48bb78";
            case "ECHOUE": return "#f56565";
            default: return "#a0aec0";
        }
    }
    
    private String formatRemboursementType(String type) {
        if (type == null) return "";
        return type.replace("_", " ");
    }
    
    @FXML
    private void handleAddRemboursement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddRemboursementDialog.fxml"));
            VBox dialogRoot = loader.load();
            AddRemboursementDialogController controller = loader.getController();
            controller.setOnSaveCallback(this::loadRemboursementsCards);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Add Remboursement");
            dialogStage.initOwner(remboursementsCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error opening add dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleModifyRemboursement(tn.esprit.entities.Remboursement remb) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyRemboursementDialog.fxml"));
            VBox dialogRoot = loader.load();
            ModifyRemboursementDialogController controller = loader.getController();
            controller.setRemboursement(remb);
            controller.setOnSaveCallback(this::loadRemboursementsCards);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Credix - Modify Remboursement");
            dialogStage.initOwner(remboursementsCardsContainer.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot));
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showError("Error opening modify dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteRemboursement(tn.esprit.entities.Remboursement remb) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Remboursement");
        confirm.setContentText("Are you sure you want to delete this remboursement?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    remboursementServices.supprimer(remb.getIdRemboursement());
                    loadRemboursementsCards();
                    showSuccess("Remboursement deleted successfully!");
                } catch (SQLException e) {
                    showError("Error deleting remboursement: " + e.getMessage());
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
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
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
