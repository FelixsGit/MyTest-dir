package server.integration;

import server.model.Account;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CatalogDAO {

    private Connection con;

    public CatalogDAO(){
        connection();
    }

    private void connection(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:80/catalogDB","Felix Toppar","123");
        }catch(Exception e){
            System.out.println(e);
        }
    }


    public void createAccount(Account account){
        try {
            Statement stmt = con.createStatement();
            String sqlQuery = "Insert INTO account (username, password) VALUES (" + account.getUsername() + "," + account.getPassword() + ")";
            stmt.executeQuery(sqlQuery);
        }catch (SQLException e){
            System.err.println("Error: Failed to insert");
        }
    }
}
