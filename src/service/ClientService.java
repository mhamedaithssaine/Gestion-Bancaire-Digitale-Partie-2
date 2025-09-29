package service;

import model.Client;
import repository.impl.ClientRepositoryImp;
import repository.repositoryInterface.ClientRepository;

import java.util.UUID;

public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService() {
        this.clientRepository = new ClientRepositoryImp();
    }

    public boolean createClient(String fullName, String email, String phone) {
        Client client = new Client(fullName, email, phone);
        return clientRepository.createClient(client);
    }

    public Client findById(UUID id) {
        return clientRepository.findById(id).orElse(null);
    }
}
