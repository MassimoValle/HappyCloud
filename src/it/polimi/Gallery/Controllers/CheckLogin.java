package it.polimi.Gallery.Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.Gallery.Beans.User;
import it.polimi.Gallery.Dao.UserDAO;
import it.polimi.Gallery.Utils.ConnectionHandler;
import it.polimi.Gallery.Utils.ServletUtils;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;


@WebServlet("/CheckLogin")
@MultipartConfig
public class CheckLogin extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;


    public void init() throws ServletException {

        connection = ConnectionHandler.getConnection(getServletContext());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // obtain and escape params
        String username;
        String password;

        try {
            username = request.getParameter("username");
            password = request.getParameter("password");

            if (username==null || password==null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Login error: Credentials can't be null");
                return;
            }
            else if(username.isEmpty() || password.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Login error: Credentials can't be empty");
                return;
            }

        } catch (NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // query db to authenticate for user
        UserDAO userDao = new UserDAO(connection);
        User user;

        try {
            user = userDao.checkUser(username, password);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
            return;
        }


        // If the user exists, add info to the session and go to home page, otherwise
        // show login page with error message

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Login error: Incorrect credentials");
            return;
        }

        request.getSession().setAttribute("user", user);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(username);

    }
    
    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
