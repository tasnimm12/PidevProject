package tn.esprit.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Credit {
    private int idCredit;
    private int userId;
    private long compteId;
    private BigDecimal montantDemande;
    private String typeCredit; // 3M, 6M, 12M, 24M, 36M
    private BigDecimal tauxInteret;
    private BigDecimal montantTotal;
    private BigDecimal montantRestant;
    private LocalDate dateDemande;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statutCredit; // EN_ATTENTE, ACCEPTE, REFUSE, EN_COURS, TERMINE, EN_RETARD
    private String motifRefus;
    private BigDecimal mensualite;
    
    // For joined queries
    private User user;
    private CompteBancaire compte;
    
    public Credit() {
    }
    
    public Credit(int userId, long compteId, BigDecimal montantDemande, String typeCredit, 
                 BigDecimal tauxInteret, BigDecimal montantTotal, BigDecimal montantRestant, 
                 LocalDate dateDemande, LocalDate dateDebut, LocalDate dateFin, 
                 String statutCredit, BigDecimal mensualite) {
        this.userId = userId;
        this.compteId = compteId;
        this.montantDemande = montantDemande;
        this.typeCredit = typeCredit;
        this.tauxInteret = tauxInteret;
        this.montantTotal = montantTotal;
        this.montantRestant = montantRestant;
        this.dateDemande = dateDemande;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statutCredit = statutCredit;
        this.mensualite = mensualite;
    }
    
    public Credit(int idCredit, int userId, long compteId, BigDecimal montantDemande, 
                 String typeCredit, BigDecimal tauxInteret, BigDecimal montantTotal, 
                 BigDecimal montantRestant, LocalDate dateDemande, LocalDate dateDebut, 
                 LocalDate dateFin, String statutCredit, String motifRefus, BigDecimal mensualite) {
        this.idCredit = idCredit;
        this.userId = userId;
        this.compteId = compteId;
        this.montantDemande = montantDemande;
        this.typeCredit = typeCredit;
        this.tauxInteret = tauxInteret;
        this.montantTotal = montantTotal;
        this.montantRestant = montantRestant;
        this.dateDemande = dateDemande;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statutCredit = statutCredit;
        this.motifRefus = motifRefus;
        this.mensualite = mensualite;
    }
    
    // Getters and Setters
    public int getIdCredit() {
        return idCredit;
    }
    
    public void setIdCredit(int idCredit) {
        this.idCredit = idCredit;
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
    
    public BigDecimal getMontantDemande() {
        return montantDemande;
    }
    
    public void setMontantDemande(BigDecimal montantDemande) {
        this.montantDemande = montantDemande;
    }
    
    public String getTypeCredit() {
        return typeCredit;
    }
    
    public void setTypeCredit(String typeCredit) {
        this.typeCredit = typeCredit;
    }
    
    public BigDecimal getTauxInteret() {
        return tauxInteret;
    }
    
    public void setTauxInteret(BigDecimal tauxInteret) {
        this.tauxInteret = tauxInteret;
    }
    
    public BigDecimal getMontantTotal() {
        return montantTotal;
    }
    
    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }
    
    public BigDecimal getMontantRestant() {
        return montantRestant;
    }
    
    public void setMontantRestant(BigDecimal montantRestant) {
        this.montantRestant = montantRestant;
    }
    
    public LocalDate getDateDemande() {
        return dateDemande;
    }
    
    public void setDateDemande(LocalDate dateDemande) {
        this.dateDemande = dateDemande;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public String getStatutCredit() {
        return statutCredit;
    }
    
    public void setStatutCredit(String statutCredit) {
        this.statutCredit = statutCredit;
    }
    
    public String getMotifRefus() {
        return motifRefus;
    }
    
    public void setMotifRefus(String motifRefus) {
        this.motifRefus = motifRefus;
    }
    
    public BigDecimal getMensualite() {
        return mensualite;
    }
    
    public void setMensualite(BigDecimal mensualite) {
        this.mensualite = mensualite;
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
        return "Credit{" +
                "idCredit=" + idCredit +
                ", userId=" + userId +
                ", compteId=" + compteId +
                ", montantDemande=" + montantDemande +
                ", typeCredit='" + typeCredit + '\'' +
                ", statutCredit='" + statutCredit + '\'' +
                '}';
    }
}
