DELIMITER $$

CREATE TRIGGER after_sales_person_delete
    AFTER DELETE ON sales_person
    FOR EACH ROW
BEGIN
    -- Check if the deleted personID exists in the manager table and delete it
    DELETE FROM manager
    WHERE personID = OLD.personID;
    END$$

    DELIMITER ;
