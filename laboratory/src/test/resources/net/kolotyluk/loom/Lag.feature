Feature: Induced Lag
  There are many reasons we might want to induce lag into our code, such as for testing, benchmarking,
  experimenting, etc.

  Scenario: Definite Duration
    Given a definite Lag
    When I compute the definite duration
    Then it should equal the minimum duration

  Scenario: Random Duration
    Given a random Lag
    When I compute the random duration
    Then it should not equal either duration
    And it should not be outside the range
