Feature: Induced Lag
  There are many reasons we might want to induce lag into our code, such as for testing, benchmarking,
  experimenting, etc.

# Note: Cucumber tests are reported in scenario order as defined here
# TODO: Does this mean they are run in the same order, or are they run in random order, perhaps in parallel?

  Scenario: Minimal Duration
    Given a minimal Lag
    When I compute the minimal duration
    Then it should equal zero

  Scenario: Definite Duration
    Given a definite Lag
    When I compute the definite duration
    Then it should equal the minimum duration

  Scenario: Random Duration
    Given a random Lag
    When I compute the random duration
    Then it should not equal either the minimum or the maximum
    And it should not be outside the range

  Scenario: Sleep With Interrupt Handler
    Given a task with random Lag
    When I start it
    Then it should start normally
    And it should complete normally without interrupt

#  Scenario: Interrupted Sleep With Interrupt Handler
#    Given another task with random Lag
#    When I start it too
#    Then it should start normally as well
#    And it should complete prematurely with interrupt