package com.falconcars.www.falconcars;

import com.almasb.fxgl.entity.action.Action;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseController {
    private final String styleSheet = "styles.css";
    // Field for define a successfully logged in user
    private DBUser currentUser;
    @FXML
    private TabPane dbTabPane;
    @FXML
    private Tab locationTab;
    @FXML
    private Tab employeeTab;
    @FXML
    private Tab searchTab ;
    @FXML
    private Tab addVehicleTab;
    @FXML
    private Tab sellVehicleTab;

    // Fields for Vehicle Trends tab
    @FXML
    private TextField makeField;
    @FXML
    private TextField modelField;
    @FXML
    private Button vSearchButton;
    @FXML
    private Button vClearButton;
    @FXML
    private TableView<Map<String, Object>> vehicleTableView;
    @FXML
    private RadioButton yearRadioButton;
    @FXML
    private RadioButton monthRadioButton;

    // Fields for Top Sellers tab
    @FXML
    private RadioButton topRadioButton;
    @FXML
    private RadioButton usedRadioButton;
    @FXML
    private Button topSellersButton;
    @FXML
    private Button clearTopSellersButton;
    @FXML
    private TableView<Map<String, Object>> topSellersTableView;

    // Fields for Customer tab
    @FXML
    private TextField ctMakeField;
    @FXML
    private TextField ctModelField;
    @FXML
    private Button ctSearchButton;
    @FXML
    private Button ctClearButton;
    @FXML
    private TableView<Map<String, Object>> customerTableView;

    // Fields for Locations Tab
    @FXML
    private Button locationUpdateButton;
    @FXML
    private Button locationClearButton;
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
    private Button employeeUpdateButton;
    @FXML
    private Button employeeClearButton;
    @FXML
    private TableView<Map<String, Object>> employeeTableView;

    // Fields for input search tab
    @FXML
    private Button inputSearchButton;
    @FXML
    private Button clearInputSearchButton;
    @FXML
    private TextArea searchTextArea;
    @FXML
    private TableView<Map<String, Object>> searchTableView;

    // Fields for add/update customers
    // Fields for customer details
    @FXML
    private TextField customerIDField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerEmailField;
    @FXML
    private TextField customerPhoneField;
    @FXML
    private TextField customerAddressField;
    @FXML
    private TextField customerCityField;
    @FXML
    private TextField customerStateField;
    @FXML
    private TextField customerZipCodeField;

    // TableView for customer look-up
    @FXML
    private TableView<Map<String, Object>> customerLookUpTableView;
    // Buttons for add/update customers
    @FXML
    private Button addCustomerButton;
    @FXML
    private Button updateCustomerButton;
    @FXML
    private Button customerLookUpButton;
    @FXML
    private Button clearCustomerFormButton;


    // Fields for add/sell vehicle
    @FXML
    private TextField asMakeField;
    @FXML
    private TextField asModelField;
    @FXML
    private TextField asYearField;
    @FXML
    private TextField asColorField;
    // Buttons for actions
    private byte[] imageBytes;
    @FXML
    private TextField asConditionField;
    @FXML
    private TextField asStatusField;
    @FXML
    private TextField asPriceField;
    private int vehicleID ;

    // Buttons for add/sell vehicle
    @FXML
    private Button asAddVehicleButton;
    @FXML
    private Button asSearchVehicleButton;
    @FXML
    private Button asClearVehicleButton;

    @FXML
    private TextField sellVehicleIDField ;
    @FXML
    private TextField sellCustomerIDField ;
    @FXML
    private TextField salesPersonTextField ;

    // Manage Employee Tab fields
    @FXML
    private TextField employeeIDField;
    @FXML
    private TextField employeeNameField;
    @FXML
    private TextField employeeEmailField;
    @FXML
    private TextField employeePhoneField;
    @FXML
    private TextField employeeAddressField;
    @FXML
    private TextField employeeCityField;
    @FXML
    private TextField employeeStateField;
    @FXML
    private TextField employeeZipCodeField;
    @FXML
    private TextField employeeRoleField;
    @FXML
    private Button employeeLookUpButton;
    @FXML
    private Button employeeAddButton;
    @FXML
    private Button employeeSearchButton;
    @FXML
    private Button clearEmployeeFieldsButton;
    @FXML
    private TableView<Map<String, Object>> employeeLookUpTableView ;

    private TableView<Map<String, Object>> asTableView;
    private ToggleGroup vehicleTypeToggleGroup;
    private ToggleGroup newUsedToggleGroup;

    // Database info for connection
    private final String DB_URL = DatabaseConfig.getDbUrl(); // Static call
    private final String DB_USER = DatabaseConfig.getDbUser(); // Static call
    private final String DB_PASSWORD = DatabaseConfig.getDbPassword(); // Static call

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

    private void setupEventHandlers() {
        // Events for Trends Tab
        vSearchButton.setOnAction(event -> handleVehicleSearch());
        vClearButton.setOnAction(event -> clearVTableView());
        // Events for Top Sellers Tabs
        topSellersButton.setOnAction(event -> handleTopSellersSearch());
        clearTopSellersButton.setOnAction(event -> handleClearTopSellers());
        // Events for Customers Tab
        ctSearchButton.setOnAction(event -> handleCustomerSearch());
        ctClearButton.setOnAction(event -> handleCTClear());
        // Events for Add/Update Customer Tab
        customerLookUpButton.setOnAction(event -> handleCustomerLookUp());
        // Events for Location Tab
        locationUpdateButton.setOnAction(event -> handleLocationSearch());
        locationClearButton.setOnAction(event -> handleLocationClear());
        // Events for Employee Tab
        employeeUpdateButton.setOnAction(event -> handleSalespersonUpdate());
        employeeClearButton.setOnAction(event -> handleEmployeeClear());
        //Events for Search Tab
        inputSearchButton.setOnAction(event -> handleInputSearch());
        clearInputSearchButton.setOnAction(event -> handleClearInput());
    }

    private String getVehicleSearchQuery(String timePeriod) {
        return "SELECT YEAR(r.dateOfPurchase) AS purchaseYear, " +
                (timePeriod.equals("Monthly") ? "MONTH(r.dateOfPurchase) AS purchaseMonth, " : "") +
                "COUNT(r.stockNumber) AS numSales, SUM(i.netSalePrice) AS totalSales " +
                "FROM records r " +
                "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "WHERE LOWER(i.make) LIKE LOWER(?) AND LOWER(i.model) LIKE LOWER(?) " +
                "AND Year(r.dateOfPurchase) between Year(curdate()) - 3 AND Year(curdate()) " +
                (timePeriod.equals("Yearly") ? "GROUP BY purchaseYear " : "GROUP BY purchaseYear, purchaseMonth ") +
                "ORDER BY purchaseYear" + (timePeriod.equals("Monthly") ? ", purchaseMonth" : "");
    }

    @FXML
    private void handleClearInput() {
        searchTableView.getItems().clear();
        searchTableView.getColumns().clear();
        searchTextArea.setText(null);
    }

    @FXML
    private void handleInputSearch() {
        String query = searchTextArea.getText().trim();

        if (query.isEmpty() || query.equals("Enter a query")) {
            showAlert("Input Error", "Missing Information", "Please enter a query before pressing search.");
            return;
        }

        if (query.endsWith(";")) {
            query = query.substring(0, query.length() - 1);
        }

        // Check if the query starts with UPDATE or INSERT
        String queryUpper = query.toUpperCase();
        if (!queryUpper.startsWith("SELECT")) {
            showAlert("Invalid Query", "Only SELECT Queries Allowed", "Please enter a valid SELECT query.");
            searchTextArea.setText("");
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
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage());
        }
    }

    @FXML
    private void handleSalespersonUpdate() {

        String query = "SELECT sp.personID, p.name, p.email, p.phoneNum, p.address, p.city, p.state, p.zipcode, " +
                "COUNT(r.stockNumber) AS numSales, " +
                "SUM(CASE WHEN YEAR(r.dateOfPurchase) >= YEAR(CURDATE()) - 1 THEN i.netSalePrice " +
                "* sp.commissionRate ELSE 0 END) AS totalCommission " +
                "FROM sales_person sp " +
                "JOIN person p ON sp.personID = p.personID " +
                "LEFT JOIN records r ON sp.personID = r.salesPersonID AND YEAR(r.dateOfPurchase) >= YEAR(CURDATE()) - 1 " +
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
                showAlert("No Data Found", "No Salesperson Data Available",
                        "No data found for the past year.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage());
        }
    }

    @FXML
    private void handleLocationSearch() {
        // Ensure columns are set up dynamically
        if (!locationTableView.getColumns().contains(locationIDColumn)) {
            locationTableView.getColumns().addAll(
                    locationIDColumn, addressColumn, cityColumn, stateColumn, totalSalesColumn);
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

            // Dynamically create columns based on ResultSet metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing columns before adding new ones
            locationTableView.getColumns().clear();

            for (int i = 1; i <= columnCount; i++) {
                final String columnName = metaData.getColumnName(i);
                final String formattedColumnName = columnName
                        .replaceFirst("(?<=[a-z])(?=[A-Z])", " ") // Split at first capital letter
                        .toUpperCase();

                TableColumn<Map<String, Object>, String> column = new TableColumn<>(formattedColumnName);

                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = row.get(columnName); // Use original column name as key
                    if ("totalSales".equals(columnName) && cellValue instanceof Double) {
                        // Format totalSales as currency
                        return new SimpleStringProperty(String.format("$%,.2f", cellValue));
                    }
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });

                locationTableView.getColumns().add(column);
            }

            // Populate data into TableView rows
            ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    row.put(columnName, rs.getObject(columnName)); // Use column name as key
                }
                data.add(row);
            }
            locationTableView.setItems(data);

            // Show alert if no data is found
            if (data.isEmpty()) {
                showAlert("No Results", "No Data Found", "No sales data found for the specified period.");
            }
        } catch (SQLException e) {
            // Handle database errors gracefully
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage());
        }
    }

    @FXML
    private void handleVehicleSearch() {
        String userMake = makeField.getText().trim();
        String userModel = modelField.getText().trim();
        String timePeriod = yearRadioButton.isSelected() ? "Yearly" : "Monthly";

        if (userMake.isEmpty() || userModel.isEmpty()) {
            showAlert("Input Error", "Missing Information", "Please fill in both 'Make' and 'Model' fields before searching.");
            return;
        }

        vehicleTableView.getItems().clear();
        vehicleTableView.getColumns().clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(getVehicleSearchQuery(timePeriod))) {

            pstmt.setString(1, "%" + userMake + "%");
            pstmt.setString(2, "%" + userModel + "%");


            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Dynamically create columns based on ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                final int columnIndex = i;
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(metaData.getColumnName(i).replaceAll("(?<!^)(?=[A-Z])", " ").toUpperCase());
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
                column.setPrefWidth(metaData.getColumnName(i).length() * 20);
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
                showAlert("No Data Found", "No Vehicle Data Available",
                        "No data found for the specified make, model, and time period.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage());
        }
    }

    @FXML
    private void handleCustomerSearch() {
        String make = ctMakeField.getText().trim();
        String model = ctModelField.getText().trim();

        if (make.isEmpty() || model.isEmpty()) {
            showAlert("Input Error", "Missing Information",
                    "Please fill in both 'Make' and 'Model' fields before searching.");
            return; // Exit the method early if input is invalid
        }

        String query = "SELECT p.personID, p.name, p.email, p.phoneNum, p.address, p.city, p.state, p.zipcode " +
                "FROM customer c " +
                "JOIN person p ON c.personID = p.personID " +
                "JOIN records r ON c.personID = r.customerID " +
                "JOIN inventory i ON r.stockNumber = i.stockNumber " +
                "WHERE LOWER(i.make) LIKE LOWER(?) AND LOWER(i.model) LIKE LOWER(?)";

        // Clear previous data from TableView
        customerTableView.getItems().clear();
        customerTableView.getColumns().clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + make + "%");
            pstmt.setString(2, "%" + model + "%");

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

