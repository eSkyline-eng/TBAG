package edu.ycp.cs320.tbag.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
       throws ServletException, IOException {
        // Invalidate the current session to reset the game state.
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
    }
}
