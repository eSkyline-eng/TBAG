<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="edu.ycp.cs320.tbag.model.Player" %>
<%
    Player player = (Player) session.getAttribute("player");
    String endingDescription = (String) session.getAttribute("endingDescription");

    if (player == null) {
%>
    <p style="color:red;">Error: Player object is null.</p>
<%
    return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>TBAG: Nine to Thrive - Game Over</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
            background: linear-gradient(to bottom, #4facfe, #00f2fe);
            color: #333;
        }
        .container {
            max-width: 800px;
            margin: 40px auto;
            background-color: rgba(255, 255, 255, 0.95);
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        }
        h1 {
            text-align: center;
            font-size: 2.5em;
            margin-bottom: 20px;
            color: #1a1a1a;
        }
        #endingDescription {
            font-size: 1.2em;
            margin-bottom: 20px;
            text-align: center;
            font-weight: bold;
        }
        #stats {
            list-style-type: none;
            padding: 0;
            font-size: 1.1em;
        }
        #stats li {
            margin-bottom: 10px;
        }
        #restartForm {
            text-align: center;
            margin-top: 30px;
        }
        #restartForm input {
            padding: 10px 20px;
            font-size: 1.1em;
            border: none;
            border-radius: 4px;
            background-color: #555;
            color: white;
            cursor: pointer;
        }
        #restartForm input:hover {
            background-color: #333;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Game Over</h1>
        <div id="endingDescription"><%= endingDescription %></div>

        <h2>Player Stats:</h2>
        <ul id="stats">
            <li>Health: <%= player.getHealth() %></li>
            <li>Inventory: <%= player.getInventoryString() %></li>
            <li>Achievements: <%= player.getFormattedAchievements() %></li>
        </ul>

        <form id="restartForm" action="index" method="get">
            <input type="hidden" name="restart" value="true" />
            <input type="submit" value="Restart Game">
        </form>
    </div>
</body>
</html>
