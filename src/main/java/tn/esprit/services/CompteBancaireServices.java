package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.CompteBancaire;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompteBancaireServices {
    private Connection cnx;

    public CompteBancaireServices() {
        cnx = DBConnection.getInstance().getCnx();
    }

    public void ajouter(CompteBancaire compte) throws SQLException {
        String sql = "INSERT INTO compte_bancaire (user_id, numero_compte, titulaire, email, telephone, solde, devise, type_compte, date_creation, actif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, compte.getUserId());
        st.setString(2, compte.getNumeroCompte());
        st.setString(3, compte.getTitulaire());
        st.setString(4, compte.getEmail());
        st.setString(5, compte.getTelephone());
        st.setBigDecimal(6, compte.getSolde());
        st.setString(7, compte.getDevise());
        st.setString(8, compte.getTypeCompte());
        st.setDate(9, compte.getDateCreation() != null ? Date.valueOf(compte.getDateCreation()) : null);
        st.setBoolean(10, compte.isActif());
        st.executeUpdate();
    }

    public void modifier(CompteBancaire compte) throws SQLException {
        String sql = "UPDATE compte_bancaire SET user_id=?, numero_compte=?, titulaire=?, email=?, telephone=?, solde=?, devise=?, type_compte=?, date_creation=?, actif=? WHERE id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, compte.getUserId());
        st.setString(2, compte.getNumeroCompte());
        st.setString(3, compte.getTitulaire());
        st.setString(4, compte.getEmail());
        st.setString(5, compte.getTelephone());
        st.setBigDecimal(6, compte.getSolde());
        st.setString(7, compte.getDevise());
        st.setString(8, compte.getTypeCompte());
        st.setDate(9, compte.getDateCreation() != null ? Date.valueOf(compte.getDateCreation()) : null);
        st.setBoolean(10, compte.isActif());
        st.setLong(11, compte.getId());
        st.executeUpdate();
    }

    public void supprimer(long id) throws SQLException {
        String sql = "DELETE FROM compte_bancaire WHERE id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setLong(1, id);
        st.executeUpdate();
    }

    public List<CompteBancaire> afficher() throws SQLException {
        List<CompteBancaire> comptes = new ArrayList<>();
        String sql = "SELECT * FROM compte_bancaire";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            CompteBancaire compte = new CompteBancaire(
                    rs.getLong("id"),
                    rs.getInt("user_id"),
                    rs.getString("numero_compte"),
                    rs.getString("titulaire"),
                    rs.getString("email"),
                    rs.getString("telephone"),
                    rs.getBigDecimal("solde"),
                    rs.getString("devise"),
                    rs.getString("type_compte"),
                    rs.getDate("date_creation") != null ? rs.getDate("date_creation").toLocalDate() : null,
                    rs.getBoolean("actif")
            );
            comptes.add(compte);
        }
        return comptes;
    }

    public CompteBancaire getById(long id) throws SQLException {
        String sql = "SELECT * FROM compte_bancaire WHERE id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setLong(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return new CompteBancaire(
                    rs.getLong("id"),
                    rs.getInt("user_id"),
                    rs.getString("numero_compte"),
                    rs.getString("titulaire"),
                    rs.getString("email"),
                    rs.getString("telephone"),
                    rs.getBigDecimal("solde"),
                    rs.getString("devise"),
                    rs.getString("type_compte"),
                    rs.getDate("date_creation") != null ? rs.getDate("date_creation").toLocalDate() : null,
                    rs.getBoolean("actif")
            );
        }
        return null;
    }

    public List<CompteBancaire> getByUserId(int userId) throws SQLException {
        List<CompteBancaire> comptes = new ArrayList<>();
        String sql = "SELECT * FROM compte_bancaire WHERE user_id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            CompteBancaire compte = new CompteBancaire(
                    rs.getLong("id"),
                    rs.getInt("user_id"),
                    rs.getString("numero_compte"),
                    rs.getString("titulaire"),
                    rs.getString("email"),
                    rs.getString("telephone"),
                    rs.getBigDecimal("solde"),
                    rs.getString("devise"),
                    rs.getString("type_compte"),
                    rs.getDate("date_creation") != null ? rs.getDate("date_creation").toLocalDate() : null,
                    rs.getBoolean("actif")
            );
            comptes.add(compte);
        }
        return comptes;
    }
}
