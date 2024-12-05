Select inventory.stockNumber, make, model, year, color, carCondition, netSalePrice, inventory.locationID
From records join inventory on records.stockNumber = inventory.stockNumber
Order by make asc