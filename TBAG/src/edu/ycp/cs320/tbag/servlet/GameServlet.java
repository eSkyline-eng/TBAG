package edu.ycp.cs320.tbag.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import edu.ycp.cs320.tbag.controller.GameController;
import edu.ycp.cs320.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.tbag.db.persist.IDatabase;
import edu.ycp.cs320.tbag.model.GameEngine;

@WebServlet("/game")
public class GameServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    /**
     * Handles GET requests by ensuring that a GameController exists in the session.
     * Forwards the request to the game view.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("gameController") == null) {
            // Create a new GameEngine and GameController if none exists.
            GameEngine engine = new GameEngine();
            GameController controller = new GameController(engine);
            session.setAttribute("gameController", controller);
        }
        req.getRequestDispatcher("/_view/game.jsp").forward(req, resp);
    }
    
    /**
     * Handles POST requests by retrieving the player's command, processing it via the GameController,
     * and then forwarding back to the game view.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        GameController controller = (GameController) session.getAttribute("gameController");
        if (controller == null) {
            // In case the session expired or was not set up, create a new instance.
            controller = new GameController(new GameEngine());
            session.setAttribute("gameController", controller);
        }
        
        req.setAttribute("player", controller.getPlayer());
        
        // Retrieve the command entered by the user.
        String command = req.getParameter("command");
        if (command != null && !command.trim().isEmpty()) {
        	String result = controller.processCommand(command);
        	
            if (result.equals("__ENDING_ACCEPTED__")) {
            	session.setAttribute("player", controller.getPlayer());
                session.setAttribute("endingDescription", controller.getGameEngine().getEndingDescription());
                req.getRequestDispatcher("/_view/ending.jsp").forward(req, resp);
                return;
            }
            
            req.setAttribute("gameOutput", result);
        }
       
        // Forward to the game JSP to update the view (transcript, inventory, etc.)
        req.getRequestDispatcher("/_view/game.jsp").forward(req, resp);
    }
}
