package com.db_group_three.www.demo;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        // commented out the login and db applications
        // to add code to push images to the DB.
        // so I could add code to push images to the DB.
        // Sorry, I guess for being so fucking awesome.
        // LoginApplication.launch(LoginApplication.class, args);
        // DatabaseApplication.launch(DatabaseApplication.class, args) ;

        InventoryUpdater updater = new InventoryUpdater();
        Scanner scanner = new Scanner(System.in);

        // Collect inputs
        System.out.print("Enter Vehicle ID (1-301): ");
        int vehicleID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter Image File Path: ");
        String filePath = scanner.nextLine();

        // Call the method to update the inventory image
        updater.updateInventoryImage(vehicleID, filePath);
    }
}
