package tn.esprit.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Depense {
    private long id;
    private String description;
    private BigDecimal montant;
    private LocalDate dateDepense;
    private String categorie;
    private String modePaiement;
    private long compteId;

    public Depense() {
    }

    public Depense(long id, String description, BigDecimal montant, LocalDate dateDepense, String categorie, String modePaiement, long compteId) {
        this.id = id;
        this.description = description;
        this.montant = montant;
        this.dateDepense = dateDepense;
        this.categorie = categorie;
        this.modePaiement = modePaiement;
        this.compteId = compteId;
    }

    public Depense(String description, BigDecimal montant, LocalDate dateDepense, String categorie, String modePaiement, long compteId) {
        this.description = description;
        this.montant = montant;
        this.dateDepense = dateDepense;
        this.categorie = categorie;
        this.modePaiement = modePaiement;
        this.compteId = compteId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDateDepense() {
        return dateDepense;
    }

    public void setDateDepense(LocalDate dateDepense) {
        this.dateDepense = dateDepense;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public long getCompteId() {
        return compteId;
    }

    public void setCompteId(long compteId) {
        this.compteId = compteId;
    }

    @Override
    public String toString() {
        return "Depense{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", montant=" + montant +
                ", dateDepense=" + dateDepense +
                ", categorie='" + categorie + '\'' +
                ", modePaiement='" + modePaiement + '\'' +
                ", compteId=" + compteId +
                '}';
    }
}
