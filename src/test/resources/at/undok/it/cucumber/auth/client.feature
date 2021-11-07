Feature: Test all client operations

  Scenario: Happy Case, create new client
    Given loggedIn User
    When trying to create a client
    Then expect this client to be created