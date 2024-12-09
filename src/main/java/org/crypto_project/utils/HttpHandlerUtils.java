package org.crypto_project.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpHandlerUtils
{
    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException
    {
        System.out.println("Envoi de la réponse (" + statusCode + ") : --" + response + "--");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static String getQueryParam(HttpExchange exchange, String paramName) {
        // Extract the query string from the request URI
        String query = exchange.getRequestURI().getQuery();

        // Parse the query string into a map of key-value pairs
        Map<String, String> queryParams = parseQuery(query);

        // Return the value for the specified parameter name
        return queryParams.getOrDefault(paramName, null);
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    queryParams.put(keyValue[0], keyValue[1]);
                } else {
                    queryParams.put(keyValue[0], ""); // Handle keys with no value
                }
            }
        }
        return queryParams;
    }

    public static String readRequestBody(HttpExchange exchange) throws IOException
    {
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            requestBody.append(line);
        }
        reader.close();
        return requestBody.toString();
    }


    public static String[] parseValues(String requestBody) {
        // Format attendu : login=username&password=password
        String[] parts = requestBody.split("&");
        if (parts.length == 3) {
            String login = parts[0].split("=")[1];
            String password = parts[1].split("=")[1];
            String token = parts[2].split("=")[1];
            return new String[]{login, password, token};
        }
        return null;
    }

    public static String getAuthPage() {
        return """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Connexion pour Achat</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f9;
                    margin: 0;
                    padding: 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                }
                .login-container {
                    background: white;
                    padding: 20px;
                    border-radius: 8px;
                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    width: 300px;
                    text-align: center;
                }
                h1 {
                    font-size: 1.5rem;
                    margin-bottom: 20px;
                }
                input[type="text"], input[type="password"] {
                    width: calc(100% - 22px);
                    padding: 10px;
                    margin: 10px 0;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                }
                button {
                    background-color: #28a745;
                    color: white;
                    padding: 10px;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                    width: 100%;
                }
                button:hover {
                    background-color: #218838;
                }
                .note {
                    margin-top: 20px;
                    font-size: 0.9rem;
                    color: #555;
                }
            </style>
        </head>
        <body>
            <div class="login-container">
                <h1>Connexion pour Achat</h1>
                <form action="/api/auth" method="POST">
                    <input type="text" name="login" placeholder="Nom d'utilisateur" required>
                    <input type="password" name="password" placeholder="Mot de passe" required>
                     <input type="text" name="token" placeholder="code d'authentification" required>
                <button type="submit">Se connecter</button>
                </form>
                <p class="note">Vos informations resteront confidentielles.</p>
            </div>
        </body>
        </html>
        """;
    }

    public static String getLoadingPage() {
        return """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Vérification en cours</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f9;
                    margin: 0;
                    padding: 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    color: #333;
                }
                .container {
                    text-align: center;
                }
                .spinner {
                    margin: 20px auto;
                    border: 8px solid #f3f3f3;
                    border-top: 8px solid #3498db;
                    border-radius: 50%;
                    width: 50px;
                    height: 50px;
                    animation: spin 1s linear infinite;
                }
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
                p {
                    margin-top: 20px;
                    font-size: 1.2rem;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="spinner"></div>
                <p>Vérification de la transaction en cours...</p>
            </div>
        </body>
        </html>
        """;
    }

    public static String getSuccessPage() {
        return """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Transaction Réussie</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f9;
                    margin: 0;
                    padding: 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    color: #333;
                }
                .container {
                    text-align: center;
                }
                .icon {
                    font-size: 4rem;
                    color: #28a745;
                    margin-bottom: 20px;
                }
                h1 {
                    font-size: 1.5rem;
                    margin-bottom: 10px;
                }
                p {
                    font-size: 1.2rem;
                    color: #555;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="icon">✔️</div>
                <h1>Transaction Réussie !</h1>
                <p>Merci pour votre achat. Votre transaction a été validée avec succès.</p>
            </div>
        </body>
        </html>
        """;
    }

    public static String getFailPage() {
        return """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Transaction Échouée</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f9;
                    margin: 0;
                    padding: 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    color: #333;
                }
                .container {
                    text-align: center;
                }
                .icon {
                    font-size: 4rem;
                    color: #e74c3c;
                    margin-bottom: 20px;
                }
                h1 {
                    font-size: 1.5rem;
                    margin-bottom: 10px;
                }
                p {
                    font-size: 1.2rem;
                    color: #555;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="icon">❌</div>
                <h1>Transaction Échouée</h1>
                <p>Nous sommes désolés, mais votre transaction n'a pas pu être complétée.</p>
            </div>
        </body>
        </html>
        """;
    }

    public static String getPaymentPage() {
        return """
    <!DOCTYPE html>
    <html lang="fr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Paiement</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f4f4f9;
                margin: 0;
                padding: 0;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
            }
            .payment-container {
                background: white;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                width: 300px;
                text-align: center;
            }
            h1 {
                font-size: 1.5rem;
                margin-bottom: 20px;
            }
            input[type="text"] {
                width: calc(100% - 22px);
                padding: 10px;
                margin: 10px 0;
                border: 1px solid #ccc;
                border-radius: 4px;
            }
            button {
                background-color: #007bff;
                color: white;
                padding: 10px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                width: 100%;
            }
            button:hover {
                background-color: #0056b3;
            }
            .note {
                margin-top: 20px;
                font-size: 0.9rem;
                color: #555;
            }
        </style>
    </head>
    <body>
        <div class="payment-container">
            <h1>Entrez votre Token</h1>
            <form action="/api/payment" method="POST">
                <input type="text" name="token" placeholder="Token de paiement" required>
                <button type="submit">Paiement</button>
            </form>
            <p class="note">Veuillez saisir un token valide pour procéder au paiement.</p>
        </div>
    </body>
    </html>
    """;
    }


}
