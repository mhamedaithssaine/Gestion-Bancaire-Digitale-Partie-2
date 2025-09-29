package service;

import model.User;
import repository.repositoryInterface.UserRepository;
import repository.impl.UserRepositoryImp;

public class AuthService {
    private final UserRepository userRepository;
    public AuthService(){
        this.userRepository = new UserRepositoryImp();
    }

    public User authenticate (String email , String password)
    {
        return userRepository.authenticate(email,password);
    }



}
