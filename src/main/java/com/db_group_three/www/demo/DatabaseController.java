package com.db_group_three.www.demo;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseController {
    // Field for define a successfully logged in user
    private DBUser currentUser ;
    @FXML
    private TabPane dbTabPane ;
    @FXML
    private Tab locationTab ;
    @FXML
    private Tab employeeTab ;
    // Fields for Vehicle Trends tab
    @FXML
    private TextField makeField;
    @FXML
    private TextField modelField;
    @FXML
    private Button vSearchButton;
    @FXML
    private TableView<Map<String, Object>> vehicleTableView ;
    @FXML
    private RadioButton yearRadioButton;
    @FXML
    private RadioButton monthRadioButton;

    // Fields for Customer tab
    @FXML
    private TextField ctMakeField;
    @FXML
    private TextField ctModelField;
    @FXML
    private Button ctSearchButton;
    @FXML
    private TableView<Map<String, Object>> customerTableView;

    // Fields for Top Sellers tab
    @FXML
    private RadioButton topRadioButton;
    @FXML
    private RadioButton usedRadioButton;
    @FXML
    private Button topSellersButton;
    @FXML
    private TableView<Map<String, Object>> topSellersTableView;

    // Fields for Locations Tab
    @FXML
    private Button locationUpdateButton ;
    @FXML
    private TableView<Map<String, Object>> locationTableView;
    @FXML
    private TableColumn<Map<String, Object>, String> locationIDColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> addressColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> cityColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> stateColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> totalSalesColumn;

    // Fields for Employee tab
    @FXML
    private Button employeeUpdateButton ;
    @FXML
    private TableView<Map<String, Object>> employeeTableView;

    // Fields for input search tab
    @FXML
    private Button inputSearchButton ;
    @FXML
    private Button clearInputSearchButton ;
    @FXML
    private TextArea searchTextArea ;
    @FXML
    private TableView<Map<String, Object>> searchTableView;

    private ToggleGroup vehicleTypeToggleGroup;
    private ToggleGroup newUsedToggleGroup;

    // Database info for connection
    private final String DB_URL = "jdbc:mysql://database-2.cns6g8eseo17.us-east-2.rds.amazonaws.com:3306/FalconSportsCar?useLegacyDatetimeCode=false&serverTimezone=America/New_York";
    private final String DB_USER = "admin";
    private final String DB_PASSWORD = "password";

    @FXML
    public void initialize() {
        setupToggleGroups();
        setupEventHandlers();
        customerTableView.setPlaceholder(new Label(""));
        searchTableView.setPlaceholder(new Label(""));
        employeeTableView.setPlaceholder(new Label(""));
        topSellersTableView.setPlaceholder(new Label(""));
        vehicleTableView.setPlaceholder(new Label(""));
        locationTableView.setPlaceholder(new Label(""));
        customerTableView.getColumns().clear();
        searchTableView.getColumns().clear();
        employeeTableView.getColumns().clear();
        topSellersTableView.getColumns().clear();
        vehicleTableView.getColumns().clear();
        locationTableView.getColumns().clear();
    }

    private void setupToggleGroups() {
        // Set up ToggleGroup for Vehicle Trends
        this.vehicleTypeToggleGroup = new ToggleGroup();
        this.yearRadioButton.setToggleGroup(vehicleTypeToggleGroup);
        this.monthRadioButton.setToggleGroup(vehicleTypeToggleGroup);
        this.yearRadioButton.setSelected(true);

        // Set up ToggleGroup for new/used selection
        this.newUsedToggleGroup = new ToggleGroup();
        this.topRadioButton.setToggleGroup(newUsedToggleGroup);
        this.usedRadioButton.setToggleGroup(newUsedToggleGroup);
        this.topRadioButton.setSelected(true);
    }

    private void clearAndPopulateListView(ListView<String> listView, String header, String... items) {
        listView.getItems().clear();
        listView.getItems().add(header);
        if (items.length > 0) {
            for (String item : items) {
                listView.getItems().add(item);
            }
        }
    }

    private String getVehicleSearchQuery(String timePeriod) {
        return "SELECT YEAR(r.dateOfPurchase) AS purchaseYear, " +
                (timePeriod.equals("Monthly") ? "MONTH(r.dateOfPurchase) AS purchaseMonth, " : "") +
                "COUNT(r.stockNumber) AS numSales, SUM(i.netSalePrice) AS totalSales " +
                "FROM records r " +
                "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "WHERE i.make = ? AND i.model = ? AND Year(r.dateOfPurchase) between Year(curdate()) - 3 AND Year(curdate()) " +
                (timePeriod.equals("Yearly") ? "GROUP BY purchaseYear " : "GROUP BY purchaseYear, purchaseMonth ") +
                "ORDER BY purchaseYear" + (timePeriod.equals("Monthly") ? ", purchaseMonth" : "");
    }

    private void setupEventHandlers() {
        vSearchButton.setOnAction(event -> handleVehicleSearch());
        ctSearchButton.setOnAction(event -> handleCustomerSearch());
        topSellersButton.setOnAction(event -> handleTopSellersSearch());
        locationUpdateButton.setOnAction(event -> handleLocationSearch() );
        employeeUpdateButton.setOnAction(event -> handleSalespersonUpdate() );
        inputSearchButton.setOnAction(event -> handleInputSearch() ) ;
        clearInputSearchButton.setOnAction(event -> handleClearInput() );
    }

    @FXML
    private void handleClearInput() {
        searchTableView.getItems().clear() ;
        searchTableView.getColumns().clear();
        searchTextArea.setText(null) ;
    }

    @FXML
    private void handleInputSearch() {
        String query = searchTextArea.getText().trim();

        if (query.isEmpty() || query.equals("Enter a query")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please enter a query before pressing search.");
            alert.showAndWait();
            return;
        }

        if (query.endsWith(";")) {
            query = query.substring(0, query.length() - 1);
        }

        // Check if the query starts with UPDATE or INSERT
        String queryUpper = query.toUpperCase();
        if (queryUpper.startsWith("ALTER") ||
                queryUpper.startsWith("CREATE") ||
                queryUpper.startsWith("DELETE") ||
                queryUpper.startsWith("DROP") ||
                queryUpper.startsWith("EXEC") ||
                queryUpper.startsWith("EXECUTE") ||
                queryUpper.startsWith("GRANT") ||
                queryUpper.startsWith("INSERT") ||
                queryUpper.startsWith("MERGE") ||
                queryUpper.startsWith("REPLACE") ||
                queryUpper.startsWith("SET") ||
                queryUpper.startsWith("TRUNCATE") ||
                queryUpper.startsWith("UPDATE") ||
                queryUpper.startsWith("WITH") ||
                queryUpper.startsWith("CALL") ||
                queryUpper.startsWith("COMMIT") ||
                queryUpper.startsWith("ROLLBACK") ||
                queryUpper.startsWith("SAVEPOINT") ||
                queryUpper.startsWith("TRANSACTION") ||
                queryUpper.startsWith("LOCK") ||
                queryUpper.startsWith("UNLOCK") ||
                queryUpper.startsWith("REVOKE") ||
                queryUpper.startsWith("ADD") ||
                queryUpper.startsWith("REMOVE") ||
                queryUpper.startsWith("DISABLE") ||
                queryUpper.startsWith("ENABLE")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Query");
            alert.setHeaderText("Only SELECT Queries Allowed");
            alert.setContentText("Please enter a valid SELECT query.");
            alert.showAndWait();
            return;
        }

        searchTableView.getItems().clear();
        searchTableView.getColumns().clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Dynamically create TableView columns based on ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                final int columnIndex = i;
                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(metaData.getColumnName(i));

                // Set to handle any object type (like Image or String)
                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = null;
                    try {
                        cellValue = row.get(metaData.getColumnName(columnIndex));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return new SimpleObjectProperty<>(cellValue); // Uses SimpleObjectProperty for any object type
                });

                // Set a custom cell for rendering Objects
                column.setCellFactory(col -> new TableCell<Map<String, Object>, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);  // Clear text for empty cells
                        } else {
                            if (item instanceof Image) {
                                Image image = (Image) item;
                                ImageView imageView = new ImageView(image);
                                imageView.setFitWidth(100);
                                imageView.setFitHeight(100);
                                setGraphic(imageView);
                                setText(null);
                            } else {  // Default behavior for non-image data
                                setText(item.toString());
                                setGraphic(null);
                            }
                        }
                    }
                });

                searchTableView.getColumns().add(column);
            }


            // Populate TableView rows
            ObservableList<Map<String, Object>> tableData = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    Object columnValue = rs.getObject(i);

                    // Check if the column is a BLOB type (for image data)
                    if (columnValue instanceof byte[]) {
                        byte[] bytes = (byte[]) columnValue;
                        InputStream inputStream = new ByteArrayInputStream(bytes);
                        Image image = new Image(inputStream);
                        row.put(metaData.getColumnName(i), image);  // Store Image in the row data map
                    } else {
                        row.put(metaData.getColumnName(i), columnValue);
                    }
                }
                tableData.add(row);
            }
            searchTableView.setItems(tableData);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Query Failed");
            alert.setContentText("Could not execute the query. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSalespersonUpdate() {
        String query = "SELECT sp.personID, p.name, p.email, p.phoneNum, p.address, p.city, p.state, p.zipcode, " +
                "COUNT(r.stockNumber) AS numSales, " +
                "SUM(CASE WHEN YEAR(r.dateOfPurchase) = YEAR(CURDATE()) - 1 THEN i.netSalePrice * sp.commissionRate ELSE 0 END) AS totalCommission " +
                "FROM sales_person sp " +
                "JOIN person p ON sp.personID = p.personID " +
                "LEFT JOIN records r ON sp.personID = r.salesPersonID AND YEAR(r.dateOfPurchase) = YEAR(CURDATE()) - 1 " +
                "LEFT JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "GROUP BY sp.personID, p.name " +
                "ORDER BY numSales DESC";

        // Clear existing data from the TableView
        employeeTableView.getItems().clear();
        employeeTableView.getColumns().clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Dynamically create columns based on ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                final int columnIndex = i;
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = null;
                    try {
                        cellValue = row.get(metaData.getColumnName(columnIndex));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });
                employeeTableView.getColumns().add(column);
            }

            // Populate rows with ResultSet data
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                employeeTableView.getItems().add(row);
            }

            // Check if no data was returned
            if (employeeTableView.getItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Data Found");
                alert.setHeaderText("No Salesperson Data Available");
                alert.setContentText("No data found for the past year.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error accessing the database");
            alert.setContentText("An error occurred while querying the database: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLocationSearch() {
        // Ensure columns are set up dynamically
        if (!locationTableView.getColumns().contains(locationIDColumn)) {
            locationTableView.getColumns().addAll(locationIDColumn, addressColumn, cityColumn, stateColumn, totalSalesColumn);
        }

        // Define cellValueFactory for each column
        locationIDColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().get("locationID"))));
        addressColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty((String) cellData.getValue().get("address")));
        cityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty((String) cellData.getValue().get("city")));
        stateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty((String) cellData.getValue().get("state")));
        totalSalesColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty((String) cellData.getValue().get("totalSales")));

        String query = "SELECT l.locationID, l.address, l.city, l.state, SUM(i.netSalePrice) AS totalSales " +
                "FROM records r " +
                "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "JOIN location l ON r.locationID = l.locationID " +
                "WHERE YEAR(r.dateOfPurchase) BETWEEN YEAR(CURDATE()) - 1 AND YEAR(CURDATE()) " +
                "GROUP BY l.locationID, l.address, l.city, l.state " +
                "ORDER BY totalSales DESC;";

        // Clear previous data
        locationTableView.getItems().clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Populate TableView with the query result
            ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("locationID", rs.getInt("locationID"));
                row.put("address", rs.getString("address"));
                row.put("city", rs.getString("city"));
                row.put("state", rs.getString("state"));
                row.put("totalSales", String.format("$%,.2f", rs.getDouble("totalSales")));
                data.add(row);
            }
            locationTableView.setItems(data);

            if (data.isEmpty()) {
                // Handle case where no data is found
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Results");
                alert.setHeaderText("No Data Found");
                alert.setContentText("No sales data found for the specified period.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error accessing the database");
            alert.setContentText("An error occurred while querying the database: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVehicleSearch() {
        String userMake = makeField.getText().trim();
        String userModel = modelField.getText().trim();
        String timePeriod = yearRadioButton.isSelected() ? "Yearly" : "Monthly";

        if (userMake.isEmpty() || userModel.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill in both 'Make' and 'Model' fields before searching.");
            alert.showAndWait();
            return;
        }

        vehicleTableView.getItems().clear();
        vehicleTableView.getColumns().clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(getVehicleSearchQuery(timePeriod))) {

            pstmt.setString(1, userMake);
            pstmt.setString(2, userModel);

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Dynamically create columns based on ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                final int columnIndex = i;
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = null;
                    try {
                        cellValue = row.get(metaData.getColumnName(columnIndex));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });
                vehicleTableView.getColumns().add(column);
            }

            // Populate rows with ResultSet data
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                vehicleTableView.getItems().add(row);
            }

            // Check if no data was returned
            if (vehicleTableView.getItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Data Found");
                alert.setHeaderText("No Vehicle Data Available");
                alert.setContentText("No data found for the specified make, model, and time period.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error accessing the database");
            alert.setContentText("An error occurred while querying the database: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCustomerSearch() {
        String make = ctMakeField.getText().trim();
        String model = ctModelField.getText().trim();

        if (make.isEmpty() || model.isEmpty()) {
            // Create and display an error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill in both 'Make' and 'Model' fields before searching.");
            alert.showAndWait();
            return; // Exit the method early if input is invalid
        }

        String query = "SELECT p.personID, p.name, p.email, p.phoneNum, p.address, p.city, p.state, p.zipcode " +
                "FROM customer c " +
                "JOIN person p ON c.personID = p.personID " +
                "JOIN records r ON c.personID = r.customerID " +
                "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "WHERE i.make = ? AND i.model = ?";

        // Clear previous data from TableView
        customerTableView.getItems().clear();
        customerTableView.getColumns().clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, make);
            pstmt.setString(2, model);

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create columns dynamically based on ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                final String columnName = metaData.getColumnName(i); // Must be effectively final
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(columnName);

                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = row.get(columnName);
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });

                customerTableView.getColumns().add(column);
            }

            // Populate rows
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                customerTableView.getItems().add(row);
            }

            // Check if no data was found
            if (customerTableView.getItems().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Results");
                alert.setHeaderText("No Data Found");
                alert.setContentText("No customers found for the specified make and model.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error accessing the database");
            alert.setContentText("An error occurred while querying the database: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTopSellersSearch() {
        topSellersTableView.getItems().clear();
        topSellersTableView.getColumns().clear();

        String query;
        String header;

        if (topRadioButton.isSelected()) {
            query = "SELECT i.make, i.model, COUNT(r.stockNumber) AS salesCount " +
                    "FROM records r " +
                    "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                    "WHERE Year(r.dateOfPurchase) between Year(curdate()) - 1 AND Year(curdate()) " +
                    "GROUP BY i.make, i.model " +
                    "ORDER BY salesCount DESC " +
                    "LIMIT 5";
            header = "Top 5 Best-Selling Cars:";
        } else if (usedRadioButton.isSelected()) {
            query = "SELECT i.make, i.model, COUNT(r.stockNumber) AS salesCount " +
                    "FROM records r " +
                    "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                    "WHERE i.carCondition = 'Used' " +
                    "GROUP BY i.make, i.model " +
                    "ORDER BY salesCount DESC";
            header = "Used Cars by Popularity:";
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Error");
            alert.setHeaderText("No Option Selected");
            alert.setContentText("Please select 'Top' or 'Used' before searching.");
            alert.showAndWait();
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Display header in TableView by creating columns
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Dynamically add columns based on query result
            for (int i = 1; i <= columnCount; i++) {
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(metaData.getColumnName(i));
                final String columnName = metaData.getColumnName(i); // make columnName effectively final
                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = row.get(columnName);
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });
                topSellersTableView.getColumns().add(column);
            }

            // Populate rows in the TableView
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                topSellersTableView.getItems().add(row);
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error accessing the database");
            alert.setContentText("An error occurred while querying the database: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void setDBUser(DBUser currentUser){
        this.currentUser = currentUser ;
        if (!this.currentUser.getIsManager()) {
            // Ensure dbTabPane is properly referenced
            if (dbTabPane != null) {
                // Remove the locationTab if it exists in the TabPane
                if (dbTabPane.getTabs().contains(locationTab)) {
                    dbTabPane.getTabs().remove(locationTab);
                }

                // Remove the employeeTab if it exists in the TabPane
                if (dbTabPane.getTabs().contains(employeeTab)) {
                    dbTabPane.getTabs().remove(employeeTab);
                }
            }
        }
    }
}
