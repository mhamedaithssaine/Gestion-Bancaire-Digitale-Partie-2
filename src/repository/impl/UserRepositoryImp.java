package repository.impl;
import model.User;
import repository.repositoryInterface.UserRepository;
import util.ConnexionDatabase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepositoryImp implements UserRepository {

@Override
    public User authenticate (String email,String password )
{
    String sql = "SELECT  fullname, email,password, role FROM \"user\" WHERE email = ?";
    try (Connection conn = ConnexionDatabase.getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql))
    {
         stmt.setString(1,email);

         ResultSet rs = stmt.executeQuery();
         if (rs.next()){
             String storeHashedPassword = rs.getString("password");
             if (BCrypt.checkpw(password,storeHashedPassword)){
                 return new User (
                         rs.getString("fullname"),
                         rs.getString("email"),
                         "",
                         rs.getString("role")
                 );
             }

         }
    } catch (SQLException e){
        e.printStackTrace();
    }
    return null;
}
@Override
    public boolean createUser(User user)
{
    String sql = "INSERT INTO \"user\" (fullname,email,password,role) VALUES (?,?,?,?::user_role)";
    try (Connection conn = ConnexionDatabase.getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql);)
    {
        String hashedPassword = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt());
        stmt.setString(1,user.getFullName());
        stmt.setString(2, user.getEmail());
        stmt.setString(3,hashedPassword);
        stmt.setString(4,user.getRole());
        int rows = stmt.executeUpdate();
        return rows > 0 ;

    }catch (SQLException e){
        e.printStackTrace();
    }
    return false ;
}

@Override
public List<User> listeUsers(){
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM \"user\" ";
    try (Connection conn= ConnexionDatabase.getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql)){
        ResultSet rs = stmt.executeQuery();
        while (rs.next()){
            String fullname = rs.getString("fullname");
            String email = rs.getString("email");
            String password = rs.getString("password");
            String role = rs.getString("role");
            users.add(new User(fullname,email,"",role));
        }

    }catch (SQLException e ){
        e.printStackTrace();
    }
    return users;
}

@Override
    public boolean isAdmin(String email){
    String sql = "SELECT role FROM \"user\" WHERE email = ? AND role = 'ADMIN' ";
    try (Connection conn = ConnexionDatabase.getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql);)
    {
        stmt.setString(1,email);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return  false ;
}
    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE \"user\" SET fullname = ?, email = ? WHERE id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setObject(3, user.getId());
            System.out.println("Executing SQL: " + sql
                    .replaceFirst("\\?", "'" + user.getFullName() + "'")
                    .replaceFirst("\\?", "'" + user.getEmail() + "'")
                    .replaceFirst("\\?", "'" + user.getId() + "'"));
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changePassword(UUID userId, String newPassword) {
        String sql = "UPDATE \"user\" SET password = ? WHERE id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setObject(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
}
