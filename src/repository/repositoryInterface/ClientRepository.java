package repository.repositoryInterface;

import model.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    boolean createClient(Client client);
    Optional<Client> findById(UUID id);
}
