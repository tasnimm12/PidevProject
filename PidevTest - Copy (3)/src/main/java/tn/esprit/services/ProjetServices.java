package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.Projet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetServices {
    private Connection cnx;

    public ProjetServices() {
        cnx = DBConnection.getInstance().getCnx();
    }

    public void ajouter(Projet projet) throws SQLException {
        String sql = "INSERT INTO projet (nomprojet, description, secteur, montant_objectif, date_debut, date_fin, statut_projet) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, projet.getNomProjet());
        st.setString(2, projet.getDescription());
        st.setString(3, projet.getSecteur());
        st.setInt(4, projet.getMontantObjectif());
        st.setDate(5, projet.getDateDebut() != null ? Date.valueOf(projet.getDateDebut()) : null);
        st.setDate(6, projet.getDateFin() != null ? Date.valueOf(projet.getDateFin()) : null);
        st.setString(7, projet.getStatutProjet());
        st.executeUpdate();
    }

    public void modifier(Projet projet) throws SQLException {
        String sql = "UPDATE projet SET nomprojet=?, description=?, secteur=?, montant_objectif=?, date_debut=?, date_fin=?, statut_projet=? WHERE idprojet=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, projet.getNomProjet());
        st.setString(2, projet.getDescription());
        st.setString(3, projet.getSecteur());
        st.setInt(4, projet.getMontantObjectif());
        st.setDate(5, projet.getDateDebut() != null ? Date.valueOf(projet.getDateDebut()) : null);
        st.setDate(6, projet.getDateFin() != null ? Date.valueOf(projet.getDateFin()) : null);
        st.setString(7, projet.getStatutProjet());
        st.setInt(8, projet.getIdProjet());
        st.executeUpdate();
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM projet WHERE idprojet=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        st.executeUpdate();
    }

    public List<Projet> afficher() throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM projet";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Projet projet = new Projet(
                    rs.getInt("idprojet"),
                    rs.getString("nomprojet"),
                    rs.getString("description"),
                    rs.getString("secteur"),
                    rs.getInt("montant_objectif"),
                    rs.getDate("date_debut") != null ? rs.getDate("date_debut").toLocalDate() : null,
                    rs.getDate("date_fin") != null ? rs.getDate("date_fin").toLocalDate() : null,
                    rs.getString("statut_projet")
            );
            projets.add(projet);
        }
        return projets;
    }

    public Projet getById(int id) throws SQLException {
        String sql = "SELECT * FROM projet WHERE idprojet=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return new Projet(
                    rs.getInt("idprojet"),
                    rs.getString("nomprojet"),
                    rs.getString("description"),
                    rs.getString("secteur"),
                    rs.getInt("montant_objectif"),
                    rs.getDate("date_debut") != null ? rs.getDate("date_debut").toLocalDate() : null,
                    rs.getDate("date_fin") != null ? rs.getDate("date_fin").toLocalDate() : null,
                    rs.getString("statut_projet")
            );
        }
        return null;
    }
}
