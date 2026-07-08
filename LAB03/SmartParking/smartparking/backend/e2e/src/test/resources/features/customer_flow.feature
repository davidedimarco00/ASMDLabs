Feature: Standard customer parking flow

  Scenario: Customer parks, pays and exits without administrator intervention
    Given The car with license plate "TEST" arrives at the parking lot
    When The system reads the license plate and calls the addCar endpoint of Parking Management
    And The system creates and saves the virtual ticket associated with the license plate in the database
    And The system calls the openEntryBarrier endpoint of Embedded to open the entry gate
    Then The car enters the parking lot

    Given The car is inside the parking lot
    When The virtual ticket is active and saved in the database
    Then The customer parks without issues

    Given The customer returns to the cashier and enters the license plate "TEST"
    When The system calls the calculateFee endpoint of Payment Service with the license plate
    And Payment Service calls Ticketing Service to get the associated virtual ticket
    And Payment Service calls getTariff to get the hourly rate
    Then The system calculates the total amount to pay and shows it to the customer

    Given The customer pays the calculated amount
    When The system calls the processPayment with nfcTag endpoint of Payment Service
    Then The payment is confirmed and the ticket is updated

    Given The customer heads to the exit
    When Embedded Service reads the license plate "TEST" at the exit
    And Embedded Service calls the removeCar endpoint of Parking Management
    And Embedded Service calls openExitBarrier to open the exit gate
    Then The customer leaves the parking lot without administrator intervention

