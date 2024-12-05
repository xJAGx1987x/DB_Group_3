USE FalconSportsCar;

#1. Sales trends for make and model over past three years by year and month
# replace 'GivenMake' and 'GivenModel' with make and model searched for
DELIMITER //

CREATE PROCEDURE GetSalesTrendsByMakeModel(IN givenMake VARCHAR(255), IN givenModel VARCHAR(255))
BEGIN
    SELECT 
        YEAR(dateOfPurchase) AS year, 
        MONTH(dateOfPurchase) AS month, 
        COUNT(*) AS total_sales
    FROM 
        records r
    JOIN 
        inventory i ON r.stockNumber = i.stockNumber
    WHERE 
        i.make = givenMake AND 
        i.model = givenModel AND 
        dateOfPurchase >= DATE_SUB(CURDATE(), INTERVAL 3 YEAR)
    GROUP BY 
        YEAR(dateOfPurchase), MONTH(dateOfPurchase)
    ORDER BY 
        year DESC, month DESC;
END //

DELIMITER ;
CALL GetSalesTrendsByMakeModel('Volkswagen','Passat');

#2. Top five vehicles based on number sold in the past year
SELECT 
    i.make, 
    i.model, 
    COUNT(*) AS total_sales
FROM 
    records r
JOIN 
    inventory i ON r.stockNumber = i.stockNumber
WHERE 
    dateOfPurchase >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
GROUP BY 
    i.make, i.model
ORDER BY 
    total_sales DESC
LIMIT 5;

#3. Sales trends of used vehicles by month
SELECT 
    MONTH(dateOfPurchase) AS month, 
    COUNT(*) AS total_sales
FROM 
    records r
JOIN 
    inventory i ON r.stockNumber = i.stockNumber
WHERE 
    i.carCondition = 'Used'
GROUP BY 
    MONTH(dateOfPurchase)
ORDER BY 
    month;

#4. List of customers who puchased a given make and model
# replace 'GivenMake' and 'GivenModel' with make and model searched for
DELIMITER //

CREATE PROCEDURE GetCustomersByMakeModel(IN givenMake VARCHAR(255), IN givenModel VARCHAR(255))
BEGIN
    SELECT 
        c.personID, 
        p.name, 
        p.email, 
        p.phoneNum, 
        p.address, 
        p.city, 
        p.state, 
        p.zipcode
    FROM 
        customer c
    JOIN 
        person p ON c.personID = p.personID
    JOIN 
        records r ON c.personID = r.customerID
    JOIN 
        inventory i ON r.stockNumber = i.stockNumber
    WHERE 
        i.make = givenMake AND 
        i.model = givenModel;
END //

DELIMITER ;
CALL GetCustomersByMakeModel('Volkswagen','Passat');
    
#5. Dealership locations based on their total sales amount in the past year
SELECT 
    l.locationID, 
    l.address, 
    l.city, 
    l.state, 
    l.zipcode, 
    SUM(i.netSalePrice) AS total_sales_amount
FROM 
    records r
JOIN 
    inventory i ON r.stockNumber = i.stockNumber
JOIN 
    location l ON r.locationID = l.locationID
WHERE 
    r.dateOfPurchase >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
GROUP BY 
    l.locationID
ORDER BY 
    total_sales_amount DESC;

#6. All sales people including number of sales and commission recieved in the last year
SELECT 
    sp.personID, 
    p.name, 
    sp.username, 
    sp.commissionRate, 
    COUNT(r.stockNumber) AS total_sales, 
    ROUND(SUM(i.netSalePrice * (sp.commissionRate / 100)), 2) AS total_commission
FROM 
    sales_person sp
JOIN 
    person p ON sp.personID = p.personID
LEFT JOIN 
    records r ON sp.personID = r.salesPersonID 
LEFT JOIN 
    inventory i ON r.stockNumber = i.stockNumber
WHERE 
    r.dateOfPurchase >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
GROUP BY 
    sp.personID
ORDER BY 
    total_sales DESC;

