package server.integration;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
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
    private PreparedStatement uploadFileStmt;
    private PreparedStatement findAllFiles;
    private PreparedStatement getUserFromIDStmt;
    private ArrayList<String> data = new ArrayList<>();
    private HashMap<Integer, String> userMap = new HashMap<>();

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
            Set set = userMap.entrySet();
            Iterator iterator = set.iterator();
            System.out.println("List of ONLINE users");
            while(iterator.hasNext()) {
                Map.Entry mapEntry = (Map.Entry)iterator.next();
                System.out.println("user: "+ mapEntry.getValue()+ " with ID: "+ mapEntry.getKey());
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
            userMap.put(id, account.getUsername());
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
            /*
            createFileStmt.setString(1,name);
            createFileStmt.setInt(2, size);
            createFileStmt.setInt(3, owner);
            createFileStmt.setInt(4, permission);
            createPersonStmt.executeUpdate();
            */
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

    private String getUserFromID(int id){
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


    public void logoutUser(int id){
        userMap.remove(id);
        System.out.println("user with id: "+ id +" logged out.");
    }

    private void preparedStatements() throws SQLException{
        createPersonStmt = connection.prepareStatement("INSERT INTO "+ACCOUNT_TABLE_NAME +" (username,password)  VALUES (?, ?)");
        findAllPersonsStmt = connection.prepareStatement("SELECT * from "+ ACCOUNT_TABLE_NAME );
        verifyUserStmt = connection.prepareStatement("SELECT ID from "+ ACCOUNT_TABLE_NAME +" WHERE username = ? AND password = ?");
        getUserFromIDStmt = connection.prepareStatement("SELECT username from "+ ACCOUNT_TABLE_NAME +" WHERE id = ?");
        uploadFileStmt = connection.prepareStatement("INSERT INTO "+FILE_TABLE_NAME+" (name,size,owner,permission) VALUES (?,?,?,?)");
        findAllFiles = connection.prepareStatement("SELECT * from "+FILE_TABLE_NAME);
    }
}
