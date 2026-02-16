package tn.esprit.services;

import tn.esprit.entities.Assurance;
import tn.esprit.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssuranceServices {
    private Connection connection;
    
    public AssuranceServices() {
        connection = DBConnection.getInstance().getCnx();
    }
    
    // Create
    public void ajouter(Assurance assurance) throws SQLException {
        String sql = "INSERT INTO assurance (utilisateur_id, type_assurance, compagnie, numero_police, " +
                    "montant_couverture, franchise, prime_annuelle, prime_mensuelle, date_debut, date_echeance, " +
                    "mode_paiement, statut, renouvellement_auto, garanties_incluses) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, assurance.getUtilisateurId());
        st.setString(2, assurance.getTypeAssurance());
        st.setString(3, assurance.getCompagnie());
        st.setString(4, assurance.getNumeroPolice());
        st.setBigDecimal(5, assurance.getMontantCouverture());
        st.setBigDecimal(6, assurance.getFranchise());
        st.setBigDecimal(7, assurance.getPrimeAnnuelle());
        st.setBigDecimal(8, assurance.getPrimeMensuelle());
        st.setDate(9, Date.valueOf(assurance.getDateDebut()));
        st.setDate(10, Date.valueOf(assurance.getDateEcheance()));
        st.setString(11, assurance.getModePaiement());
        st.setString(12, assurance.getStatut());
        st.setBoolean(13, assurance.isRenouvellementAuto());
        
        // Handle JSON field - convert to null if empty
        String garanties = assurance.getGarantiesIncluses();
        if (garanties == null || garanties.trim().isEmpty()) {
            st.setNull(14, java.sql.Types.LONGVARCHAR);
        } else {
            st.setString(14, garanties);
        }
        
        st.executeUpdate();
    }
    
    // Read All
    public List<Assurance> afficher() throws SQLException {
        List<Assurance> assurances = new ArrayList<>();
        String sql = "SELECT * FROM assurance";
        
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        
        while (rs.next()) {
            assurances.add(mapResultSetToAssurance(rs));
        }
        
        return assurances;
    }
    
    // Read by ID
    public Assurance getById(int id) throws SQLException {
        String sql = "SELECT * FROM assurance WHERE id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, id);
        
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return mapResultSetToAssurance(rs);
        }
        return null;
    }
    
    // Read by User ID
    public List<Assurance> getByUserId(int userId) throws SQLException {
        List<Assurance> assurances = new ArrayList<>();
        String sql = "SELECT * FROM assurance WHERE utilisateur_id = ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            assurances.add(mapResultSetToAssurance(rs));
        }
        
        return assurances;
    }
    
    // Update
    public void modifier(Assurance assurance) throws SQLException {
        String sql = "UPDATE assurance SET utilisateur_id = ?, type_assurance = ?, compagnie = ?, " +
                    "numero_police = ?, montant_couverture = ?, franchise = ?, prime_annuelle = ?, " +
                    "prime_mensuelle = ?, date_debut = ?, date_echeance = ?, mode_paiement = ?, " +
                    "statut = ?, renouvellement_auto = ?, garanties_incluses = ? WHERE id = ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, assurance.getUtilisateurId());
        st.setString(2, assurance.getTypeAssurance());
        st.setString(3, assurance.getCompagnie());
        st.setString(4, assurance.getNumeroPolice());
        st.setBigDecimal(5, assurance.getMontantCouverture());
        st.setBigDecimal(6, assurance.getFranchise());
        st.setBigDecimal(7, assurance.getPrimeAnnuelle());
        st.setBigDecimal(8, assurance.getPrimeMensuelle());
        st.setDate(9, Date.valueOf(assurance.getDateDebut()));
        st.setDate(10, Date.valueOf(assurance.getDateEcheance()));
        st.setString(11, assurance.getModePaiement());
        st.setString(12, assurance.getStatut());
        st.setBoolean(13, assurance.isRenouvellementAuto());
        
        // Handle JSON field - convert to null if empty
        String garanties = assurance.getGarantiesIncluses();
        if (garanties == null || garanties.trim().isEmpty()) {
            st.setNull(14, java.sql.Types.LONGVARCHAR);
        } else {
            st.setString(14, garanties);
        }
        st.setInt(15, assurance.getId());
        
        st.executeUpdate();
    }
    
    // Delete
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM assurance WHERE id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, id);
        st.executeUpdate();
    }
    
    // Helper method to map ResultSet to Assurance object
    private Assurance mapResultSetToAssurance(ResultSet rs) throws SQLException {
        Assurance assurance = new Assurance();
        assurance.setId(rs.getInt("id"));
        assurance.setUtilisateurId(rs.getInt("utilisateur_id"));
        assurance.setTypeAssurance(rs.getString("type_assurance"));
        assurance.setCompagnie(rs.getString("compagnie"));
        assurance.setNumeroPolice(rs.getString("numero_police"));
        assurance.setMontantCouverture(rs.getBigDecimal("montant_couverture"));
        assurance.setFranchise(rs.getBigDecimal("franchise"));
        assurance.setPrimeAnnuelle(rs.getBigDecimal("prime_annuelle"));
        assurance.setPrimeMensuelle(rs.getBigDecimal("prime_mensuelle"));
        
        if (rs.getDate("date_debut") != null) {
            assurance.setDateDebut(rs.getDate("date_debut").toLocalDate());
        }
        if (rs.getDate("date_echeance") != null) {
            assurance.setDateEcheance(rs.getDate("date_echeance").toLocalDate());
        }
        
        assurance.setModePaiement(rs.getString("mode_paiement"));
        assurance.setStatut(rs.getString("statut"));
        assurance.setRenouvellementAuto(rs.getBoolean("renouvellement_auto"));
        
        // Handle JSON field - may be null
        String garanties = rs.getString("garanties_incluses");
        assurance.setGarantiesIncluses(garanties != null ? garanties : "");
        
        return assurance;
    }
}
