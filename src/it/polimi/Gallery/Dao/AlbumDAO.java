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

    private Connection connection;

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

    public List<Photo> getFivePhoto(int albumId, int currentSet) throws SQLException {

        List<Photo> photos = new ArrayList<>();

        String query = "SELECT * FROM db_Gallery_TIW2020.album";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

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


}
