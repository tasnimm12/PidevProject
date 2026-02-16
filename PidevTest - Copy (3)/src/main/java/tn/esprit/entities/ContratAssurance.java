package tn.esprit.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContratAssurance {
    private int id;
    private int assuranceId;
    private int utilisateurId;
    private String numeroContrat;
    private LocalDate dateSignature;
    private LocalDate dateFinContrat;
    private Integer dureeContrat; // in months
    private String conditionsParticulieres;
    private String exclusions;
    private BigDecimal plafondAnnuel;
    private BigDecimal tauxRemboursement;
    private Integer delaiCarence; // in days
    private String clauseBeneficiaire;
    private String documentContrat;
    private String amendements; // JSON stored as String
    private String conseillerAttribue;
    private String contacts;
    private String statut; // ACTIF, EXPIRE, RESILIE, EN_ATTENTE
    
    // For joined queries
    private Assurance assurance;
    
    // Constructors
    public ContratAssurance() {
    }
    
    public ContratAssurance(int id, int assuranceId, int utilisateurId, String numeroContrat, LocalDate dateSignature, 
                           LocalDate dateFinContrat, Integer dureeContrat, String conditionsParticulieres,
                           String exclusions, BigDecimal plafondAnnuel, BigDecimal tauxRemboursement,
                           Integer delaiCarence, String clauseBeneficiaire, String documentContrat,
                           String amendements, String conseillerAttribue, String contacts, String statut) {
        this.id = id;
        this.assuranceId = assuranceId;
        this.utilisateurId = utilisateurId;
        this.numeroContrat = numeroContrat;
        this.dateSignature = dateSignature;
        this.dateFinContrat = dateFinContrat;
        this.dureeContrat = dureeContrat;
        this.conditionsParticulieres = conditionsParticulieres;
        this.exclusions = exclusions;
        this.plafondAnnuel = plafondAnnuel;
        this.tauxRemboursement = tauxRemboursement;
        this.delaiCarence = delaiCarence;
        this.clauseBeneficiaire = clauseBeneficiaire;
        this.documentContrat = documentContrat;
        this.amendements = amendements;
        this.conseillerAttribue = conseillerAttribue;
        this.contacts = contacts;
        this.statut = statut;
    }
    
    // Constructor without ID
    public ContratAssurance(int assuranceId, int utilisateurId, String numeroContrat, LocalDate dateSignature,
                          LocalDate dateFinContrat, Integer dureeContrat, String conditionsParticulieres,
                          String exclusions, BigDecimal plafondAnnuel, BigDecimal tauxRemboursement,
                          Integer delaiCarence, String clauseBeneficiaire, String documentContrat,
                          String amendements, String conseillerAttribue, String contacts, String statut) {
        this.assuranceId = assuranceId;
        this.utilisateurId = utilisateurId;
        this.numeroContrat = numeroContrat;
        this.dateSignature = dateSignature;
        this.dateFinContrat = dateFinContrat;
        this.dureeContrat = dureeContrat;
        this.conditionsParticulieres = conditionsParticulieres;
        this.exclusions = exclusions;
        this.plafondAnnuel = plafondAnnuel;
        this.tauxRemboursement = tauxRemboursement;
        this.delaiCarence = delaiCarence;
        this.clauseBeneficiaire = clauseBeneficiaire;
        this.documentContrat = documentContrat;
        this.amendements = amendements;
        this.conseillerAttribue = conseillerAttribue;
        this.contacts = contacts;
        this.statut = statut;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getAssuranceId() {
        return assuranceId;
    }
    
    public void setAssuranceId(int assuranceId) {
        this.assuranceId = assuranceId;
    }
    
    public int getUtilisateurId() {
        return utilisateurId;
    }
    
    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
    
    public String getNumeroContrat() {
        return numeroContrat;
    }
    
    public void setNumeroContrat(String numeroContrat) {
        this.numeroContrat = numeroContrat;
    }
    
    public LocalDate getDateSignature() {
        return dateSignature;
    }
    
    public void setDateSignature(LocalDate dateSignature) {
        this.dateSignature = dateSignature;
    }
    
    public LocalDate getDateFinContrat() {
        return dateFinContrat;
    }
    
    public void setDateFinContrat(LocalDate dateFinContrat) {
        this.dateFinContrat = dateFinContrat;
    }
    
    public Integer getDureeContrat() {
        return dureeContrat;
    }
    
    public void setDureeContrat(Integer dureeContrat) {
        this.dureeContrat = dureeContrat;
    }
    
    public String getConditionsParticulieres() {
        return conditionsParticulieres;
    }
    
    public void setConditionsParticulieres(String conditionsParticulieres) {
        this.conditionsParticulieres = conditionsParticulieres;
    }
    
    public String getExclusions() {
        return exclusions;
    }
    
    public void setExclusions(String exclusions) {
        this.exclusions = exclusions;
    }
    
    public BigDecimal getPlafondAnnuel() {
        return plafondAnnuel;
    }
    
    public void setPlafondAnnuel(BigDecimal plafondAnnuel) {
        this.plafondAnnuel = plafondAnnuel;
    }
    
    public BigDecimal getTauxRemboursement() {
        return tauxRemboursement;
    }
    
    public void setTauxRemboursement(BigDecimal tauxRemboursement) {
        this.tauxRemboursement = tauxRemboursement;
    }
    
    public Integer getDelaiCarence() {
        return delaiCarence;
    }
    
    public void setDelaiCarence(Integer delaiCarence) {
        this.delaiCarence = delaiCarence;
    }
    
    public String getClauseBeneficiaire() {
        return clauseBeneficiaire;
    }
    
    public void setClauseBeneficiaire(String clauseBeneficiaire) {
        this.clauseBeneficiaire = clauseBeneficiaire;
    }
    
    public String getDocumentContrat() {
        return documentContrat;
    }
    
    public void setDocumentContrat(String documentContrat) {
        this.documentContrat = documentContrat;
    }
    
    public String getAmendements() {
        return amendements;
    }
    
    public void setAmendements(String amendements) {
        this.amendements = amendements;
    }
    
    public String getConseillerAttribue() {
        return conseillerAttribue;
    }
    
    public void setConseillerAttribue(String conseillerAttribue) {
        this.conseillerAttribue = conseillerAttribue;
    }
    
    public String getContacts() {
        return contacts;
    }
    
    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public Assurance getAssurance() {
        return assurance;
    }
    
    public void setAssurance(Assurance assurance) {
        this.assurance = assurance;
    }
    
    @Override
    public String toString() {
        return "ContratAssurance{" +
                "id=" + id +
                ", assuranceId=" + assuranceId +
                ", numeroContrat='" + numeroContrat + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
