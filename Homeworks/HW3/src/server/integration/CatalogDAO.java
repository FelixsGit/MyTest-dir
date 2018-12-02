package server.integration;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import common.FileDTO;
import common.MsgContainerDTO;
import server.model.Account;
import server.model.File;
import server.model.MsgContainer;
import java.sql.*;
import java.util.*;

public class CatalogDAO {

    private Connection connection;
    private static final String ACCOUNT_TABLE_NAME = "accounts";
    private static final String FILE_TABLE_NAME = "files";
    private PreparedStatement createPersonStmt;
    private PreparedStatement findAllPersonsStmt;
    private PreparedStatement verifyUserStmt;
    private PreparedStatement findAllFiles;
    private PreparedStatement getUserFromIDStmt;
    private PreparedStatement getFileWithIDStmt;
    private PreparedStatement getIDFromUserStmt;
    private PreparedStatement updateFileStmt;
    private PreparedStatement deleteFileStmt;
    private ArrayList<String> data = new ArrayList<>();

    public CatalogDAO(){
        connectToDatabase();
    }

    private void connectToDatabase(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/catalogdb","Felix Toppar","123");
            preparedStatements();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public MsgContainerDTO createAccount(Account account){
        data.clear();
        try {
            createPersonStmt.setString(1, account.getUsername());
            createPersonStmt.setString(2, account.getPassword());
            createPersonStmt.executeUpdate();
        }catch (SQLException e){
            return new MsgContainer(null,"TAKEN");
        }
        return new MsgContainer(null, "OK");
    }

    public MsgContainerDTO getAllUsers() {
        data.clear();
        try {
            ResultSet users = findAllPersonsStmt.executeQuery();
            while (users.next()) {
                int id = users.getInt(1);
                String username = users.getString(2);
                String password = users.getString(3);
                data.add("id = "+id+",  username = "+username+",  "+password);
            }
            return new MsgContainer(data, "OK");
        }catch (SQLException e){
            return new MsgContainer(null, "FAILED");
        }
    }

    public MsgContainerDTO verifyUser(Account account){
        data.clear();
        try{
            verifyUserStmt.setString(1, account.getUsername());
            verifyUserStmt.setString(2, account.getPassword());
            ResultSet validInfo = verifyUserStmt.executeQuery();
            validInfo.next();
            int id = validInfo.getInt(1);
            data.add(""+id+"");
            return new MsgContainer(data, "VALID");
        }catch (SQLException e){
            return new MsgContainer(null, "NOTVALID");
        }
    }

    public MsgContainerDTO getAllFiles(){
        data.clear();
        try {
            ResultSet users = findAllFiles.executeQuery();
            while (users.next()) {
                String name = users.getString(1);
                int size = users.getInt(2);
                String owner = users.getString(3);
                int permission = users.getInt(4);
                int id = users.getInt(5);
                data.add(("id = "+id+", "+"name = " + name + ",  size = " + size + ",  owner =  " + owner+ ", permission = "+permission));
            }
            return new MsgContainer(data,"OK");
        }catch (SQLException e){
            return new MsgContainer(null, "FAILED");
        }
    }

    public MsgContainerDTO uploadFile(File file){
        data.clear();
        try {
            String name = file.getName();
            int size = file.getSize();
            String owner = getUserFromID(file.getOwner());
            int permission = file.writePermission();
            Statement stmt = connection.createStatement();
            String sql = "INSERT INTO " + FILE_TABLE_NAME + " (name,size,owner,permission) VALUES ('" + name + "','" + size + " ','" + owner + "','" + permission + "')";
            stmt.executeUpdate(sql);
        }catch (MySQLIntegrityConstraintViolationException e){
            return  new MsgContainer(null, "NAMEERROR");
        }catch (SQLException e){
            e.printStackTrace();
            return new MsgContainer(null,"ERROR");
        }
        return new MsgContainer(null, "OK");

    }

    public void updateFile(String fileName, int newSize){
        try {
            updateFileStmt.setInt(1, newSize);
            updateFileStmt.setString(2, fileName);
            updateFileStmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private int getIDFromUser(String username){
        try{
            getIDFromUserStmt.setString(1, username);
            ResultSet user = getIDFromUserStmt.executeQuery();
            user.next();
            int id  = user.getInt(1);
            return id;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public String getUserFromID(int id){
        try{
            getUserFromIDStmt.setInt(1, id);
            ResultSet user = getUserFromIDStmt.executeQuery();
            user.next();
            String username = user.getString(1);
            return username;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void deleteFile(String fileName){
        try {
            deleteFileStmt.setString(1, fileName);
            deleteFileStmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public FileDTO downloadFileWithID(int id){
        try{
            getFileWithIDStmt.setInt(1, id);
            ResultSet file = getFileWithIDStmt.executeQuery();
            file.next();
            String name = file.getString(1);
            int size = file.getInt(2);
            int owner = getIDFromUser(file.getString(3));
            int permission = file.getInt(4);
            return new File(name, size, owner, permission);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    private void preparedStatements() throws SQLException{
        createPersonStmt = connection.prepareStatement("INSERT INTO "+ACCOUNT_TABLE_NAME +" (username,password)  VALUES (?, ?)");
        findAllPersonsStmt = connection.prepareStatement("SELECT * from "+ ACCOUNT_TABLE_NAME );
        verifyUserStmt = connection.prepareStatement("SELECT ID from "+ ACCOUNT_TABLE_NAME +" WHERE username = ? AND password = ?");
        getUserFromIDStmt = connection.prepareStatement("SELECT username from "+ ACCOUNT_TABLE_NAME +" WHERE id = ?");
        //uploadFileStmt = connection.prepareStatement("INSERT INTO "+FILE_TABLE_NAME+" (name,size,owner,permission) VALUES (?,?,?,?)");
        findAllFiles = connection.prepareStatement("SELECT * from "+FILE_TABLE_NAME);
        getFileWithIDStmt = connection.prepareStatement("SELECT * from "+FILE_TABLE_NAME+" WHERE ID = ?");
        getIDFromUserStmt = connection.prepareStatement("SELECT ID from "+ACCOUNT_TABLE_NAME+ " WHERE username = ?");
        updateFileStmt = connection.prepareStatement("UPDATE "+FILE_TABLE_NAME+" SET size = ? WHERE name = ?");
        deleteFileStmt = connection.prepareStatement("DELETE FROM "+FILE_TABLE_NAME +" WHERE name = ?");
    }
}
