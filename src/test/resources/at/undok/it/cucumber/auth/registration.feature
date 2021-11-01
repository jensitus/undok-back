Feature: Testing registration

  Tests if the full registration workflow works as expected (this includes the registration call itself, the sending
  of the e-mail and the confirmation)

  Scenario: Happy Case, registration and confirmation of a new user (passing all verification and without password change) is successful
    Given new valid random user
    When trying to register this user
    Then expect a "201 CREATED" registration response with message "user created"
    And the user to have confirmation status "unconfirmed"
    And an confirmation email to be sent
    When clicking the confirmation link
    Then the user to have confirmation status "confirmed"


  Scenario: Registration is done with an already taken username
    Given existing user with username "cornholio"
    When another user tries to register with username "cornholio"
    Then expect a "409 CONFLICT" registration response with message "Damn -> this Username is already taken"