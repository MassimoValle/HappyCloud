package it.polimi.Gallery.Controllers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.Gallery.Beans.Comment;
import it.polimi.Gallery.Beans.User;
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

@WebServlet("/AddComment")
@MultipartConfig
public class AddComment extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //int imgSelected = (int) request.getSession().getAttribute("imgSelected");

        User user = (User) request.getSession().getAttribute("user");
        int imgSelected;
        String comment;

        try {

            comment = request.getParameter("comment");
            imgSelected = Integer.parseInt(request.getParameter("imgSelected"));

            if (comment==null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Comment can't be null");
                return;
            }
            else if(comment.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Comment can't be empty");
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
            response.getWriter().println("SQLException: Error in database");
            return;
        }

        if(!result){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Comment not saved");
            return;
        }

        List<Comment> comments = null;
        try {
            comments = photoDAO.getComments(imgSelected);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("SQLException: Error in database");
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
        String json_comments = gson.toJson(comments);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json_comments);

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
