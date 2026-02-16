package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.Investissement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvestissementServices {
    private Connection cnx;
    
    public InvestissementServices() {
        cnx = DBConnection.getInstance().getCnx();
    }
    
    // Create
    public void ajouter(Investissement inves) throws SQLException {
        String sql = "INSERT INTO investissement (montantinvesti, dateinves, modepaiement, statut_investissement, idprojet) " +
                    "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, inves.getMontantInvesti());
        st.setDate(2, inves.getDateInves() != null ? Date.valueOf(inves.getDateInves()) : null);
        st.setString(3, inves.getModePaiement());
        st.setString(4, inves.getStatutInvestissement());
        st.setInt(5, inves.getIdProjet());
        st.executeUpdate();
    }
    
    // Read All
    public List<Investissement> afficher() throws SQLException {
        List<Investissement> investissements = new ArrayList<>();
        String sql = "SELECT * FROM investissement";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        
        while (rs.next()) {
            investissements.add(mapResultSetToInvestissement(rs));
        }
        
        return investissements;
    }
    
    // Read by ID
    public Investissement getById(int id) throws SQLException {
        String sql = "SELECT * FROM investissement WHERE idinves = ?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            return mapResultSetToInvestissement(rs);
        }
        
        return null;
    }
    
    // Read by Projet ID
    public List<Investissement> getByProjetId(int projetId) throws SQLException {
        List<Investissement> investissements = new ArrayList<>();
        String sql = "SELECT * FROM investissement WHERE idprojet = ?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, projetId);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            investissements.add(mapResultSetToInvestissement(rs));
        }
        
        return investissements;
    }
    
    // Update
    public void modifier(Investissement inves) throws SQLException {
        String sql = "UPDATE investissement SET montantinvesti=?, dateinves=?, modepaiement=?, " +
                    "statut_investissement=?, idprojet=? WHERE idinves=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, inves.getMontantInvesti());
        st.setDate(2, inves.getDateInves() != null ? Date.valueOf(inves.getDateInves()) : null);
        st.setString(3, inves.getModePaiement());
        st.setString(4, inves.getStatutInvestissement());
        st.setInt(5, inves.getIdProjet());
        st.setInt(6, inves.getIdInves());
        st.executeUpdate();
    }
    
    // Delete
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM investissement WHERE idinves=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        st.executeUpdate();
    }
    
    // Helper method to map ResultSet to Investissement
    private Investissement mapResultSetToInvestissement(ResultSet rs) throws SQLException {
        Investissement inves = new Investissement();
        inves.setIdInves(rs.getInt("idinves"));
        inves.setMontantInvesti(rs.getInt("montantinvesti"));
        
        if (rs.getDate("dateinves") != null) {
            inves.setDateInves(rs.getDate("dateinves").toLocalDate());
        }
        
        inves.setModePaiement(rs.getString("modepaiement"));
        inves.setStatutInvestissement(rs.getString("statut_investissement"));
        inves.setIdProjet(rs.getInt("idprojet"));
        
        return inves;
    }
}
