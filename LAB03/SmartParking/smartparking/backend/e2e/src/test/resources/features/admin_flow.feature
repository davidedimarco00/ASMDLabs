Feature: Admin parking management flow

  Scenario: Admin login and token retrieval
    Given The admin sends a POST request to auth-service with credentials
    When The system register the authentication
    Then The admin can access protected endpoints

  Scenario: Admin sets the daily tariff
    Given The admin is authenticated
    When The admin calls POST payment-service with hourFee, dailyFee, and thresholdHours
    Then The system updates the tariff configuration

  Scenario: Admin generates the previous day's report
    Given The admin is authenticated
    When The admin calls GET analytics-service to generate reports for the previous day
    Then The system generates or retrieves the report

  Scenario: Admin manually adds a car for entry
    Given The admin is authenticated
    When The admin calls POST parking-service to addCar with the customer's license plate
    Then The system adds the car to the parking lot

  Scenario: Admin assists customer exit after payment verification
    Given The admin is authenticated
    When The admin calls GET getAllTickets to view active tickets
    And The admin calls GET getAllHistoryTickets to check payment status
    And If payment is confirmed, the admin calls POST openExitBarrier
    Then The system opens the exit barrier for the customer
