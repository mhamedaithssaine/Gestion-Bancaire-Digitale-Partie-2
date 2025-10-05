package repository.impl;

import model.Credit;
import repository.repositoryInterface.CreditRepository;
import util.ConnexionDatabase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreditRepositoryImp implements CreditRepository {

    private static CreditRepositoryImp instance;

    private CreditRepositoryImp() {}

    public static CreditRepositoryImp getInstance() {
        if (instance == null) {
            instance = new CreditRepositoryImp();
        }
        return instance;
    }

    @Override
    public boolean createCredit(Credit credit) {
        String sql = "INSERT INTO \"credits\" (id, client_id, account_id, amount, currency, requested_at, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, credit.getId());
            stmt.setObject(2, credit.getClientId());
            stmt.setString(3, credit.getAccountId());
            stmt.setBigDecimal(4, credit.getAmount());
            stmt.setString(5, credit.getCurrency());
            stmt.setObject(6, credit.getRequestedAt());
            stmt.setString(7, credit.getStatus());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error creating credit: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Credit findById(UUID creditId) {
        String sql = "SELECT * FROM \"credits\" WHERE id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, creditId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Credit credit = new Credit();
                credit.setId(UUID.fromString(rs.getString("id")));
                credit.setClientId(UUID.fromString(rs.getString("client_id")));
                credit.setAccountId(rs.getString("account_id"));
                credit.setAmount(rs.getBigDecimal("amount"));
                credit.setCurrency(rs.getString("currency"));
                credit.setRequestedAt(rs.getObject("requested_at", LocalDateTime.class));
                credit.setStatus(rs.getString("status"));
                return credit;
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Error finding credit: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Credit> findByClientId(UUID clientId) {
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM \"credits\" WHERE client_id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Credit credit = new Credit();
                credit.setId(UUID.fromString(rs.getString("id")));
                credit.setClientId(UUID.fromString(rs.getString("client_id")));
                credit.setAccountId(rs.getString("account_id"));
                credit.setAmount(rs.getBigDecimal("amount"));
                credit.setCurrency(rs.getString("currency"));
                credit.setRequestedAt(rs.getObject("requested_at", LocalDateTime.class));
                credit.setStatus(rs.getString("status"));
                credits.add(credit);
            }
            return credits;
        } catch (SQLException e) {
            System.out.println("Error finding credits by client: " + e.getMessage());
            return credits;
        }
    }

    @Override
    public boolean repayCredit(UUID creditId, BigDecimal amount) {
        String sql = "UPDATE \"credits\" SET amount = amount - ? WHERE id = ? AND status = 'APPROVED'";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setObject(2, creditId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error repaying credit: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean approveOrRejectCredit(UUID creditId, String status) {
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            System.out.println("Invalid status. Must be APPROVED or REJECTED.");
            return false;
        }
        String sql = "UPDATE \"credits\" SET status = ? WHERE id = ? AND status = 'PENDING'";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setObject(2, creditId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Cannot update credit: Either credit not found or not in PENDING status.");
                return false;
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating credit status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Credit> findAll() {
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM \"credits\"";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Credit credit = new Credit();
                credit.setId(UUID.fromString(rs.getString("id")));
                credit.setClientId(UUID.fromString(rs.getString("client_id")));
                credit.setAccountId(rs.getString("account_id"));
                credit.setAmount(rs.getBigDecimal("amount"));
                credit.setCurrency(rs.getString("currency"));
                credit.setRequestedAt(rs.getObject("requested_at", LocalDateTime.class));
                credit.setStatus(rs.getString("status"));
                credits.add(credit);
            }
            return credits;
        } catch (SQLException e) {
            System.out.println("Error fetching all credits: " + e.getMessage());
            return credits;
        }
    }
}