package edu.ycp.cs320.tbag.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

/**
 * IndexServlet forwards requests to the landing page (index.jsp).
 */
@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Forward the request to the index JSP page located under _view directory.
        req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
    }
}