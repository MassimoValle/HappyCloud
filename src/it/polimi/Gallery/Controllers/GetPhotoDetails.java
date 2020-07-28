package it.polimi.Gallery.Controllers;

import it.polimi.Gallery.Beans.Photo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@WebServlet("/GetPhotoDetails")
public class GetPhotoDetails extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int albumId;
        List<Photo> photos;
        int set;
        try {
            albumId = Integer.parseInt(request.getParameter("albumId"));
            //photos = new ArrayList<Photo>((Collection<? extends Photo>) Arrays.asList(request.getParameter("photos")));
            set = Integer.parseInt(request.getParameter("set"));
        } catch (NumberFormatException | NullPointerException e) {
            // only for debugging e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
        }

    }
}
