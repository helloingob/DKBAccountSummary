package com.helloingob.accsum.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.helloingob.accsum.data.DatabaseConnection;
import com.helloingob.accsum.data.to.ApplicantTO;

public class ApplicantDAO {

    public static Map<String, ApplicantTO> get() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, ApplicantTO> applicants = new HashMap<String, ApplicantTO>();
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                   .append(" * ")
               .append(" FROM ")
                   .append(" applicant ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ApplicantTO applicant = new ApplicantTO();

                applicant.setPk(resultSet.getInt("pk"));
                applicant.setName(resultSet.getString("name"));
                applicant.setIban(resultSet.getString("iban"));
                applicant.setBic(resultSet.getString("bic"));

                applicants.put(applicant.getIban(), applicant);
            }
            return applicants;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return Collections.emptyMap();
    }

    public static boolean insert(ApplicantTO applicant) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" INSERT ")
                   .append(" INTO ")
                       .append(" applicant( ")
                           .append(" name, ")
                           .append(" iban, ")
                           .append(" bic ")
                       .append(" ) ")
                   .append(" VALUES( ")
                       .append(" ?, ")
                       .append(" ?, ")
                       .append(" ? ")
                   .append(" ) ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, applicant.getName());
            preparedStatement.setString(2, applicant.getIban());
            preparedStatement.setString(3, applicant.getBic());
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                applicant.setPk(rs.getInt(1));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return false;
    }

}
