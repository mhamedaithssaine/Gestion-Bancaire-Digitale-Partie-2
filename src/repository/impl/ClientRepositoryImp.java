package repository.impl;

import model.Client;
import repository.repositoryInterface.ClientRepository;
import util.ConnexionDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientRepositoryImp implements ClientRepository {
    @Override
    public boolean createClient(Client client) {
        String sql = "INSERT INTO client (id ,full_name, email, phone) VALUES (?, ?, ?,?)";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1,client.getId());
            stmt.setString(2, client.getFullName());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getPhone());
            int rows = stmt.executeUpdate();
                return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Optional<Client> findById(UUID id) {
        String sql = "SELECT id, full_name, email, phone FROM client WHERE id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(
                        new Client(
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Client> listClientsWithAccounts() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT DISTINCT c.id, c.full_name, c.email, c.phone " +
                "FROM client c " +
                "JOIN account a ON c.id = a.client_id";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clients.add(new Client(
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                ) {
                    { setId((UUID) rs.getObject("id")); } // Initialisation du champ id via un bloc d'initialisation
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
}
