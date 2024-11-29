package com.falconcars.www.falconcars;

public class Main {
    public static void main(String[] args){
        LoginApplication.launch(LoginApplication.class, args);

    /*
        ******* Please don't remove yet.
        ******* We may still need this logic
        InventoryUpdater updater = new InventoryUpdater();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Vehicle ID (1-301): ");
        int vehicleID = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter Image File Path: ");
        String filePath = scanner.nextLine();

        updater.updateInventoryImage(vehicleID, filePath);
    */

    }
}
