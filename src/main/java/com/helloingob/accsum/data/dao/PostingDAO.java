package com.helloingob.accsum.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.helloingob.accsum.data.DatabaseConnection;
import com.helloingob.accsum.data.to.PostingTO;

public class PostingDAO {

    public static Map<String, PostingTO> get() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, PostingTO> postings = new HashMap<String, PostingTO>();
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                   .append(" * ")
               .append(" FROM ")
                   .append(" posting ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PostingTO posting = new PostingTO();

                posting.setPk(resultSet.getInt("pk"));
                posting.setText(resultSet.getString("text"));

                postings.put(posting.getText(), posting);
            }
            return postings;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return Collections.emptyMap();
    }

    public static boolean insert(PostingTO posting) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" INSERT ")
                   .append(" INTO ")
                       .append(" posting(text) ")
                   .append(" VALUES(?) ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, posting.getText());
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                posting.setPk(rs.getInt(1));
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