// Create columns dynamically based on ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                // Get column name and format it (split at the first capital letter)
                final String originalColumnName = metaData.getColumnName(i);
                final String formattedColumnName = originalColumnName
                        .replaceFirst("(?<=[a-z])(?=[A-Z])", " ")
                        .toUpperCase(); // Add space before the first capital and convert to uppercase

                // Create table column with the formatted name
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(formattedColumnName);

                // Set cell value factory to populate column cells with corresponding row data
                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = row.get(originalColumnName); // Use original column name for the key
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });
                column.setPrefWidth(metaData.getColumnName(i).length() * 20);
                customerTableView.getColumns().add(column);
            }

            // Populate rows dynamically
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i)); // Use original column name as key
                }
                customerTableView.getItems().add(row);
            }


            // Check if no data was found
            if (customerTableView.getItems().isEmpty()) {
                showAlert("No Results", "No Data Found",
                        "No customers found for the specified make and model.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage());
        }
    }

    // Search for top-selling vehicles by year or month
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
            showAlert("Selection Error", "No Option Selected",
                    "Please select 'Top' or 'Used' before searching.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Display header in TableView by creating columns
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String formattedColumnName = columnName.replaceFirst("(?<=[a-z])(?=[A-Z])", " ").toUpperCase();

                TableColumn<Map<String, Object>, String> column = new TableColumn<>(formattedColumnName);
                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = row.get(columnName); // Use unmodified column name as key
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });
                column.setPrefWidth(formattedColumnName.length() * 20);
                topSellersTableView.getColumns().add(column);
            }

            // Populate rows in the TableView
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i); // Use unmodified column name
                    row.put(columnName, rs.getObject(i));
                }
                topSellersTableView.getItems().add(row);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage());
        }
    }


    // Wrapper to streamline insert person-customer
    @FXML
    private void handleAddCustomer() {
        String name = customerNameField.getText();
        String email = customerEmailField.getText();
        String phone = customerPhoneField.getText();
        String address = customerAddressField.getText();
        String city = customerCityField.getText();
        String state = customerStateField.getText();
        String zipCode = customerZipCodeField.getText();

        if (!validateInput(name, email, phone, address, city, state, zipCode)) {
            showAlert("ERROR", "Please fill in all fields correctly!", "");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Begin transaction

            int personID = insertPerson(conn, name, email, phone, address, city, state, zipCode);
            insertCustomer(conn, personID);

            conn.commit(); // Commit transaction
            showAlert("SUCCESS", "Customer added successfully!", name + " added!");
            clearCustomerForm();
        } catch (SQLException e) {
            showAlert("ERROR", "Error adding customer", e.getMessage());
        }
    }

    // Handle the customer look up for Vehicle tab
    @FXML
    private void handleCustomerLookUp() {
        if (!isCustomerLookUpInputValid()) {
            showAlert("Input Error", "Missing Information",
                    "Please enter at least one of the following: Customer ID, Name, or Email.");
            return;
        }

        String query = "SELECT p.personID, p.name, p.email, p.phoneNum, p.address, p.city, p.state, p.zipcode " +
                "FROM customer c " +
                "JOIN person p ON c.personID = p.personID " +
                "WHERE ";
        boolean hasCondition = false;

        if (!customerNameField.getText().trim().isEmpty()) {
            if (hasCondition) query += "AND ";
            query += "p.name LIKE ? ";
            hasCondition = true;
        }
        if (!customerEmailField.getText().trim().isEmpty()) {
            if (hasCondition) query += "AND ";
            query += "p.email LIKE ? ";
        }
        if (!customerPhoneField.getText().trim().isEmpty()) {
            if (hasCondition) query += "AND ";
            query += "p.phoneNum LIKE ? ";
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            if (!customerNameField.getText().trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + customerNameField.getText().trim() + "%");
            }
            if (!customerEmailField.getText().trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + customerEmailField.getText().trim() + "%");
            }
            if (!customerPhoneField.getText().trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + customerPhoneField.getText().trim() + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear previous data and columns
            customerLookUpTableView.getItems().clear();
            customerLookUpTableView.getColumns().clear();

            // Dynamically create columns based on ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                final String columnName = metaData.getColumnName(i).replaceFirst("(?<=[a-z])(?=[A-Z])", " ").toUpperCase();
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(columnName);

                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = row.get(columnName);
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });
                column.setPrefWidth(metaData.getColumnName(i).length() * 20);
                customerLookUpTableView.getColumns().add(column);
            }

            // Populate rows
            ObservableList<Map<String, Object>> results = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i).replaceFirst("(?<=[a-z])(?=[A-Z])", " ").toUpperCase(), rs.getObject(i));
                }
                results.add(row);
            }

            customerLookUpTableView.setItems(results);
            customerLookUpTableView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // Double-click event
                    Map<String, Object> selectedItem = customerLookUpTableView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        customerIDField.setText(selectedItem.get("PERSON ID").toString());
                        customerNameField.setText(selectedItem.get("NAME").toString());
                        customerEmailField.setText(selectedItem.get("EMAIL").toString());
                        customerPhoneField.setText(selectedItem.get("PHONE NUM").toString());
                        customerAddressField.setText(selectedItem.get("ADDRESS").toString());
                        customerCityField.setText(selectedItem.get("CITY").toString());
                        customerStateField.setText(selectedItem.get("STATE").toString());
                        customerZipCodeField.setText(selectedItem.get("ZIPCODE").toString());
                    }
                }
            });

            // Check if no data was found
            if (results.isEmpty()) {
                showAlert("No Results", "No Data Found",
                        "No customers found for the specified criteria.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage());
        }
    }

    // Helper to confirm customer look up input is valid
    private boolean isCustomerLookUpInputValid() {
        return !customerNameField.getText().trim().isEmpty() ||
                !customerEmailField.getText().trim().isEmpty() ||
                !customerPhoneField.getText().trim().isEmpty();
    }

    // clear the customer form
    @FXML
    public void handleClearCustomerForm() {
        customerIDField.clear();
        customerNameField.clear();
        customerEmailField.clear();
        customerPhoneField.clear();
        customerAddressField.clear();
        customerCityField.clear();
        customerStateField.clear();
        customerZipCodeField.clear();
        customerLookUpTableView.getColumns().clear();
    }

    // Update existing customer in the database
    @FXML
    private void handleUpdateCustomer() {
        String name = customerNameField.getText();
        String email = customerEmailField.getText();
        String phone = customerPhoneField.getText();
        String address = customerAddressField.getText();
        String city = customerCityField.getText();
        String state = customerStateField.getText();
        String zipCode = customerZipCodeField.getText();
        String id = customerIDField.getText();

        if (!validateInput(name, email, phone, address, city, state, zipCode)) {
            showAlert("ERROR", "Please fill in all fields correctly!", "");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Begin transaction

            updatePerson(conn, name, email, phone, address, city, state, zipCode, id);

            conn.commit(); // Commit transaction
            showAlert("SUCCESS", "Customer added successfully!", name + " added!");
            clearCustomerForm();
        } catch (SQLException e) {
            showAlert("ERROR", "Error adding customer", e.getMessage());
        }
    }

    // Helper to check for null values on input customer
    private boolean validateInput(String name, String email, String phone, String address, String city, String state, String zipCode) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() ||
                city.isEmpty() || state.isEmpty() || zipCode.isEmpty()) {
            return false;
        }
        return true;
    }

    // Insert a new person into the database
    private int insertPerson(Connection conn, String name, String email, String phone, String address, String city, String state, String zipCode) throws SQLException {
        String insertPersonSQL = "INSERT INTO person (name, email, phoneNum, address, city, state, zipcode) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement personStmt = conn.prepareStatement(insertPersonSQL, Statement.RETURN_GENERATED_KEYS)) {
            personStmt.setString(1, name);
            personStmt.setString(2, email);
            personStmt.setString(3, phone);
            personStmt.setString(4, address);
            personStmt.setString(5, city);
            personStmt.setString(6, state);
            personStmt.setString(7, zipCode);
            personStmt.executeUpdate();

            ResultSet rs = personStmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return generated personID
            } else {
                throw new SQLException("Failed to retrieve personID.");
            }
        }
    }

    // Updates current Customer Selected
    private void updatePerson(Connection conn, String name, String email, String phone, String address, String city, String state, String zipCode, String personID) throws SQLException {
        String insertPersonSQL = "UPDATE person SET name = ?, email = ?, phoneNum = ?, address = ?, city = ?, state = ?, zipCode = ? WHERE personID = ?";
        try (PreparedStatement personStmt = conn.prepareStatement(insertPersonSQL, Statement.RETURN_GENERATED_KEYS)) {
            personStmt.setString(1, name);
            personStmt.setString(2, email);
            personStmt.setString(3, phone);
            personStmt.setString(4, address);
            personStmt.setString(5, city);
            personStmt.setString(6, state);
            personStmt.setString(7, zipCode);
            personStmt.setString(8, personID);
            personStmt.executeUpdate();
        }
    }

    // Insert a new customer into the database
    private void insertCustomer(Connection conn, int personID) throws SQLException {
        String insertCustomerSQL = "INSERT INTO customer (personID) VALUES (?)";
        try (PreparedStatement customerStmt = conn.prepareStatement(insertCustomerSQL)) {
            customerStmt.setInt(1, personID);
            customerStmt.executeUpdate();
        }
    }

    // Clear the customer form
    private void clearCustomerForm() {
        customerNameField.clear();
        customerEmailField.clear();
        customerPhoneField.clear();
        customerAddressField.clear();
        customerCityField.clear();
        customerStateField.clear();
        customerZipCodeField.clear();
    }

    // Set the DBUser
    public void setDBUser(DBUser currentUser) {
        this.currentUser = currentUser;
        if(dbTabPane != null){
            dbTabPane.getTabs().remove(searchTab) ;
        }

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

                if(dbTabPane.getTabs().contains(addVehicleTab)){
                    dbTabPane.getTabs().remove(addVehicleTab);
                }

                if(dbTabPane.getTabs().contains(sellVehicleTab)){
                    dbTabPane.getTabs().remove(sellVehicleTab);
                }
            }
        }
    }

    // Clear Vehicle Tab
    @FXML
    private void clearVTableView() {
        vehicleTableView.getItems().clear();
        vehicleTableView.getColumns().clear();
        vehicleTableView.getSelectionModel().clearSelection();
        vehicleTableView.refresh();
        makeField.setText("");
        modelField.setText("");
    }

    // Clear Top Sellers Tab
    private void handleClearTopSellers() {
        topSellersTableView.getColumns().clear();
        vehicleTableView.getItems().clear();
        vehicleTableView.getSelectionModel().clearSelection();
        vehicleTableView.refresh();
    }

    // Clear Customer Tab
    private void handleCTClear() {

        customerTableView.getColumns().clear();
        customerTableView.getItems().clear();
        customerTableView.getSelectionModel().clearSelection();
        customerTableView.refresh();

        ctMakeField.setText("");
        ctModelField.setText("");
    }

    // Clear location Tab
    private void handleLocationClear() {
        locationTableView.getColumns().clear();
        locationTableView.getItems().clear();
        locationTableView.getSelectionModel().clearSelection();
        locationTableView.refresh();
    }

    // Clear employee Tab
    private void handleEmployeeClear() {
        employeeTableView.getColumns().clear();
        employeeTableView.getItems().clear();
        employeeTableView.getSelectionModel().clearSelection();
        employeeTableView.refresh();
    }

    // Shows most alerts with three arguments
    private void showAlert(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        styleAlert(alert);
    }

    @FXML
    private void handleSelectFile() {
        imageBytes = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("db/photos"));
        fileChooser.setTitle("Select File");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                imageBytes = Files.readAllBytes(selectedFile.toPath());
                Image image = new Image(new ByteArrayInputStream(imageBytes));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(300);
                imageView.setFitHeight(300);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Image");
                alert.setHeaderText("Do you want to use this image?");
                alert.setGraphic(imageView);
                styleAlert(alert);

                ButtonType buttonYes = new ButtonType("Yes");
                ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(buttonYes, buttonNo);

                alert.showAndWait().ifPresent(response -> {
                    if (response != buttonYes) {
                        imageBytes = null;
                    }
                });
            } catch (IOException e) {
                showAlert("File Error", "Error reading file", e.getMessage());
            }
        }
    }

    // Styling function to apply consistent styles
    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource(styleSheet).toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }

    // Clear the Sell Vehicle Tab
    @FXML
    private void handleASClear(ActionEvent actionEvent) {
        // Clear all text fields
        asMakeField.clear();
        asModelField.clear();
        asYearField.clear();
        asColorField.clear();
        // Reset the imageBytes to null
        imageBytes = null;
        asConditionField.clear();
        asStatusField.clear();
        asPriceField.clear();
        asTableView.getColumns().clear();
    }

    // Search on Vehicle Add/Sell Tab
    @FXML
    private void handleASSearch(ActionEvent actionEvent) {
        String make = asMakeField.getText().trim();
        String model = asModelField.getText().trim();
        String year = asYearField.getText().trim();
        String color = asColorField.getText().trim();
        String condition = asConditionField.getText().trim();
        String status = asStatusField.getText().trim();
        String price = asPriceField.getText().trim();

        if (make.isEmpty() && model.isEmpty() && year.isEmpty() && color.isEmpty() && condition.isEmpty() && status.isEmpty() && price.isEmpty()) {
            showAlert("Input Error", "Missing Information", "Please fill in at least one field before searching.");
            return;
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM inventory WHERE ");
        boolean hasCondition = false;

        if (!make.isEmpty()) {
            if (hasCondition) queryBuilder.append("AND ");
            queryBuilder.append("LOWER(make) LIKE LOWER(?) ");
            hasCondition = true;
        }
        if (!model.isEmpty()) {
            if (hasCondition) queryBuilder.append("AND ");
            queryBuilder.append("LOWER(model) LIKE LOWER(?) ");
            hasCondition = true;
        }
        if (!year.isEmpty()) {
            if (hasCondition) queryBuilder.append("AND ");
            queryBuilder.append("YEAR = ? ");
            hasCondition = true;
        }
        if (!color.isEmpty()) {
            if (hasCondition) queryBuilder.append("AND ");
            queryBuilder.append("LOWER(color) LIKE LOWER(?) ");
            hasCondition = true;
        }
        if (!condition.isEmpty()) {
            if (hasCondition) queryBuilder.append("AND ");
            queryBuilder.append("LOWER(carCondition) LIKE LOWER(?) ");
            hasCondition = true;
        }
        if (!status.isEmpty()) {
            if (hasCondition) queryBuilder.append("AND ");
            queryBuilder.append("LOWER(status) LIKE LOWER(?) ");
            hasCondition = true;
        }
        if (!price.isEmpty()) {
            if (hasCondition) queryBuilder.append("AND ");
            queryBuilder.append("netSalePrice = ? ");
        }

        String query = queryBuilder.toString();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            if (!make.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + make + "%");
            }
            if (!model.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + model + "%");
            }
            if (!year.isEmpty()) {
                pstmt.setString(paramIndex++, year);
            }
            if (!color.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + color + "%");
            }
            if (!condition.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + condition + "%");
            }
            if (!status.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + status + "%");
            }
            if (!price.isEmpty()) {
                pstmt.setString(paramIndex++, price);
            }

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Dynamically create columns based on ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                final String columnNameReal = metaData.getColumnName(i);
                final String columnName = metaData.getColumnName(i).replaceAll("(?=[A-Z])", " ").toUpperCase();
                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnName);

                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = row.get(columnNameReal);
                    if (cellValue instanceof byte[]) {
                        byte[] imageBytes = (byte[]) cellValue;
                        if (imageBytes != null) {
                            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(imageBytes)));
                            imageView.setFitWidth(100);
                            imageView.setFitHeight(100);
                            return new SimpleObjectProperty<>(imageView);
                        } else {
                            return new SimpleObjectProperty<>("No Image");
                        }
                    } else {
                        return new SimpleObjectProperty(cellValue == null ? "NULL" : cellValue.toString());
                    }
                });
                column.setPrefWidth(metaData.getColumnName(i).length() * 20);
                asTableView.getColumns().add(column);
            }

            // Populate rows with ResultSet data
            ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                data.add(row);
            }
            asTableView.setItems(data);

            // Add action listener to handle row selection
            asTableView.setRowFactory(tv -> {
                TableRow<Map<String, Object>> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Map<String, Object> rowData = row.getItem();
                        imageBytes = (byte[]) rowData.get("image");
                        if (imageBytes != null) {
                            // create a pop out to display larger image with details
                            Image image = new Image(new ByteArrayInputStream(imageBytes));
                            ImageView imageView = new ImageView(image);
                            imageView.setFitWidth(400);
                            imageView.setFitHeight(300);
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Vehicle Image");
                            alert.setHeaderText("Vehicle Image");
                            alert.setGraphic(imageView);
                            styleAlert(alert);
                        } else {
                            showAlert("No Image", "No Image Available", "This vehicle does not have an image.");
                        }
                    }
                });
                return row;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleSellVehicle(ActionEvent actionEvent) {
        try {
            // Get and validate inputs
            String tempStockNumber = sellVehicleIDField.getText().trim();
            int stockNum = Integer.parseInt(tempStockNumber);
            String tempCustomerID = sellCustomerIDField.getText().trim();
            int customerID = Integer.parseInt(tempCustomerID);
            String tempSalesPersonID = salesPersonTextField.getText().trim();
            int salesPersonID = Integer.parseInt(tempSalesPersonID);
            String tempLocation = this.currentUser.getLocation();
            int locationID = Integer.parseInt(tempLocation);

            // Get current date
            LocalDate dateOfPurchase = LocalDate.now();

            // Prepare SQL INSERT
            String sql = "INSERT INTO records (stockNumber, salesPersonID, customerID, locationID, dateOfPurchase) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, stockNum); // stockNumber
                pstmt.setInt(2, salesPersonID); // salesPersonID
                pstmt.setInt(3, customerID); // customerID
                pstmt.setInt(4, locationID); // locationID
                pstmt.setDate(5, Date.valueOf(dateOfPurchase)); // dateOfPurchase as java.sql.Date

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Success", "Vehicle Sold", "The vehicle sale has been successfully recorded!");
                } else {
                    showAlert("Error", "Operation Failed", "Failed to record the sale. Please try again.");
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Input", "Please enter valid numeric values for all fields.");
        } catch (SQLException e) {
            showAlert("Error", "Database Error", "An error occurred while accessing the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSellClear(ActionEvent actionEvent) {
        sellVehicleIDField.clear();
        sellCustomerIDField.clear();
    }

    @FXML
    private void handleEmployeeLookUp(ActionEvent actionEvent) {
        String query = "SELECT * FROM employee e JOIN person p ON e.personID = p.personID";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Dynamically create columns based on ResultSet metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing columns before adding new ones
            employeeTableView.getColumns().clear();

            for (int i = 1; i <= columnCount; i++) {
                final String columnName = metaData.getColumnName(i);
                final String formattedColumnName = columnName
                        .replaceFirst("(?<=[a-z])(?=[A-Z])", " ") // Split at first capital letter
                        .toUpperCase();

                TableColumn<Map<String, Object>, String> column = new TableColumn<>(formattedColumnName);

                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = row.get(columnName); // Use original column name as key
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });

                employeeTableView.getColumns().add(column);
            }

            // Populate data into TableView rows
            ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    row.put(columnName, rs.getObject(columnName)); // Use column name as key
                }
                data.add(row);
            }
            employeeTableView.setItems(data);

            // Show alert if no data is found
            if (data.isEmpty()) {
                showAlert("No Results", "No Data Found", "No sales data found for the specified period.");
            }

        } catch (SQLException e) {
            // Handle database errors gracefully
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage());
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Dynamically create columns based on ResultSet metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing columns before adding new ones
            employeeLookUpTableView.getColumns().clear();

            for (int i = 1; i <= columnCount; i++) {
                final String columnName = metaData.getColumnName(i);
                final String formattedColumnName = columnName
                        .replaceFirst("(?<=[a-z])(?=[A-Z])", " ") // Split at first capital letter
                        .toUpperCase();

                TableColumn<Map<String, Object>, String> column = new TableColumn<>(formattedColumnName);

                column.setCellValueFactory(cellData -> {
                    Map<String, Object> row = cellData.getValue();
                    Object cellValue = row.get(columnName); // Use original column name as key
                    return new SimpleStringProperty(cellValue == null ? "NULL" : cellValue.toString());
                });

                employeeLookUpTableView.getColumns().add(column);
            }

            // Populate data into TableView rows
            ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    row.put(columnName, rs.getObject(columnName)); // Use column name as key
                }
                data.add(row);
            }
            employeeLookUpTableView.setItems(data);

            // Action listener on table to load data into text fields
            employeeLookUpTableView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Map<String, Object> selectedItem = employeeLookUpTableView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        employeeIDField.setText(selectedItem.get("PERSONID").toString());
                        employeeNameField.setText(selectedItem.get("NAME").toString());
                        employeeEmailField.setText(selectedItem.get("EMAIL").toString());
                        employeePhoneField.setText(selectedItem.get("PHONENUM").toString());
                        employeeAddressField.setText(selectedItem.get("ADDRESS").toString());
                        employeeCityField.setText(selectedItem.get("CITY").toString());
                        employeeStateField.setText(selectedItem.get("STATE").toString());
                        employeeZipCodeField.setText(selectedItem.get("ZIPCODE").toString());
                        employeeRoleField.setText(selectedItem.get("ROLE").toString());
                    }
                }
            });

            // Show alert if no data is found
            if (data.isEmpty()) {
                showAlert("No Results", "No Data Found", "No sales data found for the specified period.");
            }

        } catch (SQLException e) {
            // Handle database errors gracefully
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddEmployee(ActionEvent actionEvent) {
        String name = employeeNameField.getText();
        String email = employeeEmailField.getText();
        String phone = employeePhoneField.getText();
        String address = employeeAddressField.getText();
        String city = employeeCityField.getText();
        String state = employeeStateField.getText();
        String zipCode = employeeZipCodeField.getText();
        String role = employeeRoleField.getText(); // Position

        if(name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || zipCode.isEmpty() || role.isEmpty()){
            showAlert("ERROR", "Please fill in all fields correctly!", "");
            return;
        }

        // if valid, prompt for username and password
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Account");
        dialog.setHeaderText("Enter Username");
        dialog.setContentText("Username:");

        // Apply custom stylesheet
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return; // Exit the method if the user cancels the dialog
        }
        String username = result.get();

        dialog.setHeaderText("Enter Password");
        dialog.setContentText("Password:");

        result = dialog.showAndWait();


        if (result.isEmpty()) {
            return; // Exit the method if the user cancels the dialog
        }
        String password = result.get();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Begin transaction

            int personID = insertPerson(conn, name, email, phone, address, city, state, zipCode);
            insertSalesPerson(conn, personID, username, password);

            if (role.equalsIgnoreCase("manager")) {
                insertManager(conn, personID);
            }
            insertSalesPerson(conn, personID, username, password);
            conn.commit(); // Commit transaction
            showAlert("SUCCESS", "Employee added successfully!", name + " added!");
            clearEmployeeForm();
        } catch (SQLException e) {
            showAlert("ERROR", "Error adding employee", e.getMessage());
        }
    }

    private void insertSalesPerson(Connection conn, int personID, String username, String password) throws SQLException {
        String insertSalesPersonSQL = "INSERT INTO sales_person (personID, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement salesPersonStmt = conn.prepareStatement(insertSalesPersonSQL)) {
            salesPersonStmt.setInt(1, personID);
            salesPersonStmt.setString(2, username);
            salesPersonStmt.setString(3, password);
            salesPersonStmt.executeUpdate();
        }
    }

    private void insertManager(Connection conn, int personID) throws SQLException {
        String insertManagerSQL = "INSERT INTO manager (personID) VALUES (?)";
        try (PreparedStatement managerStmt = conn.prepareStatement(insertManagerSQL)) {
            managerStmt.setInt(1, personID);
            managerStmt.executeUpdate();
        }
    }

    @FXML
    private void handleClearEmployeeForm(ActionEvent actionEvent){
        clearEmployeeForm();
    }

    private void clearEmployeeForm() {
        employeeNameField.clear();
        employeeEmailField.clear();
        employeePhoneField.clear();
        employeeAddressField.clear();
        employeeCityField.clear();
        employeeStateField.clear();
        employeeZipCodeField.clear();
        employeeRoleField.clear();

        employeeLookUpTableView.getColumns().clear();
    }

    @FXML
    private void handleDeleteEmployee(){
        String personID = employeeIDField.getText().trim();
        String role = employeeRoleField.getText().trim();
        if(personID.isEmpty()){
            showAlert("ERROR", "Please fill in all fields correctly!", "");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Begin transaction

            deletePerson(conn, personID);

            conn.commit(); // Commit transaction
            showAlert("SUCCESS", "Employee deleted successfully!", personID + " deleted!");
            clearEmployeeForm();
        } catch (SQLException e) {
            showAlert("ERROR", "Error deleting employee", e.getMessage());
        }
    }

    private void deletePerson(Connection conn, String personID) throws SQLException {
        String deletePersonSQL = "DELETE FROM person WHERE personID = ?";
        try (PreparedStatement personStmt = conn.prepareStatement(deletePersonSQL)) {
            personStmt.setString(1, personID);
            personStmt.executeUpdate();
        }
    }

    private void deleteEmployee(Connection conn, String personID) throws SQLException {
        String deleteEmployeeSQL = "DELETE FROM employee WHERE personID = ?";
        try (PreparedStatement employeeStmt = conn.prepareStatement(deleteEmployeeSQL)) {
            employeeStmt.setString(1, personID);
            employeeStmt.executeUpdate();
        }
    }

    private void deleteManager(Connection conn, String personID) throws SQLException {
        String deleteManagerSQL = "DELETE FROM manager WHERE personID = ?";
        try (PreparedStatement managerStmt = conn.prepareStatement(deleteManagerSQL)) {
            managerStmt.setString(1, personID);
            managerStmt.executeUpdate();
        }
    }

}// End Class
