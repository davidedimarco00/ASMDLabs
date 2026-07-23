@llm
Feature: LLM-based parking decision

  The parking decision service uses a local language model
  to evaluate entry and exit situations.

  Background:
    Given the vehicle plate is "AB123CD"
    And the vehicle is present
    And the camera is available
    And the induction coil is triggered
    And the camera detects plate "AB123CD"
    And the NFC device reports plate "AB123CD"


  Scenario: A parked vehicle exits with a paid ticket
    Given the requested parking operation is "EXIT"
    And the vehicle is already registered inside the parking area
    And the ticket status is "PAID"
    When the parking decision is requested from the language model
    Then the classification should be "NORMAL_EXIT"
    And the suggested action should be "ALLOW_EXIT"
    And the decision reason should not be empty
    And the confidence should be between 0.0 and 1.0


  Scenario: A parked vehicle attempts to exit with an unpaid ticket
    Given the requested parking operation is "EXIT"
    And the vehicle is already registered inside the parking area
    And the ticket status is "UNPAID"
    When the parking decision is requested from the language model
    Then the classification should be "PAYMENT_REQUIRED"
    And the suggested action should be "REQUEST_PAYMENT"
    And the decision reason should not be empty
    And the confidence should be between 0.0 and 1.0


  Scenario: A vehicle attempts to exit but is not registered inside
    Given the requested parking operation is "EXIT"
    And the vehicle is not registered inside the parking area
    And the ticket status is "PAID"
    When the parking decision is requested from the language model
    Then the classification should be "VEHICLE_NOT_FOUND"
    And the suggested action should be "DENY_EXIT"
    And the decision reason should not be empty
    And the confidence should be between 0.0 and 1.0


  Scenario: Camera and NFC identify different vehicles
    Given the requested parking operation is "EXIT"
    And the vehicle is already registered inside the parking area
    And the ticket status is "PAID"
    And the camera detects plate "AB123CD"
    And the NFC device reports plate "XY987ZT"
    When the parking decision is requested from the language model
    Then the classification should be "PLATE_NFC_MISMATCH"
    And the suggested action should be "REQUEST_MANUAL_REVIEW"
    And the decision reason should not be empty
    And the confidence should be between 0.0 and 1.0


  Scenario: Camera is unavailable while a vehicle is detected
    Given the requested parking operation is "EXIT"
    And the vehicle is already registered inside the parking area
    And the ticket status is "PAID"
    And the camera is unavailable
    When the parking decision is requested from the language model
    Then the classification should be "DEVICE_FAILURE"
    And the suggested action should be "REQUEST_MANUAL_REVIEW"
    And the decision reason should not be empty
    And the confidence should be between 0.0 and 1.0


  Scenario: A new vehicle enters the parking area
    Given the requested parking operation is "ENTRY"
    And the vehicle is not registered inside the parking area
    And the ticket status is "UNKNOWN"
    When the parking decision is requested from the language model
    Then the classification should be "NORMAL_ENTRY"
    And the suggested action should be "ALLOW_ENTRY"
    And the decision reason should not be empty
    And the confidence should be between 0.0 and 1.0


  Scenario: A vehicle already inside attempts another entry
    Given the requested parking operation is "ENTRY"
    And the vehicle is already registered inside the parking area
    And the ticket status is "UNKNOWN"
    When the parking decision is requested from the language model
    Then the classification should be "VEHICLE_ALREADY_PRESENT"
    And the suggested action should be "DENY_ENTRY"
    And the decision reason should not be empty
    And the confidence should be between 0.0 and 1.0