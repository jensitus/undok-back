Feature: Testing registration

  Tests if the full registration workflow works as expected (this includes the registration call itself, the sending
  of the e-mail and the confirmation)

  Scenario: Happy Case, registration and confirmation of a new user (passing all verification) is successful
    Given new valid random user
    When trying to register this user
    Then expect a "201 CREATED" registration response with message "user created"
    And an confirmation email to be sent


  Scenario: Registration is done with an already taken username
    Given existing user with username "cornholio"
    When another user tries to register with username "cornholio"
    Then expect a "409 CONFLICT" registration response with message "Damn -> this Username is already taken"