package tn.esprit.entities;

import java.time.LocalDate;

public class UserAbonnement {
    private int idUserAbonnement;
    private int idUser;
    private int idAbonnement;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statut; // actif, expire, annule
    private boolean renouvellementAuto;
    private LocalDate dateSouscription;
    
    // For joined queries
    private Abonnement abonnement;
    private User user;
    
    public UserAbonnement() {
    }
    
    public UserAbonnement(int idUser, int idAbonnement, LocalDate dateDebut, LocalDate dateFin, String statut, boolean renouvellementAuto) {
        this.idUser = idUser;
        this.idAbonnement = idAbonnement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.renouvellementAuto = renouvellementAuto;
    }
    
    public UserAbonnement(int idUserAbonnement, int idUser, int idAbonnement, LocalDate dateDebut, LocalDate dateFin, String statut, boolean renouvellementAuto, LocalDate dateSouscription) {
        this.idUserAbonnement = idUserAbonnement;
        this.idUser = idUser;
        this.idAbonnement = idAbonnement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.renouvellementAuto = renouvellementAuto;
        this.dateSouscription = dateSouscription;
    }
    
    // Getters and Setters
    public int getIdUserAbonnement() {
        return idUserAbonnement;
    }
    
    public void setIdUserAbonnement(int idUserAbonnement) {
        this.idUserAbonnement = idUserAbonnement;
    }
    
    public int getIdUser() {
        return idUser;
    }
    
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    
    public int getIdAbonnement() {
        return idAbonnement;
    }
    
    public void setIdAbonnement(int idAbonnement) {
        this.idAbonnement = idAbonnement;
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
    
    public LocalDate getDateSouscription() {
        return dateSouscription;
    }
    
    public void setDateSouscription(LocalDate dateSouscription) {
        this.dateSouscription = dateSouscription;
    }
    
    public Abonnement getAbonnement() {
        return abonnement;
    }
    
    public void setAbonnement(Abonnement abonnement) {
        this.abonnement = abonnement;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}
