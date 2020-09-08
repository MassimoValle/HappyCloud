package it.polimi.Gallery.Controllers;


import it.polimi.Gallery.Beans.User;
import it.polimi.Gallery.Dao.UserDAO;
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
import java.util.regex.Pattern;

@WebServlet("/Signup")
@MultipartConfig
public class Signup extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // obtain and escape params
        String username;
        String name;
        String surname;
        String email;
        String password;
        String confPassoword;

        try {
            username = request.getParameter("username");
            name = request.getParameter("name");
            surname = request.getParameter("surname");
            email = request.getParameter("email");
            password = request.getParameter("password");
            confPassoword = request.getParameter("confPassword");

            if (username==null || name==null || surname== null || email==null || password==null || confPassoword==null) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Signup error: Attributes can't be null");
                return;
            }
            else if(username.isEmpty() || name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confPassoword.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Signup error: Attributes can't be empty");
                return;
            }
            else if (!isEmailValid(email)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Signup error: The email isn't valid.");
                return;
            }
            else if (!password.equals(confPassoword)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Signup error: Passwords aren't equal!");
                return;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Signup error: Something went wrong, try again!");
            return;
        }


        // query db to authenticate for user
        UserDAO userDao = new UserDAO(connection);
        User user;

        try {
            // provo ad aggiungere l'utente al db, poichè username è attributo unico, ritorna null se già presente
            user = userDao.registerUser(username, name, surname, email, password);
        } catch (SQLException e) {
            // se exc -> invalid username
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Signup error: username or email are already used");
            return;
        }

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Signup error: Not Possible to check attributes");
            return;
        }


        // se tutto ok:

        request.getSession().setAttribute("user", user);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(username);

    }

    
    private boolean isEmailValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern patternRegex = Pattern.compile(emailRegex);

        if (email == null)
            return false;

        return patternRegex.matcher(email).matches();
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
