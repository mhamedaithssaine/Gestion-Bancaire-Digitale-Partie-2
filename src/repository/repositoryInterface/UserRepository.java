package repository.repositoryInterface;

import model.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    User authenticate(String email, String password);
    boolean createUser(User user);
    boolean isAdmin(String email);
     List<User> listeUsers();
     boolean updateUser(User user);
    boolean changePassword(UUID userId, String newPassword);
}
