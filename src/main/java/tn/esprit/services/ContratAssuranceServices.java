package tn.esprit.services;

import tn.esprit.entities.ContratAssurance;
import tn.esprit.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContratAssuranceServices {
    private Connection connection;
    
    public ContratAssuranceServices() {
        connection = DBConnection.getInstance().getCnx();
    }
    
    // Create
    public void ajouter(ContratAssurance contrat) throws SQLException {
        String sql = "INSERT INTO contrat_assurance (assurance_id, utilisateur_id, numero_contrat, date_signature, " +
                    "date_fin_contrat, duree_contrat, conditions_particulieres, exclusions, plafond_annuel, " +
                    "taux_remboursement, delai_carence, clause_beneficiaire, document_contrat, amendements, " +
                    "conseiller_attribue, contacts, statut) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, contrat.getAssuranceId());
        st.setInt(2, contrat.getUtilisateurId());
        st.setString(3, contrat.getNumeroContrat());
        st.setDate(4, contrat.getDateSignature() != null ? Date.valueOf(contrat.getDateSignature()) : null);
        st.setDate(5, contrat.getDateFinContrat() != null ? Date.valueOf(contrat.getDateFinContrat()) : null);
        
        if (contrat.getDureeContrat() != null) {
            st.setInt(6, contrat.getDureeContrat());
        } else {
            st.setNull(6, Types.INTEGER);
        }
        
        st.setString(7, contrat.getConditionsParticulieres());
        st.setString(8, contrat.getExclusions());
        st.setBigDecimal(9, contrat.getPlafondAnnuel());
        st.setBigDecimal(10, contrat.getTauxRemboursement());
        
        if (contrat.getDelaiCarence() != null) {
            st.setInt(11, contrat.getDelaiCarence());
        } else {
            st.setNull(11, Types.INTEGER);
        }
        
        st.setString(12, contrat.getClauseBeneficiaire());
        st.setString(13, contrat.getDocumentContrat());
        
        // Handle JSON field - convert to null if empty
        String amendements = contrat.getAmendements();
        if (amendements == null || amendements.trim().isEmpty()) {
            st.setNull(14, java.sql.Types.LONGVARCHAR);
        } else {
            st.setString(14, amendements);
        }
        
        st.setString(15, contrat.getConseillerAttribue());
        st.setString(16, contrat.getContacts());
        st.setString(17, contrat.getStatut());
        
        st.executeUpdate();
    }
    
    // Read All
    public List<ContratAssurance> afficher() throws SQLException {
        List<ContratAssurance> contrats = new ArrayList<>();
        String sql = "SELECT * FROM contrat_assurance";
        
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        
        while (rs.next()) {
            contrats.add(mapResultSetToContrat(rs));
        }
        
        return contrats;
    }
    
    // Read by ID
    public ContratAssurance getById(int id) throws SQLException {
        String sql = "SELECT * FROM contrat_assurance WHERE id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, id);
        
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return mapResultSetToContrat(rs);
        }
        return null;
    }
    
    // Read by Assurance ID
    public List<ContratAssurance> getByAssuranceId(int assuranceId) throws SQLException {
        List<ContratAssurance> contrats = new ArrayList<>();
        String sql = "SELECT * FROM contrat_assurance WHERE assurance_id = ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, assuranceId);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            contrats.add(mapResultSetToContrat(rs));
        }
        
        return contrats;
    }
    
    // Read by User ID
    public List<ContratAssurance> getByUserId(int userId) throws SQLException {
        List<ContratAssurance> contrats = new ArrayList<>();
        String sql = "SELECT * FROM contrat_assurance WHERE utilisateur_id = ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            contrats.add(mapResultSetToContrat(rs));
        }
        
        return contrats;
    }
    
    // Update
    public void modifier(ContratAssurance contrat) throws SQLException {
        String sql = "UPDATE contrat_assurance SET assurance_id = ?, utilisateur_id = ?, numero_contrat = ?, date_signature = ?, " +
                    "date_fin_contrat = ?, duree_contrat = ?, conditions_particulieres = ?, exclusions = ?, " +
                    "plafond_annuel = ?, taux_remboursement = ?, delai_carence = ?, clause_beneficiaire = ?, " +
                    "document_contrat = ?, amendements = ?, conseiller_attribue = ?, contacts = ?, statut = ? " +
                    "WHERE id = ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, contrat.getAssuranceId());
        st.setInt(2, contrat.getUtilisateurId());
        st.setString(3, contrat.getNumeroContrat());
        st.setDate(4, contrat.getDateSignature() != null ? Date.valueOf(contrat.getDateSignature()) : null);
        st.setDate(5, contrat.getDateFinContrat() != null ? Date.valueOf(contrat.getDateFinContrat()) : null);
        
        if (contrat.getDureeContrat() != null) {
            st.setInt(6, contrat.getDureeContrat());
        } else {
            st.setNull(6, Types.INTEGER);
        }
        
        st.setString(7, contrat.getConditionsParticulieres());
        st.setString(8, contrat.getExclusions());
        st.setBigDecimal(9, contrat.getPlafondAnnuel());
        st.setBigDecimal(10, contrat.getTauxRemboursement());
        
        if (contrat.getDelaiCarence() != null) {
            st.setInt(11, contrat.getDelaiCarence());
        } else {
            st.setNull(11, Types.INTEGER);
        }
        
        st.setString(12, contrat.getClauseBeneficiaire());
        st.setString(13, contrat.getDocumentContrat());
        
        // Handle JSON field - convert to null if empty
        String amendements = contrat.getAmendements();
        if (amendements == null || amendements.trim().isEmpty()) {
            st.setNull(14, java.sql.Types.LONGVARCHAR);
        } else {
            st.setString(14, amendements);
        }
        
        st.setString(15, contrat.getConseillerAttribue());
        st.setString(16, contrat.getContacts());
        st.setString(17, contrat.getStatut());
        st.setInt(18, contrat.getId());
        
        st.executeUpdate();
    }
    
    // Delete
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM contrat_assurance WHERE id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, id);
        st.executeUpdate();
    }
    
    // Helper method to map ResultSet to ContratAssurance object
    private ContratAssurance mapResultSetToContrat(ResultSet rs) throws SQLException {
        ContratAssurance contrat = new ContratAssurance();
        contrat.setId(rs.getInt("id"));
        contrat.setAssuranceId(rs.getInt("assurance_id"));
        contrat.setUtilisateurId(rs.getInt("utilisateur_id"));
        contrat.setNumeroContrat(rs.getString("numero_contrat"));
        
        if (rs.getDate("date_signature") != null) {
            contrat.setDateSignature(rs.getDate("date_signature").toLocalDate());
        }
        if (rs.getDate("date_fin_contrat") != null) {
            contrat.setDateFinContrat(rs.getDate("date_fin_contrat").toLocalDate());
        }
        
        int duree = rs.getInt("duree_contrat");
        if (!rs.wasNull()) {
            contrat.setDureeContrat(duree);
        }
        
        contrat.setConditionsParticulieres(rs.getString("conditions_particulieres"));
        contrat.setExclusions(rs.getString("exclusions"));
        contrat.setPlafondAnnuel(rs.getBigDecimal("plafond_annuel"));
        contrat.setTauxRemboursement(rs.getBigDecimal("taux_remboursement"));
        
        int delai = rs.getInt("delai_carence");
        if (!rs.wasNull()) {
            contrat.setDelaiCarence(delai);
        }
        
        contrat.setClauseBeneficiaire(rs.getString("clause_beneficiaire"));
        contrat.setDocumentContrat(rs.getString("document_contrat"));
        
        // Handle JSON field - may be null
        String amendements = rs.getString("amendements");
        contrat.setAmendements(amendements != null ? amendements : "");
        
        contrat.setConseillerAttribue(rs.getString("conseiller_attribue"));
        contrat.setContacts(rs.getString("contacts"));
        contrat.setStatut(rs.getString("statut"));
        
        return contrat;
    }
}
