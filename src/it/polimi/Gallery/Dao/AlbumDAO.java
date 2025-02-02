package it.polimi.Gallery.Dao;

import it.polimi.Gallery.Beans.Album;
import it.polimi.Gallery.Beans.Comment;
import it.polimi.Gallery.Beans.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlbumDAO {

    private final Connection connection;

    public AlbumDAO(Connection connection) {
        this.connection = connection;
    }


    public List<Album> getAlbums() throws SQLException {

        List<Album> albums = new ArrayList<>();

        String query = "SELECT * FROM db_Gallery_TIW2020.album ORDER BY Date DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Album album = new Album();
                    album.setId(result.getInt("Id"));
                    album.setTitle(result.getString("title"));
                    album.setDate(result.getDate("date"));

                    albums.add(album);
                }
            }
            catch (SQLException e){
                connection.rollback();
                return null;
            }
        }

        return albums;
    }

    public List<Photo> getFivePhotos(int albumId, int currentSet) throws SQLException {

        List<Photo> photos = new ArrayList<>();

        String query = "SELECT * FROM db_Gallery_TIW2020.Image " +
                         "WHERE AlbumId = ? ORDER BY Date DESC LIMIT 5 OFFSET ?";
        // prendi le foto nell'album che corrisponde a albumId che vanno da currentSet a currentSet+4 (estremi inclusi)

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, albumId);
            preparedStatement.setInt(2, ((currentSet-1)*5));

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Photo photo = new Photo();
                    photo.setId(result.getInt("id"));
                    photo.setTitle(result.getString("title"));
                    photo.setDate(result.getDate("date"));
                    photo.setDescription(result.getString("description"));
                    photo.setPath(result.getString("path"));

                    photos.add(photo);
                }
            }
            catch (SQLException e){
                connection.rollback();
                return null;
            }
        }

        photos = getComments(photos);

        return photos;

    }

    private List<Photo> getComments(List<Photo> photos) throws SQLException {


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


    public boolean hasNext(Integer albumId, Integer currentSet) throws SQLException {

        String query = "SELECT * FROM db_Gallery_TIW2020.Image " +
                "WHERE AlbumId = ? LIMIT 5 OFFSET ?";
        //exclude currentSet x 5 result


        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, albumId);
            preparedStatement.setInt(2, (currentSet*5));

            try (ResultSet result = preparedStatement.executeQuery()) {

                return result.next();
            } catch (SQLException e) {
                connection.rollback();
                return false;
            }
        }
    }


}
