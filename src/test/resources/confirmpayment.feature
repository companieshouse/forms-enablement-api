Feature: confirm payment

  Scenario: As an API consumer I want to confirm payment to the Forms Service API so that submissions can be sent to CHIPS
    Given I submit a nonexisting package id confirm payment request to the forms API using the correct credentials
    Then I should receive a 404 response
    
    Given The queue contains a package with a status of unpaid
    When I submit a valid confirm payment request to the forms API using the correct credentials
    Then I should receive a 200 response
    
    When I submit a confirm payment request with invalid presenter credentials
    Then An exception should be thrown
    
    Given I submit an invalid media type to the forms confirm payment API using the correct credentials
    Then I should receive an invalid media type error from the confirm payment api
