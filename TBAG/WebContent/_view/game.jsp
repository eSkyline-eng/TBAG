<%@ page language="java" contentType="text/html; charset=UTF-8" 
    pageEncoding="UTF-8"%>
<%
    // Retrieve the GameController from the session.
    edu.ycp.cs320.tbag.controller.GameController controller = 
        (edu.ycp.cs320.tbag.controller.GameController) session.getAttribute("gameController");
    String transcript = (controller != null) ? controller.getTranscript() : "";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>TBAG: Nine to Thrive - Game</title>
    <style>
        /* Global Styles */
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
        #transcript {
            width: calc(100% - 20px); /* subtract 10px on each side */
            margin: 0 auto 20px auto; /* center horizontally with bottom margin */
            height: 300px;
            border: 1px solid #ccc;
            background-color: #fff;
            padding: 10px;
            overflow-y: scroll;
            white-space: pre-wrap;
        }
        #commandForm {
            text-align: center;
            margin-bottom: 20px;
        }
        #commandInput {
            width: 70%;
            padding: 10px;
            font-size: 1.1em;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        #submitCommand {
            padding: 10px 20px;
            font-size: 1.1em;
            margin-left: 10px;
            border: none;
            border-radius: 4px;
            background-color: #ff7f50;
            color: white;
            cursor: pointer;
        }
        #submitCommand:hover {
            background-color: #ff6347;
        }
        #restartForm {
            text-align: center;
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
    <script>
        // Auto-scrolls the transcript div to the bottom upon page load.
        function scrollTranscript() {
            var transcriptDiv = document.getElementById("transcript");
            transcriptDiv.scrollTop = transcriptDiv.scrollHeight;
        }
        window.onload = scrollTranscript;
    </script>
</head>
<body>
    <div class="container">
        <h1>TBAG: Nine to Thrive</h1>
        <!-- Display the game transcript -->
        <div id="transcript"><%= transcript %></div>
        <!-- Command input form -->
        <form id="commandForm" action="game" method="post">
            <input type="text" id="commandInput" name="command" placeholder="Enter your command here..." autofocus>
            <input type="submit" id="submitCommand" value="Submit">
        </form>
        <!-- Restart game button -->
        <form id="restartForm" action="index" method="get">
            <input type="submit" value="Restart Game">
        </form>
    </div>
</body>
</html>
