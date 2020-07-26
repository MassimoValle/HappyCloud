package it.polimi.Gallery.Controllers;


import it.polimi.Gallery.Beans.User;
import it.polimi.Gallery.Dao.UserDAO;
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
import java.util.ArrayList;

@WebServlet("/Registration")
public class Registration extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;
    
    public void init() throws ServletException {
        this.templateEngine = ServletUtils.createThymeleafTemplate(getServletContext());
        connection = ConnectionHandler.getConnection(getServletContext());
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // obtain and escape params
        String username;
        String email;
        String password;
        String confPassowrd;

        try {
            username = request.getParameter("username");
            email = request.getParameter("email");
            password = request.getParameter("password");
            confPassowrd = request.getParameter("confPassowrd");

            if (username==null || email==null || password==null || confPassowrd==null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                invalidCredentials("Attributes can't be null", request, response);
                return;
            }
            else if(username.isEmpty() || email.isEmpty() || password.isEmpty() || confPassowrd.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                invalidCredentials("Attributes can't be empty", request, response);
                return;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        if(!password.equals(confPassowrd)){
            // errore "passwords are different"
            return;
        }


        // query db to authenticate for user
        UserDAO userDao = new UserDAO(connection);
        User user;

        try {
            // provo ad aggiungere l'utente al db, poichè username è attributo unico, ritorna null se già presente
            user = userDao.registerUser(username, email, password);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to check attributes");
            return;
        }

        if (user == null) { // se null -> invalid username
            invalidCredentials("Choose another username", request, response);
            return;
        }


        // se tutto ok:

        String path;
        request.getSession().setAttribute("user", user);
        //request.getServletContext().setAttribute("user", user);
        path = getServletContext().getContextPath() + "/GoToHome";
        response.sendRedirect(path);
    }

    private void invalidCredentials(String errorMessage, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = "/index.html";
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("errorMessage", errorMessage);
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
