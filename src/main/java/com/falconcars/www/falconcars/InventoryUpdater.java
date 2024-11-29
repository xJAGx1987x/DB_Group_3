package com.falconcars.www.falconcars;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InventoryUpdater {

    public void updateInventoryImage(int vehicleID, String filePath) {
        // Database connection details
        String url = "jdbc:mysql://database-2.cns6g8eseo17.us-east-2.rds.amazonaws.com:3306/FalconSportsCar?useLegacyDatetimeCode=false&serverTimezone=America/New_York";
        String user = "admin";
        String password = "password";

        // Convert image to byte array
        byte[] imageBytes = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            imageBytes = bos.toByteArray();
            fis.close();
            bos.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        // Database update query
        String sql = "UPDATE inventory SET image = ? WHERE stockNumber = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters
            pstmt.setBytes(1, imageBytes);
            pstmt.setInt(2, vehicleID);

            // Execute the update
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Image updated successfully for Vehicle ID: " + vehicleID);
            } else {
                System.out.println("No vehicle found with ID: " + vehicleID);
            }

        } catch (SQLException e) {
            System.out.println("Database connection or update failed: " + e.getMessage());
        }
    }
}


