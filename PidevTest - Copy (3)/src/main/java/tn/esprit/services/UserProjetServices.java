package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.UserProjet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserProjetServices {
    private Connection cnx;

    public UserProjetServices() {
        cnx = DBConnection.getInstance().getCnx();
    }

    public void ajouter(UserProjet userProjet) throws SQLException {
        String sql = "INSERT INTO user_projet (user_id, projet_id) VALUES (?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userProjet.getUserId());
        st.setInt(2, userProjet.getProjetId());
        st.executeUpdate();
    }

    public void supprimer(int userId, int projetId) throws SQLException {
        String sql = "DELETE FROM user_projet WHERE user_id=? AND projet_id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userId);
        st.setInt(2, projetId);
        st.executeUpdate();
    }

    public List<UserProjet> afficher() throws SQLException {
        List<UserProjet> userProjets = new ArrayList<>();
        String sql = "SELECT * FROM user_projet";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            UserProjet userProjet = new UserProjet(
                    rs.getInt("user_id"),
                    rs.getInt("projet_id")
            );
            userProjets.add(userProjet);
        }
        return userProjets;
    }

    public List<UserProjet> getByUserId(int userId) throws SQLException {
        List<UserProjet> userProjets = new ArrayList<>();
        String sql = "SELECT * FROM user_projet WHERE user_id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            UserProjet userProjet = new UserProjet(
                    rs.getInt("user_id"),
                    rs.getInt("projet_id")
            );
            userProjets.add(userProjet);
        }
        return userProjets;
    }

    public List<UserProjet> getByProjetId(int projetId) throws SQLException {
        List<UserProjet> userProjets = new ArrayList<>();
        String sql = "SELECT * FROM user_projet WHERE projet_id=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, projetId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            UserProjet userProjet = new UserProjet(
                    rs.getInt("user_id"),
                    rs.getInt("projet_id")
            );
            userProjets.add(userProjet);
        }
        return userProjets;
    }
}
