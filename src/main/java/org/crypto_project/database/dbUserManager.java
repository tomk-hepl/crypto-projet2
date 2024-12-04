package org.crypto_project.database;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.crypto_project.database.dbUserMethods.addUser;
import static org.crypto_project.database.dbUserMethods.createTable;


public class dbUserManager
{

    private static final String DB_URL = "jdbc:sqlite:database.users";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {

            createTable(connection);


            addUser(connection, "Josue", "password1");
            addUser(connection, "Nasser", "password2");
            addUser(connection, "Tomas", "password3");

            System.out.println("Users successfully added.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
