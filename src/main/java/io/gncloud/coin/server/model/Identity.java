package io.gncloud.coin.server.model;

public class Identity {
    private String userId;
    private String password;
    private String email;
    private String session;
    private boolean isSeller;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public boolean isSeller() {
        return isSeller;
    }

    public void setSeller(boolean seller) {
        isSeller = seller;
    }

    @Override
    public String toString() {
        return "Identity{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", session='" + session + '\'' +
                '}';
    }
}
