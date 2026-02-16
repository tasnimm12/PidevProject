package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.Credit;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreditServices {
    private Connection cnx;
    
    public CreditServices() {
        cnx = DBConnection.getInstance().getCnx();
    }
    
    public void ajouter(Credit credit) throws SQLException {
        String sql = "INSERT INTO credit (user_id, compte_id, montant_demande, type_credit, taux_interet, " +
                    "montant_total, montant_restant, date_demande, date_debut, date_fin, statut_credit, " +
                    "motif_refus, mensualite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, credit.getUserId());
        st.setLong(2, credit.getCompteId());
        st.setBigDecimal(3, credit.getMontantDemande());
        st.setString(4, credit.getTypeCredit());
        st.setBigDecimal(5, credit.getTauxInteret());
        st.setBigDecimal(6, credit.getMontantTotal());
        st.setBigDecimal(7, credit.getMontantRestant());
        st.setDate(8, Date.valueOf(credit.getDateDemande()));
        st.setDate(9, credit.getDateDebut() != null ? Date.valueOf(credit.getDateDebut()) : null);
        st.setDate(10, credit.getDateFin() != null ? Date.valueOf(credit.getDateFin()) : null);
        st.setString(11, credit.getStatutCredit());
        st.setString(12, credit.getMotifRefus());
        st.setBigDecimal(13, credit.getMensualite());
        st.executeUpdate();
    }
    
    public void modifier(Credit credit) throws SQLException {
        String sql = "UPDATE credit SET user_id=?, compte_id=?, montant_demande=?, type_credit=?, " +
                    "taux_interet=?, montant_total=?, montant_restant=?, date_demande=?, date_debut=?, " +
                    "date_fin=?, statut_credit=?, motif_refus=?, mensualite=? WHERE id_credit=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, credit.getUserId());
        st.setLong(2, credit.getCompteId());
        st.setBigDecimal(3, credit.getMontantDemande());
        st.setString(4, credit.getTypeCredit());
        st.setBigDecimal(5, credit.getTauxInteret());
        st.setBigDecimal(6, credit.getMontantTotal());
        st.setBigDecimal(7, credit.getMontantRestant());
        st.setDate(8, Date.valueOf(credit.getDateDemande()));
        st.setDate(9, credit.getDateDebut() != null ? Date.valueOf(credit.getDateDebut()) : null);
        st.setDate(10, credit.getDateFin() != null ? Date.valueOf(credit.getDateFin()) : null);
        st.setString(11, credit.getStatutCredit());
        st.setString(12, credit.getMotifRefus());
        st.setBigDecimal(13, credit.getMensualite());
        st.setInt(14, credit.getIdCredit());
        st.executeUpdate();
    }
    
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM credit WHERE id_credit=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        st.executeUpdate();
    }
    
    public List<Credit> afficher() throws SQLException {
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM credit ORDER BY date_demande DESC";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        
        while (rs.next()) {
            credits.add(mapResultSetToCredit(rs));
        }
        return credits;
    }
    
    public Credit getById(int id) throws SQLException {
        String sql = "SELECT * FROM credit WHERE id_credit=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            return mapResultSetToCredit(rs);
        }
        return null;
    }
    
    public List<Credit> getByUserId(int userId) throws SQLException {
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM credit WHERE user_id=? ORDER BY date_demande DESC";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            credits.add(mapResultSetToCredit(rs));
        }
        return credits;
    }
    
    public List<Credit> getByStatut(String statut) throws SQLException {
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM credit WHERE statut_credit=? ORDER BY date_demande DESC";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, statut);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            credits.add(mapResultSetToCredit(rs));
        }
        return credits;
    }
    
    private Credit mapResultSetToCredit(ResultSet rs) throws SQLException {
        return new Credit(
            rs.getInt("id_credit"),
            rs.getInt("user_id"),
            rs.getLong("compte_id"),
            rs.getBigDecimal("montant_demande"),
            rs.getString("type_credit"),
            rs.getBigDecimal("taux_interet"),
            rs.getBigDecimal("montant_total"),
            rs.getBigDecimal("montant_restant"),
            rs.getDate("date_demande").toLocalDate(),
            rs.getDate("date_debut") != null ? rs.getDate("date_debut").toLocalDate() : null,
            rs.getDate("date_fin") != null ? rs.getDate("date_fin").toLocalDate() : null,
            rs.getString("statut_credit"),
            rs.getString("motif_refus"),
            rs.getBigDecimal("mensualite")
        );
    }
    
    /**
     * Calculate interest rate based on user's subscription type
     */
    public BigDecimal calculateInterestRate(String subscriptionType) {
        if (subscriptionType == null) {
            return new BigDecimal("15.00"); // No subscription
        }
        
        switch (subscriptionType.toLowerCase()) {
            case "gold":
                return new BigDecimal("8.00");
            case "premium":
                return new BigDecimal("10.00");
            case "basic":
                return new BigDecimal("12.00");
            default:
                return new BigDecimal("15.00");
        }
    }
    
    /**
     * Calculate total amount to pay (principal + interest)
     */
    public BigDecimal calculateMontantTotal(BigDecimal principal, BigDecimal tauxInteret, int durationMonths) {
        // Simple interest formula: Total = Principal * (1 + (rate/100) * (months/12))
        BigDecimal rate = tauxInteret.divide(new BigDecimal("100"));
        BigDecimal years = new BigDecimal(durationMonths).divide(new BigDecimal("12"), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal interest = principal.multiply(rate).multiply(years);
        return principal.add(interest);
    }
    
    /**
     * Calculate monthly payment amount
     */
    public BigDecimal calculateMensualite(BigDecimal montantTotal, int durationMonths) {
        return montantTotal.divide(new BigDecimal(durationMonths), 2, BigDecimal.ROUND_HALF_UP);
    }
}
