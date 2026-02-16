package tn.esprit.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Remboursement {
    private int idRemboursement;
    private Integer creditId; // Nullable for refunds
    private int userId;
    private long compteId;
    private String typeRemboursement; // CREDIT_PAYMENT, ABONNEMENT_REFUND, ASSURANCE_REFUND
    private BigDecimal montant;
    private LocalDate dateRemboursement;
    private String statut; // EN_ATTENTE, COMPLETE, ECHOUE
    private String description;
    private Integer referenceId; // Reference to cancelled subscription/insurance
    
    // For joined queries
    private Credit credit;
    private User user;
    private CompteBancaire compte;
    
    public Remboursement() {
    }
    
    public Remboursement(Integer creditId, int userId, long compteId, String typeRemboursement, 
                        BigDecimal montant, LocalDate dateRemboursement, String statut, String description) {
        this.creditId = creditId;
        this.userId = userId;
        this.compteId = compteId;
        this.typeRemboursement = typeRemboursement;
        this.montant = montant;
        this.dateRemboursement = dateRemboursement;
        this.statut = statut;
        this.description = description;
    }
    
    public Remboursement(Integer creditId, int userId, long compteId, String typeRemboursement, 
                        BigDecimal montant, LocalDate dateRemboursement, String statut, String description, 
                        Integer referenceId) {
        this.creditId = creditId;
        this.userId = userId;
        this.compteId = compteId;
        this.typeRemboursement = typeRemboursement;
        this.montant = montant;
        this.dateRemboursement = dateRemboursement;
        this.statut = statut;
        this.description = description;
        this.referenceId = referenceId;
    }
    
    public Remboursement(int idRemboursement, Integer creditId, int userId, long compteId, 
                        String typeRemboursement, BigDecimal montant, LocalDate dateRemboursement, 
                        String statut, String description, Integer referenceId) {
        this.idRemboursement = idRemboursement;
        this.creditId = creditId;
        this.userId = userId;
        this.compteId = compteId;
        this.typeRemboursement = typeRemboursement;
        this.montant = montant;
        this.dateRemboursement = dateRemboursement;
        this.statut = statut;
        this.description = description;
        this.referenceId = referenceId;
    }
    
    // Getters and Setters
    public int getIdRemboursement() {
        return idRemboursement;
    }
    
    public void setIdRemboursement(int idRemboursement) {
        this.idRemboursement = idRemboursement;
    }
    
    public Integer getCreditId() {
        return creditId;
    }
    
    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public long getCompteId() {
        return compteId;
    }
    
    public void setCompteId(long compteId) {
        this.compteId = compteId;
    }
    
    public String getTypeRemboursement() {
        return typeRemboursement;
    }
    
    public void setTypeRemboursement(String typeRemboursement) {
        this.typeRemboursement = typeRemboursement;
    }
    
    public BigDecimal getMontant() {
        return montant;
    }
    
    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    
    public LocalDate getDateRemboursement() {
        return dateRemboursement;
    }
    
    public void setDateRemboursement(LocalDate dateRemboursement) {
        this.dateRemboursement = dateRemboursement;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }
    
    public Credit getCredit() {
        return credit;
    }
    
    public void setCredit(Credit credit) {
        this.credit = credit;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public CompteBancaire getCompte() {
        return compte;
    }
    
    public void setCompte(CompteBancaire compte) {
        this.compte = compte;
    }
    
    @Override
    public String toString() {
        return "Remboursement{" +
                "idRemboursement=" + idRemboursement +
                ", creditId=" + creditId +
                ", typeRemboursement='" + typeRemboursement + '\'' +
                ", montant=" + montant +
                ", statut='" + statut + '\'' +
                '}';
    }
}
