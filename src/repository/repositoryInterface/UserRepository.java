package repository.repositoryInterface;

import model.User;

import java.util.List;

public interface UserRepository {
    User authenticate(String email, String password);
    boolean createUser(User user);
    boolean isAdmin(String email);
     List<User> listeUsers();
}
