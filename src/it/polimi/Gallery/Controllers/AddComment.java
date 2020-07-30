package it.polimi.Gallery.Controllers;


import it.polimi.Gallery.Beans.User;
import it.polimi.Gallery.Dao.PhotoDAO;
import it.polimi.Gallery.Utils.ConnectionHandler;
import it.polimi.Gallery.Utils.ServletUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/AddComment")
public class AddComment extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public void init() throws ServletException {
        this.templateEngine = ServletUtils.createThymeleafTemplate(getServletContext());
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int imgSelected = (int) request.getSession().getAttribute("imgSelected");

        User user = (User) request.getSession().getAttribute("user");
        String comment;

        try {

            comment = request.getParameter("comment");

            if (comment==null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                invalidComment("Comment can't be null", request, response);
                return;
            }
            else if(comment.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                invalidComment("Comment can't be empty", request, response);
                return;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        PhotoDAO photoDAO = new PhotoDAO(connection);

        boolean result;

        try {
            result = photoDAO.addComment(imgSelected, user.getUsername(), comment);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            invalidComment("SQLException: Error in database", request, response);
            return;
        }

        if(!result){
            invalidComment("Comment not saved", request, response);
            return;
        }


        request.setAttribute("addComment", true);

        RequestDispatcher rd = request.getRequestDispatcher("/GetPhotos");
        rd.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
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
