package com.falconcars.www.falconcars;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();
    private static final String fileName = "src/main/resources/com/falconcars/www/falconcars/.env" ;

    // Static initializer block to load the properties file
    static {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }

    // Static methods to get the database properties
    public static String getDbUrl() {
        return properties.getProperty("DB_URL"); // Matches DB_URL in .env
    }

    public static String getDbUser() {
        return properties.getProperty("DB_USER");
    }

    public static String getDbPassword() {
        return properties.getProperty("DB_PASSWORD");
    }

}


