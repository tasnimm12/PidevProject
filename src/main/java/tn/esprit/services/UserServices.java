package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServices {
    private Connection cnx;

    public UserServices() {
        cnx = DBConnection.getInstance().getCnx();
    }

    public void ajouter(User user) throws SQLException {
        String sql = "INSERT INTO users (nom, prenom, email, mot_de_passe, telephone, date_naissance, role, statut_compte) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, user.getNom());
        st.setString(2, user.getPrenom());
        st.setString(3, user.getEmail());
        st.setString(4, user.getMotDePasse());
        st.setString(5, user.getTelephone());
        st.setDate(6, user.getDateNaissance() != null ? Date.valueOf(user.getDateNaissance()) : null);
        st.setString(7, user.getRole());
        st.setString(8, user.getStatutCompte());
        st.executeUpdate();
    }

    public void modifier(User user) throws SQLException {
        String sql = "UPDATE users SET nom=?, prenom=?, email=?, mot_de_passe=?, telephone=?, date_naissance=?, role=?, statut_compte=? WHERE id_user=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, user.getNom());
        st.setString(2, user.getPrenom());
        st.setString(3, user.getEmail());
        st.setString(4, user.getMotDePasse());
        st.setString(5, user.getTelephone());
        st.setDate(6, user.getDateNaissance() != null ? Date.valueOf(user.getDateNaissance()) : null);
        st.setString(7, user.getRole());
        st.setString(8, user.getStatutCompte());
        st.setInt(9, user.getIdUser());
        st.executeUpdate();
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id_user=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        st.executeUpdate();
    }

    public List<User> afficher() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            User user = new User(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("telephone"),
                    rs.getDate("date_naissance") != null ? rs.getDate("date_naissance").toLocalDate() : null,
                    rs.getString("role"),
                    rs.getString("statut_compte")
            );
            users.add(user);
        }
        return users;
    }

    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id_user=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return new User(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("telephone"),
                    rs.getDate("date_naissance") != null ? rs.getDate("date_naissance").toLocalDate() : null,
                    rs.getString("role"),
                    rs.getString("statut_compte")
            );
        }
        return null;
    }
}
