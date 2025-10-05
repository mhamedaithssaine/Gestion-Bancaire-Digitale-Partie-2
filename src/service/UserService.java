package service;

import model.User;
import repository.repositoryInterface.UserRepository;
import repository.impl.UserRepositoryImp;

import java.util.List;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;

    public UserService(){
        this.userRepository = new UserRepositoryImp();

    }
    public boolean createUser (User user, String adminEmail)
    {
        if (userRepository.isAdmin(adminEmail)){
            return userRepository.createUser(user);
        }
        return false ;
    }

    public void listeUsers(String adminEmail){
        if (userRepository.isAdmin(adminEmail)){
            List<User> users = userRepository.listeUsers();
            for (User user : users){
                System.out.println("User :" + user.getFullName() + " , Email :"+user.getEmail()+ " ,Role :" + user.getRole());
            }
        }else {
            System.out.println("Access denied: You are not an administrator.");
        }
    }
    public boolean updateProfile(UUID userId, String fullName, String email) {
        User user = new User(fullName, email, null, null);
        user.setId(userId);
        return userRepository.updateUser(user);
    }

    public boolean changePassword(UUID userId, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            System.out.println("Password cannot be empty.");
            return false;
        }
        return userRepository.changePassword(userId, newPassword);
    }

}
