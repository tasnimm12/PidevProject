package tn.esprit.entities;

import java.time.LocalDate;

public class Investissement {
    private int idInves;
    private int montantInvesti;
    private LocalDate dateInves;
    private String modePaiement;
    private String statutInvestissement;
    private int idProjet;
    
    // For joined queries
    private Projet projet;
    
    public Investissement() {
    }
    
    public Investissement(int idInves, int montantInvesti, LocalDate dateInves, String modePaiement, 
                         String statutInvestissement, int idProjet) {
        this.idInves = idInves;
        this.montantInvesti = montantInvesti;
        this.dateInves = dateInves;
        this.modePaiement = modePaiement;
        this.statutInvestissement = statutInvestissement;
        this.idProjet = idProjet;
    }
    
    public Investissement(int montantInvesti, LocalDate dateInves, String modePaiement, 
                         String statutInvestissement, int idProjet) {
        this.montantInvesti = montantInvesti;
        this.dateInves = dateInves;
        this.modePaiement = modePaiement;
        this.statutInvestissement = statutInvestissement;
        this.idProjet = idProjet;
    }
    
    // Getters and Setters
    public int getIdInves() {
        return idInves;
    }
    
    public void setIdInves(int idInves) {
        this.idInves = idInves;
    }
    
    public int getMontantInvesti() {
        return montantInvesti;
    }
    
    public void setMontantInvesti(int montantInvesti) {
        this.montantInvesti = montantInvesti;
    }
    
    public LocalDate getDateInves() {
        return dateInves;
    }
    
    public void setDateInves(LocalDate dateInves) {
        this.dateInves = dateInves;
    }
    
    public String getModePaiement() {
        return modePaiement;
    }
    
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
    
    public String getStatutInvestissement() {
        return statutInvestissement;
    }
    
    public void setStatutInvestissement(String statutInvestissement) {
        this.statutInvestissement = statutInvestissement;
    }
    
    public int getIdProjet() {
        return idProjet;
    }
    
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }
    
    public Projet getProjet() {
        return projet;
    }
    
    public void setProjet(Projet projet) {
        this.projet = projet;
    }
    
    @Override
    public String toString() {
        return "Investissement{" +
                "idInves=" + idInves +
                ", montantInvesti=" + montantInvesti +
                ", dateInves=" + dateInves +
                ", modePaiement='" + modePaiement + '\'' +
                ", statutInvestissement='" + statutInvestissement + '\'' +
                ", idProjet=" + idProjet +
                '}';
    }
}
