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
import java.util.Map;
import java.util.TreeMap;

import com.helloingob.accsum.data.DatabaseConnection;
import com.helloingob.accsum.data.to.TagTO;
import com.helloingob.accsum.data.to.TransferTO;

public class TagDAO {

    public static boolean insert(TagTO tag) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" INSERT ")
                   .append(" INTO ")
                       .append(" tag(title,visible) ")
                   .append(" VALUES(?, ?) ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, tag.getTitle());
            preparedStatement.setBoolean(2, tag.isVisible());
            int manipulatedRows = preparedStatement.executeUpdate();
            if (manipulatedRows > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                tag.setPk(rs.getInt(1));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return false;
    }

    public static boolean update(TagTO tag) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" UPDATE ")
                     .append(" tag ")
                 .append(" SET ")
                     .append(" title =?, ")
                     .append(" visible =? ")
                 .append(" WHERE ")
                     .append(" pk =? ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, tag.getTitle());
            preparedStatement.setBoolean(2, tag.isVisible());
            preparedStatement.setInt(3, tag.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return false;
    }

    public static boolean add(TransferTO transfer) {
        for (TagTO tag : transfer.getTags()) {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            Connection connection = null;

            try {
                connection = DatabaseConnection.getInstance().getConnection();
                StringBuilder query = new StringBuilder();
                // @formatter:off
                query.append(" INSERT ")
                       .append(" INTO ")
                           .append(" transfer_tag( ")
                               .append(" transfer_fk, ")
                               .append(" tag_fk ")
                           .append(" ) ")
                       .append(" VALUES( ")
                           .append(" ?, ")
                           .append(" ? ")
                       .append(" ) ");
                // @formatter:on
                preparedStatement = connection.prepareStatement(query.toString());
                preparedStatement.setInt(1, transfer.getPk());
                preparedStatement.setInt(2, tag.getPk());
                if (preparedStatement.executeUpdate() <= 0) {
                    return false;
                }
            } catch (Exception e) {
                System.err.println(transfer);
                e.printStackTrace();
            } finally {
                DatabaseConnection.close(preparedStatement, resultSet);
            }
        }
        return true;
    }

    public static boolean isUsed(TagTO tag) {
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
                      .append(" transfer_tag ")
                  .append(" WHERE ")
                      .append(" tag_fk = ? ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setInt(1, tag.getPk());
            return preparedStatement.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return true;
    }

    public static boolean delete(TagTO tag) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" DELETE ")
                 .append(" FROM ")
                     .append(" tag ")
                 .append(" WHERE ")
                     .append(" pk = ? ");
             // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setInt(1, tag.getPk());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return false;
    }

    public static boolean delete(TransferTO transfer) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" DELETE ")
                 .append(" FROM ")
                     .append(" transfer_tag ")
                 .append(" WHERE ")
                     .append(" transfer_fk = ? ");
             // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setInt(1, transfer.getPk());
            return preparedStatement.executeUpdate() >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return false;
    }

    public static boolean fill(TransferTO transfer) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                     .append(" * ")
                 .append(" FROM ")
                     .append(" transfer_tag ")
                 .append(" JOIN tag ON ")
                     .append(" tag.pk = transfer_tag.tag_fk ")
                 .append(" WHERE ")
                     .append(" transfer_fk =? ")
                .append(" ORDER BY title ");
             // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setInt(1, transfer.getPk());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                transfer.addTag(constructObject(resultSet));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return false;
    }

    public static Map<String, TagTO> get(List<TagTO> excludedTags) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, TagTO> tags = new TreeMap<String, TagTO>();
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                   .append(" * ")
               .append(" FROM ")
                   .append(" tag ");
            if (excludedTags.size() >0) {
                query.append(" WHERE ")
                .append(" pk NOT IN (");
                for (int i = 0; i < excludedTags.size(); i++) {
                    query.append("?,");
                }
                query.deleteCharAt(query.length() - 1).append(")");
            }
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            if (excludedTags.size() > 0) {
                int index = 1;
                for (TagTO tag : excludedTags) {
                    preparedStatement.setObject(index++, tag.getPk());
                }
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TagTO tag = constructObject(resultSet);
                tags.put(tag.getTitle(), tag);
            }
            return tags;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return Collections.emptyMap();
    }

    public static Map<String, TagTO> get(LocalDate from, LocalDate to) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, TagTO> tags = new TreeMap<String, TagTO>();
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                   .append(" * ")
               .append(" FROM ")
                   .append(" transfer_tag ")
               .append(" JOIN transfer ON ")
                   .append(" transfer.pk = transfer_tag.transfer_fk ")
               .append(" JOIN tag ON ")
                   .append(" tag.pk = transfer_tag.tag_fk ")
               .append(" WHERE ")
                   .append(" date BETWEEN ? AND ? ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(to.atStartOfDay()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TagTO tag = constructObject(resultSet);
                tags.put(tag.getTitle(), tag);
            }
            return tags;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return Collections.emptyMap();
    }

    public static Map<String, TagTO> get() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        Map<String, TagTO> tags = new TreeMap<String, TagTO>();
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                   .append(" * ")
               .append(" FROM ")
                   .append(" tag ");
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TagTO tag = constructObject(resultSet);
                tags.put(tag.getTitle(), tag);
            }
            return tags;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return Collections.emptyMap();
    }

    public static List<TagTO> get(String iban) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<TagTO> tags = new ArrayList<TagTO>();
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            StringBuilder query = new StringBuilder();
            // @formatter:off
            query.append(" SELECT ")
                   .append(" * ")
               .append(" FROM ")
                   .append(" transfer_tag ")
               .append(" JOIN tag ON ")
                   .append(" tag_fk = tag.pk ")                   
               .append(" WHERE ")
                   .append(" transfer_fk =( ")
                        .append(" SELECT ")        
                            .append(" transfer.pk AS transfer_pk ")
                        .append(" FROM ")
                            .append(" applicant ")  
                        .append(" JOIN transfer ON ")
                            .append(" applicant.pk = transfer.applicant_fk ")
                        .append(" WHERE ")  
                            .append(" iban = ? ")
                        .append(" ORDER BY ")
                            .append(" date DESC LIMIT 1 ")
                   .append(" ) ");                        
            // @formatter:on
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.setString(1, iban);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tags.add(constructObject(resultSet));
            }
            return tags;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(preparedStatement, resultSet);
        }
        return Collections.emptyList();
    }

    private static TagTO constructObject(ResultSet resultSet) {
        TagTO tag = new TagTO();
        try {
            tag.setPk(resultSet.getInt("pk"));
            tag.setTitle(resultSet.getString("title"));
            tag.setVisible(resultSet.getBoolean("visible"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }

}
