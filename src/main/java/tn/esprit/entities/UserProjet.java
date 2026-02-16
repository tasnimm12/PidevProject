package tn.esprit.entities;

public class UserProjet {
    private int userId;
    private int projetId;

    public UserProjet() {
    }

    public UserProjet(int userId, int projetId) {
        this.userId = userId;
        this.projetId = projetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProjetId() {
        return projetId;
    }

    public void setProjetId(int projetId) {
        this.projetId = projetId;
    }

    @Override
    public String toString() {
        return "UserProjet{" +
                "userId=" + userId +
                ", projetId=" + projetId +
                '}';
    }
}
