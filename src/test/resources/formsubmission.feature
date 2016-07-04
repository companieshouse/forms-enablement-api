Feature: form submission from salesforce

  Scenario: Invalid submission
    Given I submit a invalid form to the forms API using the correct credentials
    Then the database should contain 0 packages and 0 forms
    Then I should receive an error response from the API

  Scenario: Valid submission
    Given I submit a valid form to the forms API using the correct credentials
    Then the database should contain 1 packages and 2 forms
    Then the package and forms should have the correct info
    Then I should get a success response from the API
