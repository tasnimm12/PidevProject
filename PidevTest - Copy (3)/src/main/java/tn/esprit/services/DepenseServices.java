package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.Depense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepenseServices {
    private Connection cnx;

    public DepenseServices() {
        cnx = DBConnection.getInstance().getCnx();
    }

    public void ajouter(Depense depense) throws SQLException {
        String sql = "INSERT INTO depense (description, montant, date_depense, categorie, mode_paiement, compte_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, depense.getDescription());
        st.setBigDecimal(2, depense.getMontant());
        st.setDate(3, depense.getDateDepense() != null ? Date.valueOf(depense.getDateDepense()) : null);
        st.setString(4, depense.getCategorie());
        st.setString(5, depense.getModePaiement());
        st.setLong(6, depense.getCompteId());
        st.executeUpdate();
    }

    public void modifier(Depense depense) throws SQLException {
        String sql = "UPDATE depense SET description=?, montant=?, date_depense=?, categorie=?, mode_paiement=?, compte_id=? WHERE id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, depense.getDescription());
        st.setBigDecimal(2, depense.getMontant());
        st.setDate(3, depense.getDateDepense() != null ? Date.valueOf(depense.getDateDepense()) : null);
        st.setString(4, depense.getCategorie());
        st.setString(5, depense.getModePaiement());
        st.setLong(6, depense.getCompteId());
        st.setLong(7, depense.getId());
        st.executeUpdate();
    }

    public void supprimer(long id) throws SQLException {
        String sql = "DELETE FROM depense WHERE id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setLong(1, id);
        st.executeUpdate();
    }

    public List<Depense> afficher() throws SQLException {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT * FROM depense";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Depense depense = new Depense(
                    rs.getLong("id"),
                    rs.getString("description"),
                    rs.getBigDecimal("montant"),
                    rs.getDate("date_depense") != null ? rs.getDate("date_depense").toLocalDate() : null,
                    rs.getString("categorie"),
                    rs.getString("mode_paiement"),
                    rs.getLong("compte_id")
            );
            depenses.add(depense);
        }
        return depenses;
    }

    public Depense getById(long id) throws SQLException {
        String sql = "SELECT * FROM depense WHERE id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setLong(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return new Depense(
                    rs.getLong("id"),
                    rs.getString("description"),
                    rs.getBigDecimal("montant"),
                    rs.getDate("date_depense") != null ? rs.getDate("date_depense").toLocalDate() : null,
                    rs.getString("categorie"),
                    rs.getString("mode_paiement"),
                    rs.getLong("compte_id")
            );
        }
        return null;
    }

    public List<Depense> getByCompteId(long compteId) throws SQLException {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT * FROM depense WHERE compte_id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setLong(1, compteId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Depense depense = new Depense(
                    rs.getLong("id"),
                    rs.getString("description"),
                    rs.getBigDecimal("montant"),
                    rs.getDate("date_depense") != null ? rs.getDate("date_depense").toLocalDate() : null,
                    rs.getString("categorie"),
                    rs.getString("mode_paiement"),
                    rs.getLong("compte_id")
            );
            depenses.add(depense);
        }
        return depenses;
    }
}
