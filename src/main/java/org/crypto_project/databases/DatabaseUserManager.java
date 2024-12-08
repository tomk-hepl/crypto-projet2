package org.crypto_project.databases;

public class DatabaseUserManager
{
    private static final String DB_URL = "jdbc:sqlite:database.users";

    public static void main(String[] args) {
        try (SQLLiteDatabase database = new SQLLiteDatabase(DB_URL)) {

            database.createTable();

            database.addUser("Josue", "password1");
            database.addUser("Nasser", "password2");
            database.addUser("Tomas", "password3");

            System.out.println("Users successfully added.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
