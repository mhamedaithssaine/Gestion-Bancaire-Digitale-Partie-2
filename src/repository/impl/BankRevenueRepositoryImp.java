package repository.impl;

import model.BankRevenue;
import repository.repositoryInterface.BankRevenueRepository;
import util.ConnexionDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BankRevenueRepositoryImp implements BankRevenueRepository {
    private static BankRevenueRepositoryImp instance;

    private BankRevenueRepositoryImp() {}

    public static BankRevenueRepositoryImp getInstance() {
        if (instance == null) {
            instance = new BankRevenueRepositoryImp();
        }
        return instance;
    }

    @Override
    public boolean save(BankRevenue revenue) {
        String sql = "INSERT INTO bank_revenue " +
                "(id, source_type, source_sub_type, amount, currency, occurred_at, transfer_id, note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, revenue.getId());
            ps.setString(2, revenue.getSourceType());
            ps.setString(3, revenue.getSourceSubType());
            ps.setBigDecimal(4, revenue.getAmount());
            ps.setString(5, revenue.getCurrency());
            ps.setTimestamp(6, Timestamp.valueOf(revenue.getOccurredAt()));
            ps.setObject(7, revenue.getTransferId());
            ps.setString(8, revenue.getNote());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<BankRevenue> findById(UUID id) {
        String sql = "SELECT * FROM bank_revenue WHERE id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToBankRevenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<BankRevenue> findAll() {
        List<BankRevenue> list = new ArrayList<>();
        String sql = "SELECT * FROM bank_revenue";
        try (Connection conn = ConnexionDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToBankRevenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<BankRevenue> findByTransferId(UUID transferId) {
        List<BankRevenue> list = new ArrayList<>();
        String sql = "SELECT * FROM bank_revenue WHERE transfer_id = ?";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, transferId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToBankRevenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private BankRevenue mapResultSetToBankRevenue(ResultSet rs) throws SQLException {
        BankRevenue revenue = new BankRevenue();
        revenue.setId((UUID) rs.getObject("id"));
        revenue.setSourceType(rs.getString("source_type"));
        revenue.setSourceSubType(rs.getString("source_sub_type"));
        revenue.setAmount(rs.getBigDecimal("amount"));
        revenue.setCurrency(rs.getString("currency"));
        revenue.setOccurredAt(rs.getTimestamp("occurred_at").toLocalDateTime());
        revenue.setTransferId((UUID) rs.getObject("transfer_id"));
        revenue.setNote(rs.getString("note"));
        return revenue;
    }
}
