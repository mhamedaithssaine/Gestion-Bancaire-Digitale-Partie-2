package repository.impl;

import model.Transaction;
import model.type.TransactionStatus;
import model.type.TransactionType;
import repository.repositoryInterface.TransactionRepository;
import util.ConnexionDatabase;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionRepositoryImp implements TransactionRepository {

private static TransactionRepositoryImp instance;
private  TransactionRepositoryImp () {}


    public static TransactionRepositoryImp getInstance(){
        if (instance == null ){
            return instance = new TransactionRepositoryImp();
        }
        return instance ;

    }

    @Override
    public boolean save (Transaction transaction ){
        String sql = "INSERT INTO transactions (id, account_id, created_by, transaction_type, amount, created_at, status) " +
                "VALUES (?, ?, ?, ?::transaction_type, ?, ?, ?::transaction_status)";

        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, transaction.getId());
            ps.setString(2, transaction.getAccountId());
            ps.setObject(3, transaction.getCreatedBy());
            ps.setString(4, transaction.getTransactionType().name());
            ps.setBigDecimal(5, transaction.getAmount());
            ps.setTimestamp(6, Timestamp.valueOf(transaction.getCreatedAt()));
            ps.setString(7, transaction.getStatus().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
}

@Override
public Optional<Transaction> findById(UUID id) {
    String sql = "SELECT * FROM transactions WHERE id = ?";
    try (Connection conn = ConnexionDatabase.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setObject(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return Optional.of(mapResultSetToTransaction(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return Optional.empty();
}

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC";

        try (Connection conn = ConnexionDatabase.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByAccountId(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";

        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByUserId(UUID userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE created_by = ? ORDER BY created_at DESC";

        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE transaction_type = ? ORDER BY created_at DESC";

        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";

        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByAccountAndDateRange(String accountId, LocalDateTime start, LocalDateTime end) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? AND created_at BETWEEN ? AND ? ORDER BY created_at DESC";

        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public BigDecimal getTotalBalance() {
        String sql = "SELECT SUM(amount) AS total FROM transactions";
        try (Connection conn = ConnexionDatabase.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }


    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId((UUID) rs.getObject("id"));
        t.setAccountId(rs.getString("account_id"));
        t.setCreatedBy((UUID) rs.getObject("created_by"));
        t.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        t.setStatus(TransactionStatus.valueOf(rs.getString("status")));
        return t;
    }
}
