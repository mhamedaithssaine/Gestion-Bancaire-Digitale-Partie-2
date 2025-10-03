package repository.impl;

import model.Client;
import model.Credit;
import repository.repositoryInterface.CreditRepository;
import util.ConnexionDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreditRepositoryImp implements CreditRepository {

    @Override
    public boolean createCredit(Credit credit) {
        String sql = "INSERT INTO credit (id, client_id, amount, duration_months, interest_rate, income_proof, submission_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnexionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, credit.getId());
            stmt.setObject(2, credit.getClientId());
            stmt.setBigDecimal(3, credit.getAmount());
            stmt.setInt(4, credit.getDurationMonths());
            stmt.setBigDecimal(5, credit.getInterestRate());
            stmt.setString(6, credit.getIncomeProof());
            stmt.setObject(7, credit.getSubmissionDate());
            stmt.setString(8, credit.getStatus());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
