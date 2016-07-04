Feature: form barcode from chips

  Scenario: As an API consumer I want to post a date to the Forms Service API so I can get a unique barcode
    Given I submit a valid date to the forms API using the correct credentials
    Then I should receive a response from CHIPS barcode service

    Given I submit an invalid media type to the barcode forms API using the correct credentials
    Then I should receive an invalid media type error from the barcode api
