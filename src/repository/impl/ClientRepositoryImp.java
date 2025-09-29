package repository.impl;

import model.Client;
import repository.repositoryInterface.ClientRepository;
import util.ConnexionDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class ClientRepositoryImp implements ClientRepository {
    @Override
    public boolean createClient(Client client) {
        String sql = "INSERT INTO client (full_name, email, phone) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getFullName());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getPhone());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                client.setId(UUID.fromString(rs.getString("id")));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
}
