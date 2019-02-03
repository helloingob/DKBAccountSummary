package com.helloingob.accsum.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.helloingob.accsum.data.DatabaseConnection;
import com.helloingob.accsum.data.to.ApplicantTO;
import com.helloingob.accsum.data.to.PostingTO;
import com.helloingob.accsum.data.to.TransferTO;

public class TransferDAO {

    public static List<TransferTO> get(LocalDate from, LocalDate to) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<TransferTO> activities = new ArrayList<TransferTO>();
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                       .append(" * ")
                   .append(" FROM ")
                       .append(" transfer ")
                   .append(" JOIN posting ON ")
                       .append(" transfer.postingtext_fk = posting.pk ")
                   .append(" JOIN applicant ON ")
                       .append(" transfer.applicant_fk = applicant.pk ")
                   .append(" WHERE ")
                       .append(" date BETWEEN ? AND ? ")
                   .append(" ORDER BY date DESC, amount ASC  ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(to.atStartOfDay()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                activities.add(constructObject(resultSet));
            }
            return activities;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return Collections.emptyList();
    }

    public static List<TransferTO> getWithNoTagSet() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<TransferTO> activities = new ArrayList<TransferTO>();
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                     .append(" * ")
                 .append(" FROM ")
                     .append(" transfer ")
                 .append(" JOIN posting ON ")
                     .append(" transfer.postingtext_fk = posting.pk ")
                 .append(" JOIN applicant ON ")
                     .append(" transfer.applicant_fk = applicant.pk ")
                 .append(" WHERE ")
                     .append(" transfer.pk NOT IN( ")
                         .append(" SELECT ")
                             .append(" transfer_fk ")
                         .append(" FROM ")
                             .append(" transfer_tag ")
                         .append(" WHERE ")
                             .append(" transfer_fk = transfer.pk ")
                     .append(" ) ")
                 .append(" ORDER BY ")
                     .append(" DATE DESC, ")
                     .append(" amount ASC ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                activities.add(constructObject(resultSet));
            }
            return activities;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return Collections.emptyList();
    }

    private static TransferTO constructObject(ResultSet resultSet) {
        TransferTO transfer = new TransferTO();

        try {
            transfer.setPk(resultSet.getInt("pk"));
            transfer.setDate(resultSet.getTimestamp("date").toLocalDateTime().toLocalDate());

            PostingTO posting = new PostingTO();
            posting.setPk(resultSet.getInt("postingtext_fk"));
            posting.setText(resultSet.getString("text"));
            transfer.setPosting(posting);

            ApplicantTO applicant = new ApplicantTO();
            applicant.setPk(resultSet.getInt("applicant_fk"));
            applicant.setName(resultSet.getString("name"));
            applicant.setIban(resultSet.getString("iban"));
            applicant.setBic(resultSet.getString("bic"));
            transfer.setApplicant(applicant);

            transfer.setReasonForPayment(resultSet.getString("reasonforpayment"));
            transfer.setAmount(resultSet.getDouble("amount"));
            transfer.setComment(resultSet.getString("comment"));

            TagDAO.fill(transfer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transfer;
    }

    public static boolean update(TransferTO transfer) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" UPDATE ")
                     .append(" transfer ")
                 .append(" SET ")
                     .append(" comment = ? ")
                 .append(" WHERE ")
                     .append(" pk =? ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, transfer.getComment());
            preparedStatement.setInt(2, transfer.getPk());
            if (preparedStatement.executeUpdate() > 0) {
                if (TagDAO.delete(transfer)) {
                    return TagDAO.add(transfer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return false;
    }

    public static boolean insert(TransferTO transfer) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" INSERT ")
                  .append(" INTO ")
                      .append(" transfer( ")
                          .append(" date, ")
                          .append(" postingtext_fk, ")
                          .append(" applicant_fk, ")
                          .append(" reasonforpayment, ")
                          .append(" amount ")
                      .append(" ) ")
                  .append(" VALUES( ")
                      .append(" ?, ")
                      .append(" ?, ")
                      .append(" ?, ")
                      .append(" ?, ")
                      .append(" ? ")
                  .append(" ) ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(transfer.getDate().atStartOfDay()));
            preparedStatement.setInt(2, transfer.getPosting().getPk());
            preparedStatement.setInt(3, transfer.getApplicant().getPk());
            preparedStatement.setString(4, transfer.getReasonForPayment());
            preparedStatement.setDouble(5, transfer.getAmount());
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                transfer.setPk(rs.getInt(1));

                //add tags
                TagDAO.add(transfer);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return false;
    }

    public static boolean isDublicate(TransferTO transfer) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                      .append(" TRUE ")
                  .append(" FROM ")
                      .append(" transfer ")
                  .append(" JOIN posting ON ")
                      .append(" transfer.postingtext_fk = posting.pk ")
                  .append(" JOIN applicant ON ")
                      .append(" transfer.applicant_fk = applicant.pk ")
                  .append(" WHERE ")
                      .append(" date = ? ")
                      .append(" AND posting.text = ? ")
                      .append(" AND applicant.iban = ? ")
                      .append(" AND reasonforpayment = ? ")
                      .append(" AND amount = ? ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setTimestamp(1, Timestamp.valueOf(transfer.getDate().atStartOfDay()));
            preparedStatement.setString(2, transfer.getPosting().getText());
            preparedStatement.setString(3, transfer.getApplicant().getIban());
            preparedStatement.setString(4, transfer.getReasonForPayment());
            preparedStatement.setDouble(5, transfer.getAmount());
            return preparedStatement.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return true;
    }

}
