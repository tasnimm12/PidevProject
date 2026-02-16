package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.Abonnement;
import tn.esprit.entities.UserAbonnement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAbonnementServices {
    private Connection cnx;

    public UserAbonnementServices() {
        cnx = DBConnection.getInstance().getCnx();
    }

    public void ajouter(UserAbonnement userAbonnement) throws SQLException {
        String sql = "INSERT INTO user_abonnement (id_user, id_abonnement, date_debut, date_fin, statut, renouvellement_auto) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userAbonnement.getIdUser());
        st.setInt(2, userAbonnement.getIdAbonnement());
        st.setDate(3, Date.valueOf(userAbonnement.getDateDebut()));
        st.setDate(4, Date.valueOf(userAbonnement.getDateFin()));
        st.setString(5, userAbonnement.getStatut());
        st.setBoolean(6, userAbonnement.isRenouvellementAuto());
        st.executeUpdate();
    }

    public void modifier(UserAbonnement userAbonnement) throws SQLException {
        String sql = "UPDATE user_abonnement SET id_user=?, id_abonnement=?, date_debut=?, date_fin=?, statut=?, renouvellement_auto=? WHERE id_user_abonnement=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userAbonnement.getIdUser());
        st.setInt(2, userAbonnement.getIdAbonnement());
        st.setDate(3, Date.valueOf(userAbonnement.getDateDebut()));
        st.setDate(4, Date.valueOf(userAbonnement.getDateFin()));
        st.setString(5, userAbonnement.getStatut());
        st.setBoolean(6, userAbonnement.isRenouvellementAuto());
        st.setInt(7, userAbonnement.getIdUserAbonnement());
        st.executeUpdate();
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM user_abonnement WHERE id_user_abonnement=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        st.executeUpdate();
    }

    public List<UserAbonnement> afficher() throws SQLException {
        List<UserAbonnement> userAbonnements = new ArrayList<>();
        String sql = "SELECT * FROM user_abonnement";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            UserAbonnement ua = new UserAbonnement(
                    rs.getInt("id_user_abonnement"),
                    rs.getInt("id_user"),
                    rs.getInt("id_abonnement"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("statut"),
                    rs.getBoolean("renouvellement_auto"),
                    rs.getTimestamp("date_souscription") != null ? 
                        rs.getTimestamp("date_souscription").toLocalDateTime().toLocalDate() : null
            );
            userAbonnements.add(ua);
        }
        return userAbonnements;
    }

    public UserAbonnement getActiveByUserId(int userId) throws SQLException {
        String sql = "SELECT ua.*, a.type_abonnement, a.prix_mensuel, a.prix_annuel, a.duree " +
                     "FROM user_abonnement ua " +
                     "JOIN abonnement a ON ua.id_abonnement = a.id_abonnement " +
                     "WHERE ua.id_user = ? AND ua.statut = 'actif' " +
                     "ORDER BY ua.date_debut DESC LIMIT 1";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            UserAbonnement ua = new UserAbonnement(
                    rs.getInt("id_user_abonnement"),
                    rs.getInt("id_user"),
                    rs.getInt("id_abonnement"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("statut"),
                    rs.getBoolean("renouvellement_auto"),
                    rs.getTimestamp("date_souscription") != null ? 
                        rs.getTimestamp("date_souscription").toLocalDateTime().toLocalDate() : null
            );
            
            // Create abonnement object from joined data
            Abonnement abon = new Abonnement();
            abon.setIdAbonnement(rs.getInt("id_abonnement"));
            abon.setTypeAbonnement(rs.getString("type_abonnement"));
            abon.setPrixMensuel(rs.getBigDecimal("prix_mensuel"));
            abon.setPrixAnnuel(rs.getBigDecimal("prix_annuel"));
            abon.setDuree(rs.getString("duree"));
            
            ua.setAbonnement(abon);
            return ua;
        }
        return null;
    }

    public List<UserAbonnement> getByUserId(int userId) throws SQLException {
        List<UserAbonnement> userAbonnements = new ArrayList<>();
        String sql = "SELECT * FROM user_abonnement WHERE id_user=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            UserAbonnement ua = new UserAbonnement(
                    rs.getInt("id_user_abonnement"),
                    rs.getInt("id_user"),
                    rs.getInt("id_abonnement"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("statut"),
                    rs.getBoolean("renouvellement_auto"),
                    rs.getTimestamp("date_souscription") != null ? 
                        rs.getTimestamp("date_souscription").toLocalDateTime().toLocalDate() : null
            );
            userAbonnements.add(ua);
        }
        return userAbonnements;
    }
    
    public List<UserAbonnement> getByAbonnementId(int abonnementId) throws SQLException {
        List<UserAbonnement> userAbonnements = new ArrayList<>();
        String sql = "SELECT * FROM user_abonnement WHERE id_abonnement=? ORDER BY date_debut DESC";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, abonnementId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            UserAbonnement ua = new UserAbonnement(
                    rs.getInt("id_user_abonnement"),
                    rs.getInt("id_user"),
                    rs.getInt("id_abonnement"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getString("statut"),
                    rs.getBoolean("renouvellement_auto"),
                    rs.getTimestamp("date_souscription") != null ? 
                        rs.getTimestamp("date_souscription").toLocalDateTime().toLocalDate() : null
            );
            userAbonnements.add(ua);
        }
        return userAbonnements;
    }
}
