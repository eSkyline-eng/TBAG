package edu.ycp.cs320.tbag.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import edu.ycp.cs320.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.tbag.db.persist.IDatabase;
import edu.ycp.cs320.tbag.db.persist.DerbyGameDatabase;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests to /index.  Only when the "restart" parameter is set to "true"
     * will it invalidate the session and reset the Derby database back to its initial CSV-loaded state.
     * Otherwise it simply forwards to the welcome page.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Check if this is a genuine "Restart Game" click
        if ("true".equals(req.getParameter("restart"))) {
            // 1) Invalidate the old session (clears in-memory GameController, transcript, etc.)
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // 2) Reset the Derby database (wipes player/items and reloads from CSV)
            DatabaseProvider.setInstance(new DerbyGameDatabase());
            IDatabase db = DatabaseProvider.getInstance();
            db.resetGameData();
        }

        // Forward to the index.jsp welcome page
        req.getRequestDispatcher("/_view/index.jsp")
           .forward(req, resp);
    }
}
