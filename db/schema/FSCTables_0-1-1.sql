CREATE TABLE location(
	locationID INT PRIMARY KEY AUTO_INCREMENT,
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    zipcode INT,
    phoneNum INT,
    personID INT,
    FOREIGN KEY (personID) REFERENCES sales_person(personID)
);
CREATE TABLE person(
	personID INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255),
    phoneNum INT,
    address VARCHAR(255),
	city VARCHAR(255),
    state VARCHAR(255),
    zipcode INT
);
CREATE TABLE sales_person(
	personID INT PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    commissionRate DECIMAL(5,2),
    FOREIGN KEY (personID) REFERENCES person(personID)
);
CREATE TABLE manager(
	personID INT PRIMARY KEY,
    FOREIGN KEY (personID) REFERENCES sales_person(personID)
);
CREATE TABLE customer(
	personID INT PRIMARY KEY,
    FOREIGN KEY (personID) REFERENCES person(personID)
);
CREATE TABLE inventory(
	stockNumber INT PRIMARY KEY AUTO_INCREMENT,
    make VARCHAR(255),
    model VARCHAR(255),
    year INT,
    color VARCHAR(255),
    image BLOB,
    carCondition VARCHAR(255),
    netSalePrice INT,
    locationID INT,
    FOREIGN KEY (locationID) REFERENCES location(locationID)
);
CREATE TABLE records(
	stockNumber INT,
    salesPersonID INT,
    customerID INT,
    locationID INT,
    dateOfPurchase DATE,
    PRIMARY KEY(stockNumber, salesPersonID, customerID, locationID),
    FOREIGN KEY (stockNumber) REFERENCES inventory(stockNumber),
    FOREIGN KEY (salesPersonID) REFERENCES sales_person(personID),
    FOREIGN KEY (customerID) REFERENCES customer(personID),
    FOREIGN KEY (locationID) REFERENCES location(locationID)
);