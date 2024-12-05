DELIMITER //

CREATE TRIGGER UpdateCarStatusAfterSale
AFTER INSERT ON records
FOR EACH ROW
BEGIN
    UPDATE 
        inventory
    SET 
        status = 'Sold'
    WHERE 
        stockNumber = NEW.stockNumber;
END //

DELIMITER ;
