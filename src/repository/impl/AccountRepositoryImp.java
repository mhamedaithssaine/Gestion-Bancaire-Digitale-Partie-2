package repository.impl;

import model.Account;
import model.type.AccountType;
import repository.repositoryInterface.AccountRepository;
import util.ConnexionDatabase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AccountRepositoryImp implements AccountRepository {
    @Override
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO account (account_id, type, balance, currency, client_id) VALUES (?, ?::account_type, ?, ?, ?)";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, generateAccountId());
            stmt.setString(2, account.getType().name());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.setString(4, account.getCurrency());
            stmt.setObject(5, account.getClientId());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Optional<Account> findAccountById(String accountId) {
        String sql = "SELECT account_id, type, balance, currency, client_id FROM account WHERE account_id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Account(
                        rs.getString("account_id"),
                        AccountType.valueOf(rs.getString("type")),
                        rs.getBigDecimal("balance"),
                        rs.getString("currency"),
                        (UUID) rs.getObject("client_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    @Override
    public List<Account> listAccountsForClient(UUID clientId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, type, balance, currency, client_id FROM account WHERE client_id = ? AND is_active = true";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accounts.add(new Account(
                        rs.getString("account_id"),
                        AccountType.valueOf(rs.getString("type")),
                        rs.getBigDecimal("balance"),
                        rs.getString("currency"),
                        (UUID) rs.getObject("client_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public boolean updateAccount(Account account) {
        String sql = "UPDATE account SET balance = ?, type = ?::account_type, currency = ?, client_id = ? WHERE account_id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, account.getBalance());
            stmt.setString(2, account.getType().name());
            stmt.setString(3, account.getCurrency());
            stmt.setObject(4, account.getClientId());
            stmt.setString(5, account.getAccountId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

@Override
public boolean closeAccount(String accountId) {
    String sql = "UPDATE \"account\" SET is_active = false WHERE account_id = ? AND balance = 0";
    try (Connection conn = ConnexionDatabase.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, accountId);
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected == 0) {
            System.out.println("Cannot close account: Either account not found or balance is not zero.");
            return false;
        }
        return true;
    } catch (SQLException e) {
        System.out.println("Error closing account: " + e.getMessage());
        return false;
    }
}

    @Override
    public boolean updateAccountStatus(String accountId, boolean isActive) {
        String sql = "UPDATE \"accounts\" SET is_active = ? WHERE account_id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setString(2, accountId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating account status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hasAccount(UUID clientId) {
        return !listAccountsForClient(clientId).isEmpty();
    }


    private String generateAccountId() {
        return "BK-" + java.time.Year.now().getValue() + "-" + String.format("%04d", new Random().nextInt(10000));
    }
}
