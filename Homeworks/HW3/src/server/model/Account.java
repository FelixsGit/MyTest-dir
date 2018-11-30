package server.model;

import common.AccountDTO;

public class Account implements AccountDTO {

    private String username;
    private String password;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
}
