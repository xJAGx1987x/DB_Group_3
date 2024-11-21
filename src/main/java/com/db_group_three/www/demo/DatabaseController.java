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
    private Button vClearButton ;
    @FXML
    private TableView<Map<String, Object>> vehicleTableView ;
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
    private Button clearTopSellersButton ;
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
    private Button ctClearButton ;
    @FXML
    private TableView<Map<String, Object>> customerTableView;

    // Fields for Locations Tab
    @FXML
    private Button locationUpdateButton ;
    @FXML
    private Button locationClearButton ;
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
    private Button employeeClearButton ;
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

    // Fields for add/update customers
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

    private ToggleGroup vehicleTypeToggleGroup;
    private ToggleGroup newUsedToggleGroup;

    // Database info for connection
    private final String DB_URL
        = "jdbc:mysql://database-2.cns6g8eseo17.us-east-2.rds.amazonaws.com:3306" +
            "/FalconSportsCar?useLegacyDatetimeCode=false&serverTimezone=America/New_York";
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

    private void setupEventHandlers() {
        // Events for Trends Tab
        vSearchButton.setOnAction(event -> handleVehicleSearch());
        vClearButton.setOnAction(event -> clearVTableView() ) ;
        // Events for Top Sellers Tabs
        topSellersButton.setOnAction(event -> handleTopSellersSearch());
        clearTopSellersButton.setOnAction(event -> handleClearTopSellers() );
        // Events for Customers Tab
        ctSearchButton.setOnAction(event -> handleCustomerSearch());
        ctClearButton.setOnAction(event -> handleCTClear() );
        // Events for Location Tab
        locationUpdateButton.setOnAction(event -> handleLocationSearch() );
        locationClearButton.setOnAction(event -> handleLocationClear() );
        // Events for Employee Tab
        employeeUpdateButton.setOnAction(event -> handleSalespersonUpdate() );
        employeeClearButton.setOnAction(event -> handleEmployeeClear() );
        //Events for Search Tab
        inputSearchButton.setOnAction(event -> handleInputSearch() ) ;
        clearInputSearchButton.setOnAction(event -> handleClearInput() );
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
        searchTableView.getItems().clear() ;
        searchTableView.getColumns().clear();
        searchTextArea.setText(null) ;
    }

    @FXML
    private void handleInputSearch() {
        String query = searchTextArea.getText().trim();

        if (query.isEmpty() || query.equals("Enter a query")) {
            showAlert("Input Error","Missing Information","Please enter a query before pressing search.");
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
            showAlert("Invalid Query","Only SELECT Queries Allowed","Please enter a valid SELECT query.");
            searchTextArea.setText("") ;
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
            "SUM(CASE WHEN YEAR(r.dateOfPurchase) = YEAR(CURDATE()) - 1 THEN i.netSalePrice " +
            "* sp.commissionRate ELSE 0 END) AS totalCommission " +
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
                showAlert("No Data Found","No Salesperson Data Available",
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
                showAlert("No Results","No Data Found",
                        "No sales data found for the specified period.");
            }
        } catch (SQLException e) {
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
            showAlert("Input Error", "Missing Information","Please fill in both 'Make' and 'Model' fields before searching.");
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
                showAlert("No Data Found", "No Vehicle Data Available",
                        "No data found for the specified make, model, and time period.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage() );
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

            pstmt.setString(1,"%"+make+"%");
            pstmt.setString(2, "%"+model+"%");

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create columns dynamically based on ResultSet metadata
            for (int i = 1; i <= columnCount; i++) {
                final String columnName = metaData.getColumnName(i);
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
                showAlert("No Results", "No Data Found",
                        "No customers found for the specified make and model.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage() );
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
            showAlert("Selection Error","No Option Selected",
                    "Please select 'Top' or 'Used' before searching.");
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
            showAlert("Database Error","Error accessing the database",
                    "An error occurred while querying the database: " + e.getMessage() );
        }
    }

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
    @FXML
    private void handleUpdateCustomer(){
        return ;
    }

    @FXML
    public void handleClearCustomerForm() {
        customerNameField.clear();
        customerEmailField.clear();
        customerPhoneField.clear();
        customerAddressField.clear();
        customerCityField.clear();
        customerStateField.clear();
        customerZipCodeField.clear();
    }

    private boolean validateInput(String name, String email, String phone, String address, String city, String state, String zipCode) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() ||
                city.isEmpty() || state.isEmpty() || zipCode.isEmpty()) {
            return false;
        }
        return true;
    }

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

    private void insertCustomer(Connection conn, int personID) throws SQLException {
        String insertCustomerSQL = "INSERT INTO customer (personID) VALUES (?)";
        try (PreparedStatement customerStmt = conn.prepareStatement(insertCustomerSQL)) {
            customerStmt.setInt(1, personID);
            customerStmt.executeUpdate();
        }
    }

    private void clearCustomerForm() {
        customerNameField.clear();
        customerEmailField.clear();
        customerPhoneField.clear();
        customerAddressField.clear();
        customerCityField.clear();
        customerStateField.clear();
        customerZipCodeField.clear();
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

    private void clearVTableView(){
        vehicleTableView.getItems().clear();
        vehicleTableView.getColumns().clear() ;
        vehicleTableView.getSelectionModel().clearSelection();
        vehicleTableView.refresh();
        makeField.setText("") ;
        modelField.setText("") ;
    }

    private void handleClearTopSellers() {
        topSellersTableView.getColumns().clear();
        vehicleTableView.getItems().clear();
        vehicleTableView.getSelectionModel().clearSelection();
        vehicleTableView.refresh();
    }

    private void handleCTClear(){

        customerTableView.getColumns().clear();
        customerTableView.getItems().clear();
        customerTableView.getSelectionModel().clearSelection();
        customerTableView.refresh();

        ctMakeField.setText("");
        ctModelField.setText("") ;
    }

    private void handleLocationClear() {
        locationTableView.getColumns().clear();
        locationTableView.getItems().clear();
        locationTableView.getSelectionModel().clearSelection();
        locationTableView.refresh();
    }

    private void handleEmployeeClear() {
        employeeTableView.getColumns().clear();
        employeeTableView.getItems().clear();
        employeeTableView.getSelectionModel().clearSelection();
        employeeTableView.refresh();
    }

    private void showAlert(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        styleAlert(alert);
    }

    // Styling function to apply consistent styles
    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }
}
