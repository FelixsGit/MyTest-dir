package server.integration;

import common.AccountDTO;
import common.FileDTO;
import server.model.*;
import java.sql.*;
import java.util.*;

public class CatalogDAO {

    private Connection connection;
    private static final String ACCOUNT_TABLE_NAME = "accounts";
    private static final String FILE_TABLE_NAME = "files";
    private PreparedStatement createPersonStmt;
    private PreparedStatement verifyUserStmt;
    private PreparedStatement uploadFileStmt;
    private PreparedStatement findAllFiles;
    private PreparedStatement getFileWithNameStmt;
    private PreparedStatement updateFileStmt;
    private PreparedStatement deleteFileStmt;
    private ArrayList<FileDTO> data = new ArrayList<>();

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
    public void createAccount(Account accountToAdd) throws AccountException {
        try {
            createPersonStmt.setString(1, accountToAdd.getUsername());
            createPersonStmt.setString(2, accountToAdd.getPassword());
            createPersonStmt.executeUpdate();
        } catch(SQLException e) {
            throw new AccountException("Username already exists");
        }
    }
    public AccountDTO  verifyUser(Account account) throws AccountException{
        try{
            verifyUserStmt.setString(1, account.getUsername());
            verifyUserStmt.setString(2, account.getPassword());
            ResultSet rs = verifyUserStmt.executeQuery();
            if(!rs.next()){
                throw new AccountException("wrong username or password");
            }
            return new Account(account.getUsername(), account.getPassword());
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public void uploadFile(File file) throws FileException {
        try {
            uploadFileStmt.setString(1, file.getName());
            uploadFileStmt.setInt(2, file.getSize());
            uploadFileStmt.setString(3, file.getOwner());
            uploadFileStmt.setInt(4, file.getPermission());
            uploadFileStmt.executeUpdate();
        }catch (SQLException e){
            throw new FileException("file with that name already exists");
        }

    }
    public ArrayList<FileDTO> getAllFiles() throws FileException{
        data.clear();
        try {
            ResultSet rs = findAllFiles.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1);
                int size = rs.getInt(2);
                String owner = rs.getString(3);
                int permission = rs.getInt(4);
                File file = new File(name,size,owner,permission);
                data.add(file);
            }
            return data;
        }catch (SQLException e){
            throw new FileException("Failed to retrieve files");
        }
    }

    public FileDTO getFileWithName(String fileName) throws FileException{
        try{
            //getFileWithNameStmt.setString(1, fileName);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from "+FILE_TABLE_NAME+" WHERE name = '"+fileName+"'");
            if(!rs.next()){
                throw new FileException("no file with that name found");
            }
            String name = rs.getString(1);
            int size = rs.getInt(2);
            String  owner = rs.getString(3);
            int permission = rs.getInt(4);
            return new File(name, size, owner, permission);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void updateFile(String fileName, int newSize) throws FileException{
        try {
            updateFileStmt.setInt(1, newSize);
            updateFileStmt.setString(2, fileName);
            updateFileStmt.executeUpdate();
        }catch (SQLException e){
            throw new FileException("failed to update file");
        }
    }

    public void deleteFile(String fileName) throws FileException{
        try {
            deleteFileStmt.setString(1, fileName);
            deleteFileStmt.executeUpdate();
        }catch (SQLException e){
            throw new FileException("failed to delete file");
        }
    }

    private void preparedStatements() throws SQLException{
        createPersonStmt = connection.prepareStatement("INSERT INTO "+ACCOUNT_TABLE_NAME +" (username,password)  VALUES (?, ?)");
        verifyUserStmt = connection.prepareStatement("SELECT * from "+ ACCOUNT_TABLE_NAME +" WHERE username = ? AND password = ?");
        uploadFileStmt = connection.prepareStatement("INSERT INTO "+FILE_TABLE_NAME+" (name,size,owner,permission) VALUES (?,?,?,?)");
        findAllFiles = connection.prepareStatement("SELECT * from "+FILE_TABLE_NAME);
        //getFileWithNameStmt = connection.prepareStatement("SELECT * from "+FILE_TABLE_NAME+" WHERE name = ?"); TODO
        updateFileStmt = connection.prepareStatement("UPDATE "+FILE_TABLE_NAME+" SET size = ? WHERE name = ?");
        deleteFileStmt = connection.prepareStatement("DELETE FROM "+FILE_TABLE_NAME +" WHERE name = ?");
    }
}
