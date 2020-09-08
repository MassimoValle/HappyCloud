package it.polimi.Gallery.Controllers;

import it.polimi.Gallery.Beans.Photo;
import it.polimi.Gallery.Dao.AlbumDAO;
import it.polimi.Gallery.Dao.PhotoDAO;
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

    private int albumId;
    private int currentSet;
    private int imgSelected;




    public void init() throws ServletException {
        this.templateEngine = ServletUtils.createThymeleafTemplate(getServletContext());
        connection = ConnectionHandler.getConnection(getServletContext());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        boolean addComment;

        try {
            addComment = (boolean) request.getAttribute("addComment");
        }
        catch (NumberFormatException | NullPointerException e) {
            addComment = false;
        }

        if(!addComment) {

            try {
                albumId = Integer.parseInt(request.getParameter("albumId"));
                currentSet = Integer.parseInt(request.getParameter("set"));
                imgSelected = Integer.parseInt(request.getParameter("imgSelected"));

            } catch (NumberFormatException | NullPointerException e) {
                // only for debugging e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                invalidComment("Incorrect param values", request, response);
                return;
            }

        }


        request.getSession().setAttribute("imgSelected", imgSelected);

        AlbumDAO albumDAO = new AlbumDAO(connection);
        PhotoDAO photoDAO = new PhotoDAO(connection);
        List<Photo> photos;
        try {
            photos = albumDAO.getFivePhotos(albumId, currentSet);
            photos = photoDAO.getComments(photos);
            if (photos == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                invalidComment("Resource not found", request, response);
                return;
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            invalidComment("Not possible to recover photos", request, response);
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


        if(imgSelected < 0) imgSelected = 6;    // significa invalid

        else {
            for (Photo photo : photos){
                if(photo.getId() == imgSelected)
                    imgSelected = photos.indexOf(photo);
            }
        }


        // Redirect to the Home page and add missions to the parameters
        String path = "/albumPage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("albumId", albumId);
        ctx.setVariable("set", currentSet);
        ctx.setVariable("photos", photos);
        ctx.setVariable("imgSelected", imgSelected);
        ctx.setVariable("before", before);
        ctx.setVariable("next", next);
        templateEngine.process(path, ctx, response.getWriter());
    }

    private void invalidComment(String errorMessage, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = "/albumPage.html";
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("errorMessage", errorMessage);
        templateEngine.process(path, webContext, response.getWriter());
    }



    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
