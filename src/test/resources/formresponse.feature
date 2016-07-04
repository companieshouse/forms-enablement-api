Feature: form response from chips

  Scenario: As an API consumer I want to post a forms verdict to the Forms Service API so it can be processed on Salesforce
    Given I submit a valid verdict to the response forms API using the correct credentials
    Then I should receive a response from Salesforce

    Given I submit an invalid media type to the response forms API using the correct credentials
    Then I should receive an invalid media type error from the response api
