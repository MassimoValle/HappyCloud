package it.polimi.Gallery.Dao;

import it.polimi.Gallery.Beans.Comment;
import it.polimi.Gallery.Beans.Photo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<Photo> appendComments(List<Photo> photos) throws SQLException {


        String query = "SELECT * FROM db_Gallery_TIW2020.Comments WHERE ImageId = ?";

        for(Photo photo : photos){

            List<Comment> comments = new ArrayList<>();

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, photo.getId());

                try (ResultSet result = preparedStatement.executeQuery()) {
                    while (result.next()) {

                        Comment comment = new Comment();

                        comment.setUsername(result.getString("Username"));
                        comment.setText(result.getString("Text"));
                        comment.setDate(result.getDate("date"));

                        comments.add(comment);

                    }
                }
                catch (SQLException e){
                    connection.rollback();
                    return null;
                }
            }

            photo.setComments(comments);
        }

        return photos;
    }

    public List<Comment> getComments(int photoId) throws SQLException {


        String query = "SELECT * FROM db_Gallery_TIW2020.Comments WHERE ImageId = ?";

        List<Comment> comments = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, photoId);

            try (ResultSet result = preparedStatement.executeQuery()) {

                while (result.next()) {

                    Comment comment = new Comment();

                    comment.setUsername(result.getString("Username"));
                    comment.setText(result.getString("Text"));
                    comment.setDate(result.getDate("date"));

                    comments.add(comment);

                }
            }
            catch (SQLException e){
                connection.rollback();
                return null;
            }
        }

        return comments;
    }
}
