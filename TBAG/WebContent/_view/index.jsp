<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>TBAG: Nine to Thrive - Welcome</title>
    <style>
        /* Global Styles */
        body {
            margin: 0;
            padding: 0;
            font-family: 'Arial', sans-serif;
            background: linear-gradient(to bottom, #4facfe, #00f2fe);
            color: #333;
        }
        .container {
            max-width: 800px;
            margin: 100px auto 0 auto;
            background-color: rgba(255, 255, 255, 0.95);
            border-radius: 8px;
            padding: 40px 20px;
            text-align: center;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        }
        h1 {
            font-size: 2.8em;
            margin-bottom: 20px;
            color: #1a1a1a;
        }
        p {
            font-size: 1.2em;
            margin-bottom: 30px;
        }
        .btn {
            background-color: #ff7f50;
            color: white;
            border: none;
            padding: 15px 30px;
            font-size: 1.2em;
            cursor: pointer;
            border-radius: 5px;
            text-decoration: none;
        }
        .btn:hover {
            background-color: #ff6347;
        }
        footer {
            text-align: center;
            margin-top: 40px;
            font-size: 0.9em;
            color: #555;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Welcome to TBAG: Nine to Thrive</h1>
        <p>
            Embark on an urban adventure where every choice shapes your destiny.
            Explore mysterious city streets, unlock hidden secrets, and build your legacy!
        </p>
        <form action="game" method="get">
            <input type="submit" class="btn" value="Play Game">
        </form>
    </div>
    <footer>
        &copy; 2025 TBAG. All rights reserved.
    </footer>
</body>
</html>
