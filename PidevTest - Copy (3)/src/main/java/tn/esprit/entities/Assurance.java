package tn.esprit.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Assurance {
    private int id;
    private int utilisateurId;
    private String typeAssurance; // VIE, SANTE, AUTO, HABITATION, RESPONSABILITE_CIVILE, SCOLAIRE, VOYAGE, PROFESSIONNELLE
    private String compagnie;
    private String numeroPolice;
    private BigDecimal montantCouverture;
    private BigDecimal franchise;
    private BigDecimal primeAnnuelle;
    private BigDecimal primeMensuelle;
    private LocalDate dateDebut;
    private LocalDate dateEcheance;
    private String modePaiement; // MENSUEL, TRIMESTRIEL, SEMESTRIEL, ANNUEL
    private String statut; // ACTIF, EXPIRE, RESILIE, SUSPENDU
    private boolean renouvellementAuto;
    private String garantiesIncluses; // JSON stored as String
    
    // For joined queries
    private User user;
    
    // Constructors
    public Assurance() {
    }
    
    public Assurance(int id, int utilisateurId, String typeAssurance, String compagnie, String numeroPolice,
                    BigDecimal montantCouverture, BigDecimal franchise, BigDecimal primeAnnuelle, 
                    BigDecimal primeMensuelle, LocalDate dateDebut, LocalDate dateEcheance, 
                    String modePaiement, String statut, boolean renouvellementAuto, String garantiesIncluses) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.typeAssurance = typeAssurance;
        this.compagnie = compagnie;
        this.numeroPolice = numeroPolice;
        this.montantCouverture = montantCouverture;
        this.franchise = franchise;
        this.primeAnnuelle = primeAnnuelle;
        this.primeMensuelle = primeMensuelle;
        this.dateDebut = dateDebut;
        this.dateEcheance = dateEcheance;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.renouvellementAuto = renouvellementAuto;
        this.garantiesIncluses = garantiesIncluses;
    }
    
    // Constructor without ID (for adding new records)
    public Assurance(int utilisateurId, String typeAssurance, String compagnie, String numeroPolice,
                    BigDecimal montantCouverture, BigDecimal franchise, BigDecimal primeAnnuelle, 
                    BigDecimal primeMensuelle, LocalDate dateDebut, LocalDate dateEcheance, 
                    String modePaiement, String statut, boolean renouvellementAuto, String garantiesIncluses) {
        this.utilisateurId = utilisateurId;
        this.typeAssurance = typeAssurance;
        this.compagnie = compagnie;
        this.numeroPolice = numeroPolice;
        this.montantCouverture = montantCouverture;
        this.franchise = franchise;
        this.primeAnnuelle = primeAnnuelle;
        this.primeMensuelle = primeMensuelle;
        this.dateDebut = dateDebut;
        this.dateEcheance = dateEcheance;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.renouvellementAuto = renouvellementAuto;
        this.garantiesIncluses = garantiesIncluses;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUtilisateurId() {
        return utilisateurId;
    }
    
    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
    
    public String getTypeAssurance() {
        return typeAssurance;
    }
    
    public void setTypeAssurance(String typeAssurance) {
        this.typeAssurance = typeAssurance;
    }
    
    public String getCompagnie() {
        return compagnie;
    }
    
    public void setCompagnie(String compagnie) {
        this.compagnie = compagnie;
    }
    
    public String getNumeroPolice() {
        return numeroPolice;
    }
    
    public void setNumeroPolice(String numeroPolice) {
        this.numeroPolice = numeroPolice;
    }
    
    public BigDecimal getMontantCouverture() {
        return montantCouverture;
    }
    
    public void setMontantCouverture(BigDecimal montantCouverture) {
        this.montantCouverture = montantCouverture;
    }
    
    public BigDecimal getFranchise() {
        return franchise;
    }
    
    public void setFranchise(BigDecimal franchise) {
        this.franchise = franchise;
    }
    
    public BigDecimal getPrimeAnnuelle() {
        return primeAnnuelle;
    }
    
    public void setPrimeAnnuelle(BigDecimal primeAnnuelle) {
        this.primeAnnuelle = primeAnnuelle;
    }
    
    public BigDecimal getPrimeMensuelle() {
        return primeMensuelle;
    }
    
    public void setPrimeMensuelle(BigDecimal primeMensuelle) {
        this.primeMensuelle = primeMensuelle;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateEcheance() {
        return dateEcheance;
    }
    
    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }
    
    public String getModePaiement() {
        return modePaiement;
    }
    
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public boolean isRenouvellementAuto() {
        return renouvellementAuto;
    }
    
    public void setRenouvellementAuto(boolean renouvellementAuto) {
        this.renouvellementAuto = renouvellementAuto;
    }
    
    public String getGarantiesIncluses() {
        return garantiesIncluses;
    }
    
    public void setGarantiesIncluses(String garantiesIncluses) {
        this.garantiesIncluses = garantiesIncluses;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "Assurance{" +
                "id=" + id +
                ", utilisateurId=" + utilisateurId +
                ", typeAssurance='" + typeAssurance + '\'' +
                ", compagnie='" + compagnie + '\'' +
                ", numeroPolice='" + numeroPolice + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
