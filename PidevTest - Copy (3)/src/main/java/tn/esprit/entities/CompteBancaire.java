package tn.esprit.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CompteBancaire {
    private long id;
    private int userId;
    private String numeroCompte;
    private String titulaire;
    private String email;
    private String telephone;
    private BigDecimal solde;
    private String devise;
    private String typeCompte;
    private LocalDate dateCreation;
    private boolean actif;

    public CompteBancaire() {
    }

    public CompteBancaire(long id, int userId, String numeroCompte, String titulaire, String email, String telephone, BigDecimal solde, String devise, String typeCompte, LocalDate dateCreation, boolean actif) {
        this.id = id;
        this.userId = userId;
        this.numeroCompte = numeroCompte;
        this.titulaire = titulaire;
        this.email = email;
        this.telephone = telephone;
        this.solde = solde;
        this.devise = devise;
        this.typeCompte = typeCompte;
        this.dateCreation = dateCreation;
        this.actif = actif;
    }

    public CompteBancaire(int userId, String numeroCompte, String titulaire, String email, String telephone, BigDecimal solde, String devise, String typeCompte, LocalDate dateCreation, boolean actif) {
        this.userId = userId;
        this.numeroCompte = numeroCompte;
        this.titulaire = titulaire;
        this.email = email;
        this.telephone = telephone;
        this.solde = solde;
        this.devise = devise;
        this.typeCompte = typeCompte;
        this.dateCreation = dateCreation;
        this.actif = actif;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public String getTitulaire() {
        return titulaire;
    }

    public void setTitulaire(String titulaire) {
        this.titulaire = titulaire;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public String getTypeCompte() {
        return typeCompte;
    }

    public void setTypeCompte(String typeCompte) {
        this.typeCompte = typeCompte;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "CompteBancaire{" +
                "id=" + id +
                ", userId=" + userId +
                ", numeroCompte='" + numeroCompte + '\'' +
                ", titulaire='" + titulaire + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", solde=" + solde +
                ", devise='" + devise + '\'' +
                ", typeCompte='" + typeCompte + '\'' +
                ", dateCreation=" + dateCreation +
                ", actif=" + actif +
                '}';
    }
}
