package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.Investissement;
import tn.esprit.entities.Projet;
import tn.esprit.entities.User;
import tn.esprit.services.InvestissementServices;
import tn.esprit.services.ProjetServices;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class OrganisateurDashboardController implements Initializable {
    
    @FXML private Button projetsBtn;
    @FXML private Button investissementsBtn;
    @FXML private Label orgNameLabel;
    
    @FXML private VBox projetsView;
    @FXML private VBox investissementsView;
    
    @FXML private TextField projetSearchField;
    @FXML private ComboBox<String> projetStatusFilter;
    @FXML private ComboBox<String> projetSortComboBox;
    @FXML private FlowPane projetsCardsContainer;
    
    @FXML private TextField invesSearchField;
    @FXML private ComboBox<String> invesStatusFilter;
    @FXML private ComboBox<String> invesSortComboBox;
    @FXML private FlowPane investissementsCardsContainer;
    
    private User currentUser;
    private ProjetServices projetServices;
    private InvestissementServices investissementServices;
    private NumberFormat currencyFormat;
    
    private List<Projet> allProjets;
    private List<Investissement> allInvestissements;
    
    public OrganisateurDashboardController() {
        projetServices = new ProjetServices();
        investissementServices = new InvestissementServices();
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProjetsCards();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            orgNameLabel.setText(user.getPrenom() + " " + user.getNom());
            loadProjetsCards();
        }
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
    
    private void switchView(VBox viewToShow) {
        projetsView.setVisible(false);
        investissementsView.setVisible(false);
        viewToShow.setVisible(true);
    }
    
    private void setActiveButton(Button activeBtn) {
        projetsBtn.getStyleClass().remove("nav-button-active");
        investissementsBtn.getStyleClass().remove("nav-button-active");
        
        if (!activeBtn.getStyleClass().contains("nav-button-active")) {
            activeBtn.getStyleClass().add("nav-button-active");
        }
    }
    
    // ============== PROJETS ==============
    
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
        card.setAlignment(Pos.TOP_LEFT);
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
        actions.setAlignment(Pos.CENTER);
        
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
    
    // ============== INVESTISSEMENTS ==============
    
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
        }
    }
    
    private void filterAndDisplayInvestissements() {
        if (allInvestissements == null) return;
        
        String statusFilter = invesStatusFilter.getValue();
        String sortBy = invesSortComboBox.getValue();
        
        List<Investissement> filtered = allInvestissements.stream()
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
    
    private void displayInvestissementCards(List<Investissement> investissements) {
        investissementsCardsContainer.getChildren().clear();
        
        if (investissements.isEmpty()) {
            Label noData = new Label("No investissements found");
            noData.setStyle("-fx-font-size: 16; -fx-text-fill: #a0aec0; -fx-padding: 50;");
            investissementsCardsContainer.getChildren().add(noData);
            return;
        }
        
        for (Investissement inves : investissements) {
            investissementsCardsContainer.getChildren().add(createInvestissementCard(inves));
        }
    }
    
    private VBox createInvestissementCard(Investissement inves) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_LEFT);
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
        actions.setAlignment(Pos.CENTER);
        
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
    
    private void handleModifyInvestissement(Investissement inves) {
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
    
    private void handleDeleteInvestissement(Investissement inves) {
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
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) orgNameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Credix - Login");
        } catch (Exception e) {
            showError("Error during logout: " + e.getMessage());
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
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
