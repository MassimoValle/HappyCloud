package it.polimi.Gallery.Dao;

import it.polimi.Gallery.Beans.Album;
import it.polimi.Gallery.Beans.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlbumDAO {

    private final Connection connection;

    public AlbumDAO(Connection connection) {
        this.connection = connection;
    }


    public List<Album> getAlbums() throws SQLException {

        List<Album> albums = new ArrayList<>();

        String query = "SELECT * FROM db_Gallery_TIW2020.album";

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

        String query = "SELECT * FROM db_Gallery_TIW2020.album WHERE id = ?";
        // prendi le foto nell'album che corrisponde a albumId che vanno da currentSet a currentSet+4 (estremi inclusi)

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, String.valueOf(albumId));
            //preparedStatement.setString(2, String.valueOf(currentSet));

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

        return photos;

    }


    public boolean hasNext(int currentSet) throws SQLException {

        String query = "SELECT * FROM db_Gallery_TIW2020.album WHERE id > ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, String.valueOf(currentSet+4));

            try (ResultSet result = preparedStatement.executeQuery()) {

                return result.next();
            } catch (SQLException e) {
                connection.rollback();
                return false;
            }
        }
    }


}
