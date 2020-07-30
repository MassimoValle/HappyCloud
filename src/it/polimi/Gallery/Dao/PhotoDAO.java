package it.polimi.Gallery.Dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PhotoDAO {

    private final Connection connection;

    public PhotoDAO(Connection connection) {
        this.connection = connection;
    }


    public boolean addComment(int imageId, String username, String text) throws SQLException{

        String query = "INSERT INTO db_Gallery_TIW2020.Comments(ImageId,Username,Text) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, imageId);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, text);

            try {

                int result = preparedStatement.executeUpdate();
                return result > 0;

            } catch (SQLException e) {

                connection.rollback();
                return false;

            }
        }
    }
}
