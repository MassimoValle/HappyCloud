package it.polimi.Gallery.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.Gallery.Beans.Photo;
import it.polimi.Gallery.Dao.AlbumDAO;
import it.polimi.Gallery.Dao.PhotoDAO;
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

@WebServlet("/GetPhotos")
@MultipartConfig
public class GetPhotos extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;


    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int albumId;
        try {
            albumId = Integer.parseInt(request.getParameter("albumId"));

        } catch (NumberFormatException | NullPointerException e) {
            // only for debugging e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param value");
            return;
        }


        AlbumDAO albumDAO = new AlbumDAO(connection);
        PhotoDAO photoDAO = new PhotoDAO(connection);
        List<Photo> photos;
        try {
            photos = albumDAO.getPhotos(albumId);
            photos = photoDAO.appendComments(photos);

            if (photos == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
                return;
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQLException: Not possible to recover photos");
            return;
        }


        Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
        String json_photos = gson.toJson(photos);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json_photos);
    }



    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
