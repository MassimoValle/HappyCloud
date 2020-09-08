package it.polimi.Gallery.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.Gallery.Beans.Album;
import it.polimi.Gallery.Dao.AlbumDAO;
import it.polimi.Gallery.Utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetAlbums")
@MultipartConfig
public class GetAlbums extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        AlbumDAO albumDAO = new AlbumDAO(connection);
        List<Album> albums;

        try {
            albums = albumDAO.getAlbums();

        } catch (SQLException e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Not possible to recover albums, try later");
            return;
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
        String json_albums = gson.toJson(albums);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json_albums);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
