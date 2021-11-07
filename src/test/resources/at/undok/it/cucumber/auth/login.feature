Feature: Testing if login works

  Scenario: Happy case, login for a confirmed user works
    Given existing confirmed user
    When trying to login
    Then expect api token to be returned
