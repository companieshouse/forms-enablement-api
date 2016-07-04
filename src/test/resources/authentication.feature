Feature: authentication

  Scenario: As an API consumer I want to send a basic auth header to the api to be allowed access
    Given I submit no auth header
    Then I should receive an unauthorized response from the api

    Given I submit a valid auth header
    Then I should receive a success response from the api

    Given I submit an invalid auth header
    Then I should receive an unauthorized response from the api