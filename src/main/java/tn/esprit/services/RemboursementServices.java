package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.Remboursement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemboursementServices {
    private Connection cnx;
    
    public RemboursementServices() {
        cnx = DBConnection.getInstance().getCnx();
    }
    
    public void ajouter(Remboursement remboursement) throws SQLException {
        String sql = "INSERT INTO remboursement (credit_id, user_id, compte_id, type_remboursement, " +
                    "montant, date_remboursement, statut, description, reference_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setObject(1, remboursement.getCreditId());
        st.setInt(2, remboursement.getUserId());
        st.setLong(3, remboursement.getCompteId());
        st.setString(4, remboursement.getTypeRemboursement());
        st.setBigDecimal(5, remboursement.getMontant());
        st.setDate(6, Date.valueOf(remboursement.getDateRemboursement()));
        st.setString(7, remboursement.getStatut());
        st.setString(8, remboursement.getDescription());
        st.setObject(9, remboursement.getReferenceId());
        st.executeUpdate();
    }
    
    public void modifier(Remboursement remboursement) throws SQLException {
        String sql = "UPDATE remboursement SET credit_id=?, user_id=?, compte_id=?, type_remboursement=?, " +
                    "montant=?, date_remboursement=?, statut=?, description=?, reference_id=? " +
                    "WHERE id_remboursement=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setObject(1, remboursement.getCreditId());
        st.setInt(2, remboursement.getUserId());
        st.setLong(3, remboursement.getCompteId());
        st.setString(4, remboursement.getTypeRemboursement());
        st.setBigDecimal(5, remboursement.getMontant());
        st.setDate(6, Date.valueOf(remboursement.getDateRemboursement()));
        st.setString(7, remboursement.getStatut());
        st.setString(8, remboursement.getDescription());
        st.setObject(9, remboursement.getReferenceId());
        st.setInt(10, remboursement.getIdRemboursement());
        st.executeUpdate();
    }
    
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM remboursement WHERE id_remboursement=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        st.executeUpdate();
    }
    
    public List<Remboursement> afficher() throws SQLException {
        List<Remboursement> remboursements = new ArrayList<>();
        String sql = "SELECT * FROM remboursement ORDER BY date_remboursement DESC";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        
        while (rs.next()) {
            remboursements.add(mapResultSetToRemboursement(rs));
        }
        return remboursements;
    }
    
    public Remboursement getById(int id) throws SQLException {
        String sql = "SELECT * FROM remboursement WHERE id_remboursement=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            return mapResultSetToRemboursement(rs);
        }
        return null;
    }
    
    public List<Remboursement> getByUserId(int userId) throws SQLException {
        List<Remboursement> remboursements = new ArrayList<>();
        String sql = "SELECT * FROM remboursement WHERE user_id=? ORDER BY date_remboursement DESC";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            remboursements.add(mapResultSetToRemboursement(rs));
        }
        return remboursements;
    }
    
    public List<Remboursement> getByCreditId(int creditId) throws SQLException {
        List<Remboursement> remboursements = new ArrayList<>();
        String sql = "SELECT * FROM remboursement WHERE credit_id=? ORDER BY date_remboursement DESC";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, creditId);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            remboursements.add(mapResultSetToRemboursement(rs));
        }
        return remboursements;
    }
    
    public List<Remboursement> getByType(String type) throws SQLException {
        List<Remboursement> remboursements = new ArrayList<>();
        String sql = "SELECT * FROM remboursement WHERE type_remboursement=? ORDER BY date_remboursement DESC";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, type);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            remboursements.add(mapResultSetToRemboursement(rs));
        }
        return remboursements;
    }
    
    private Remboursement mapResultSetToRemboursement(ResultSet rs) throws SQLException {
        Integer creditId = rs.getObject("credit_id") != null ? rs.getInt("credit_id") : null;
        Integer referenceId = rs.getObject("reference_id") != null ? rs.getInt("reference_id") : null;
        
        return new Remboursement(
            rs.getInt("id_remboursement"),
            creditId,
            rs.getInt("user_id"),
            rs.getLong("compte_id"),
            rs.getString("type_remboursement"),
            rs.getBigDecimal("montant"),
            rs.getDate("date_remboursement").toLocalDate(),
            rs.getString("statut"),
            rs.getString("description"),
            referenceId
        );
    }
}
