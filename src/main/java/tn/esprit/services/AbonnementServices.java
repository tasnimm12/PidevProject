package tn.esprit.services;

import tn.esprit.config.DBConnection;
import tn.esprit.entities.Abonnement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementServices {
    private Connection cnx;

    public AbonnementServices() {
        cnx = DBConnection.getInstance().getCnx();
    }

    public void ajouter(Abonnement abonnement) throws SQLException {
        String sql = "INSERT INTO abonnement (type_abonnement, prix_mensuel, prix_annuel, duree, description, avantages, actif) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, abonnement.getTypeAbonnement());
        st.setBigDecimal(2, abonnement.getPrixMensuel());
        st.setBigDecimal(3, abonnement.getPrixAnnuel());
        st.setString(4, abonnement.getDuree());
        st.setString(5, abonnement.getDescription());
        st.setString(6, abonnement.getAvantages());
        st.setBoolean(7, abonnement.isActif());
        st.executeUpdate();
    }

    public void modifier(Abonnement abonnement) throws SQLException {
        String sql = "UPDATE abonnement SET type_abonnement=?, prix_mensuel=?, prix_annuel=?, duree=?, description=?, avantages=?, actif=? WHERE id_abonnement=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setString(1, abonnement.getTypeAbonnement());
        st.setBigDecimal(2, abonnement.getPrixMensuel());
        st.setBigDecimal(3, abonnement.getPrixAnnuel());
        st.setString(4, abonnement.getDuree());
        st.setString(5, abonnement.getDescription());
        st.setString(6, abonnement.getAvantages());
        st.setBoolean(7, abonnement.isActif());
        st.setInt(8, abonnement.getIdAbonnement());
        st.executeUpdate();
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM abonnement WHERE id_abonnement=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        st.executeUpdate();
    }

    public List<Abonnement> afficher() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT * FROM abonnement WHERE actif = 1";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Abonnement abonnement = new Abonnement(
                    rs.getInt("id_abonnement"),
                    rs.getString("type_abonnement"),
                    rs.getBigDecimal("prix_mensuel"),
                    rs.getBigDecimal("prix_annuel"),
                    rs.getString("duree"),
                    rs.getString("description"),
                    rs.getString("avantages"),
                    rs.getBoolean("actif")
            );
            abonnements.add(abonnement);
        }
        return abonnements;
    }

    public Abonnement getById(int id) throws SQLException {
        String sql = "SELECT * FROM abonnement WHERE id_abonnement=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            return new Abonnement(
                    rs.getInt("id_abonnement"),
                    rs.getString("type_abonnement"),
                    rs.getBigDecimal("prix_mensuel"),
                    rs.getBigDecimal("prix_annuel"),
                    rs.getString("duree"),
                    rs.getString("description"),
                    rs.getString("avantages"),
                    rs.getBoolean("actif")
            );
        }
        return null;
    }
}
