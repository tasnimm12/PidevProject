package tn.esprit.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Abonnement {
    private int idAbonnement;
    private String typeAbonnement; // Bronze, Silver, Gold, Platinum
    private BigDecimal prixMensuel;
    private BigDecimal prixAnnuel;
    private String duree; // mensuel, annuel
    private String description;
    private String avantages;
    private boolean actif;

    public Abonnement() {
    }

    public Abonnement(int idAbonnement, String typeAbonnement, BigDecimal prixMensuel, BigDecimal prixAnnuel, String duree, String description, String avantages, boolean actif) {
        this.idAbonnement = idAbonnement;
        this.typeAbonnement = typeAbonnement;
        this.prixMensuel = prixMensuel;
        this.prixAnnuel = prixAnnuel;
        this.duree = duree;
        this.description = description;
        this.avantages = avantages;
        this.actif = actif;
    }

    public Abonnement(String typeAbonnement, BigDecimal prixMensuel, BigDecimal prixAnnuel, String duree, String description, String avantages, boolean actif) {
        this.typeAbonnement = typeAbonnement;
        this.prixMensuel = prixMensuel;
        this.prixAnnuel = prixAnnuel;
        this.duree = duree;
        this.description = description;
        this.avantages = avantages;
        this.actif = actif;
    }

    public int getIdAbonnement() {
        return idAbonnement;
    }

    public void setIdAbonnement(int idAbonnement) {
        this.idAbonnement = idAbonnement;
    }

    public String getTypeAbonnement() {
        return typeAbonnement;
    }

    public void setTypeAbonnement(String typeAbonnement) {
        this.typeAbonnement = typeAbonnement;
    }

    public BigDecimal getPrixMensuel() {
        return prixMensuel;
    }

    public void setPrixMensuel(BigDecimal prixMensuel) {
        this.prixMensuel = prixMensuel;
    }

    public BigDecimal getPrixAnnuel() {
        return prixAnnuel;
    }

    public void setPrixAnnuel(BigDecimal prixAnnuel) {
        this.prixAnnuel = prixAnnuel;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvantages() {
        return avantages;
    }

    public void setAvantages(String avantages) {
        this.avantages = avantages;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "Abonnement{" +
                "idAbonnement=" + idAbonnement +
                ", typeAbonnement='" + typeAbonnement + '\'' +
                ", prixMensuel=" + prixMensuel +
                ", prixAnnuel=" + prixAnnuel +
                ", duree='" + duree + '\'' +
                ", description='" + description + '\'' +
                ", avantages='" + avantages + '\'' +
                ", actif=" + actif +
                '}';
    }
}
