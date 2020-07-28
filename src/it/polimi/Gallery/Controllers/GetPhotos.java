package it.polimi.Gallery.Controllers;

import it.polimi.Gallery.Beans.Photo;
import it.polimi.Gallery.Dao.AlbumDAO;
import it.polimi.Gallery.Utils.ConnectionHandler;
import it.polimi.Gallery.Utils.ServletUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetPhotos")
public class GetPhotos extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    private int currentSet = 0;

    public void init() throws ServletException {
        this.templateEngine = ServletUtils.createThymeleafTemplate(getServletContext());
        connection = ConnectionHandler.getConnection(getServletContext());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int albumId;
        int set;
        try {
            albumId = Integer.parseInt(request.getParameter("albumId"));
            set = Integer.parseInt(request.getParameter("set"));
        } catch (NumberFormatException | NullPointerException e) {
            // only for debugging e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
        }

        currentSet = set;

        AlbumDAO albumDAO = new AlbumDAO(connection);
        List<Photo> photos;
        try {
            photos = albumDAO.getFivePhotos(albumId, currentSet);
            if (photos == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
                return;
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQLException: Not possible to recover photos");
            return;
        }

        boolean before = false;
        boolean next = false;


        if(photos.size() < 5) next = false;
        else {  // check if there are other photo after those 5
            try {
                next = albumDAO.hasNext(albumId, currentSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(currentSet > 1) before = true;



        // Redirect to the Home page and add missions to the parameters
        String path = "/albumPage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("albumId", albumId);
        ctx.setVariable("set", currentSet);
        ctx.setVariable("photos", photos);
        ctx.setVariable("before", before);
        ctx.setVariable("next", next);
        templateEngine.process(path, ctx, response.getWriter());
    }



    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
