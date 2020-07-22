package it.polimi.Gallery.Controllers;

import it.polimi.Gallery.Beans.Album;
import it.polimi.Gallery.Beans.User;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GoToHome")
public class GoToHome extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private Connection connection = null;

    public void init() throws ServletException {
        this.templateEngine = ServletUtils.createThymeleafTemplate(getServletContext());
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        AlbumDAO albumDAO = new AlbumDAO(connection);
        List<Album> albums;

        try {
            albums = albumDAO.getAlbums();

        } catch (SQLException e) {

            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover meetings");
            return;
        }

        // Redirect to the Home page and add missions to the parameters
        String path = "/home.html";
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());

        webContext.setVariable("albums", albums);

        templateEngine.process(path, webContext, response.getWriter());

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
