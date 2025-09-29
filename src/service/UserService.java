package service;

import model.User;
import repository.repositoryInterface.UserRepository;
import repository.impl.UserRepositoryImp;

import java.util.List;

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
}
