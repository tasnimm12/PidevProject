package tn.esprit.entities;

import java.time.LocalDate;

public class Projet {
    private int idProjet;
    private String nomProjet;
    private String description;
    private String secteur;
    private int montantObjectif;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statutProjet;

    public Projet() {
    }

    public Projet(int idProjet, String nomProjet, String description, String secteur, int montantObjectif, LocalDate dateDebut, LocalDate dateFin, String statutProjet) {
        this.idProjet = idProjet;
        this.nomProjet = nomProjet;
        this.description = description;
        this.secteur = secteur;
        this.montantObjectif = montantObjectif;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statutProjet = statutProjet;
    }

    public Projet(String nomProjet, String description, String secteur, int montantObjectif, LocalDate dateDebut, LocalDate dateFin, String statutProjet) {
        this.nomProjet = nomProjet;
        this.description = description;
        this.secteur = secteur;
        this.montantObjectif = montantObjectif;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statutProjet = statutProjet;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSecteur() {
        return secteur;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public int getMontantObjectif() {
        return montantObjectif;
    }

    public void setMontantObjectif(int montantObjectif) {
        this.montantObjectif = montantObjectif;
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

    public String getStatutProjet() {
        return statutProjet;
    }

    public void setStatutProjet(String statutProjet) {
        this.statutProjet = statutProjet;
    }

    @Override
    public String toString() {
        return "Projet{" +
                "idProjet=" + idProjet +
                ", nomProjet='" + nomProjet + '\'' +
                ", description='" + description + '\'' +
                ", secteur='" + secteur + '\'' +
                ", montantObjectif=" + montantObjectif +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statutProjet='" + statutProjet + '\'' +
                '}';
    }
}
