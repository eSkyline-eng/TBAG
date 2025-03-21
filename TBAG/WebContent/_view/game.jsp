<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.ycp.cs320.tbag.controller.GameController" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>TBAG: Nine to Thrive - Game</title>
    <style>
        /* Style for the transcript area */
        #transcript {
            width: 600px;
            height: 300px;
            border: 1px solid #000;
            overflow-y: scroll;
            background-color: #eee;
            padding: 10px;
            white-space: pre-wrap;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <h1>TBAG: Nine to Thrive</h1>
    <!-- Display the game transcript -->
    <div id="transcript">
        <%
            GameController controller = (GameController) session.getAttribute("gameController");
            if (controller != null) {
                out.print(controller.getTranscript());
            } else {
                out.print("No game session found. Please start a new game.");
            }
        %>
    </div>
    <!-- Form for entering game commands -->
    <form action="game" method="post">
        <input type="text" name="command" size="80" autofocus />
        <input type="submit" value="Enter Command" />
    </form>
    <!-- Option to restart the game -->
    <form action="index" method="get">
        <input type="submit" value="Restart Game" />
    </form>
</body>
</html>