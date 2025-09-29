package repository.impl;

import model.Account;
import model.type.AccountType;
import repository.repositoryInterface.AccountRepository;
import util.ConnexionDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class AccountRepositoryImp implements AccountRepository
{
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
        }
        return false;
    }

    @Override
    public Optional<Account> findByAccountId(String accountId) {
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
                       UUID.fromString( rs.getString("client_id"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private String generateAccountId() {
        return "BK-" + java.time.Year.now().getValue() + "-" + String.format("%04d", new Random().nextInt(10000));
    }
}
