package org.crypto_project.database;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.crypto_project.database.dbUserMethods.addUser;
import static org.crypto_project.database.dbUserMethods.createTable;


public class dbUserManager
{

    private static final String DB_URL = "jdbc:sqlite:database.db";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {

            createTable(connection);


            addUser(connection, "Josue", "password");
            addUser(connection, "Nasser", "securepassword123");
            addUser(connection, "Tomas", "dfvavvrv");

            System.out.println("Users successfully added.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
