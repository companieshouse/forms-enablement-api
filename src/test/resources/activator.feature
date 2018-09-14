Feature: activator request

  Scenario: As a member of companies house staff I want to request forms from the queue
    Given The queue contains 3 packages with one pending
    When  I request 2 pending packages
    Then  One packages status should be changed

    Given The queue contains a failed package
    When  I request the packages failed contents
    Then  The failed packages status should be changed