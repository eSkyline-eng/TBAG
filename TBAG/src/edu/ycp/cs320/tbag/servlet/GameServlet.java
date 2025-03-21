package edu.ycp.cs320.tbag.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import edu.ycp.cs320.tbag.controller.GameController;
import edu.ycp.cs320.tbag.model.GameEngine;

/**
 * GameServlet handles requests for the game interface.
 * It initializes the game on first access, processes player commands,
 * and forwards to the game JSP view.
 */
@WebServlet("/game")
public class GameServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    /**
     * Handles GET requests by initializing the game if necessary
     * and forwarding the request to the game view.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
       throws ServletException, IOException {
        HttpSession session = req.getSession();
        // Check if the GameController exists in the session; if not, create one.
        if (session.getAttribute("gameController") == null) {
            GameEngine engine = new GameEngine();
            GameController controller = new GameController(engine);
            session.setAttribute("gameController", controller);
        }
        req.getRequestDispatcher("/_view/game.jsp").forward(req, resp);
    }
    
    /**
     * Handles POST requests by processing the player's command.
     * The command is passed to the GameController which updates the game state.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
       throws ServletException, IOException {
        HttpSession session = req.getSession();
        GameController controller = (GameController) session.getAttribute("gameController");
        if (controller == null) {
            // In case session expired or no controller was found, create a new one.
            controller = new GameController(new GameEngine());
            session.setAttribute("gameController", controller);
        }
        
        String command = req.getParameter("command");
        if (command != null && !command.trim().isEmpty()) {
            // Process the player's command.
            controller.processCommand(command);
        }
        
        // Forward the request back to the game view to show updated state.
        req.getRequestDispatcher("/_view/game.jsp").forward(req, resp);
    }
}