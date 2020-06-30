Feature: Manage triangles
  As a user
  In order to manage triangles
  I need to send HTTP requests to create, read and delete triangles

  @smoke
  Scenario: Create a new triangle
    When I create a triangle with sides: 3.0, 4.0, 5.0
    Then I should see a 200 status code in response
    And I should see first side 3.0, second side 4.0 and third side 5.0

  @smoke
  Scenario: Check perimeter calculation
    When I create a triangle with sides: 15.5, 10.2, 6.3
    Then I should see a 200 status code in response
    And I should see 32.0 "perimeter"

  @smoke
  Scenario: Check area calculation
    When I create a triangle with sides: 5.0, 6.0, 7.0
    Then I should see a 200 status code in response
    And I should see 14.6969 "area"

  @smoke
  Scenario: Triangle CRUD
    Given I delete all triangles
    And I create a triangle with sides: 3.0, 4.0, 5.0
    And I should see 1 created triangles
    When I delete this triangle
    Then I should see a 200 status code in response
    And I should see 0 created triangles

  #This one is a bug - limit of 10 triangles is exceeded
  @regression
  Scenario: Create more then 10 triangles
    Given I delete all triangles
    And I create a triangle with sides: 1.0, 1.0, 1.0
    And I create a triangle with sides: 2.0, 3.0, 4.0
    And I create a triangle with sides: 3.0, 4.0, 5.0
    And I create a triangle with sides: 4.0, 4.0, 5.0
    And I create a triangle with sides: 5.0, 4.0, 5.0
    And I create a triangle with sides: 6.0, 4.0, 5.0
    And I create a triangle with sides: 7.0, 4.0, 5.0
    And I create a triangle with sides: 8.0, 4.0, 5.0
    And I create a triangle with sides: 9.0, 5.0, 5.0
    And I create a triangle with sides: 10.0, 10.0, 10.0
    And I should see 10 created triangles
    When I create a triangle with sides: 3.0, 4.0, 5.0
    Then I should see 10 created triangles
    And I should see a 422 status code in response

  @regression
  Scenario: Use specific separator
    When I create a triangle with data:
    """
    {"separator": "custom_separator", "input": "3.0   custom_separator   4.0    custom_separator 5.0"}
    """
    Then I should see a 200 status code in response
    And I should see first side 3.0, second side 4.0 and third side 5.0

  @regression
  Scenario: Create triangle without separator
    When I create a triangle with data:
    """
    {"input": "3;4;5"}
    """
    Then I should see a 200 status code in response
    And I should see first side 3.0, second side 4.0 and third side 5.0

  @regression @negative
  Scenario: Use specific separator without specifying it
    When I create a triangle with data:
    """
    {"input": "3.0   custom_separator   4.0    custom_separator 5.0"}
    """
    Then I should see a 422 status code in response
    And I should see "Unprocessable Entity" in response

  @regression @negative
  Scenario: Try to create a triangle with one side longer then two other sides combined
    When I create a triangle with sides: 3, 4, 10
    Then I should see a 422 status code in response
    And I should see "Unprocessable Entity" in response

  #This one looks like a bug - it is not a triangle
  @regression @negative
  Scenario: Try to create a triangle with zero length sides
    When I create a triangle with sides: 0.0, 0.0, 0.0
    Then I should see a 422 status code in response
    And I should see "Unprocessable Entity" in response

  #This one looks like a bug - negative values are converted to positive
  @regression @negative
  Scenario: Try to create a triangle with negative length of a side
    When I create a triangle with sides: -3.0, 4.0, 5.0
    Then I should see a 422 status code in response
    And I should see "Unprocessable Entity" in response

  @regression @negative
  Scenario: Send bad request body
    When I create a triangle with data:
    """
    {"Lorem ipsum"}
    """
    Then I should see a 400 status code in response
    And I should see "Bad Request" in response

  @regression @negative
  Scenario: Use invalid API key
    When I set "123" API token
    When I create a triangle with sides: 3.0, 4.0, 5.0
    Then I should see a 401 status code in response
    And I should see "Unauthorized" in response

  @regression @negative
  Scenario: Use invalid end-point
    When I set "square" end-point
    When I create a triangle with sides: 3.0, 4.0, 5.0
    Then I should see a 404 status code in response
    And I should see "Not Found" in response

  @regression @negative
  Scenario: Try to create a triangle with not a numeric value as "input"
    When I create a triangle with data:
    """
    {"separator": ";", "input": "hello"}
    """
    Then I should see a 422 status code in response
    And I should see "Unprocessable Entity" in response

  #This one looks like a bug - unhandled NPE
  @regression @negative
  Scenario: Try to create a triangle without mandatory "input" key
    When I create a triangle with data:
    """
    {"separator": ";", "output": "3;4;5"}
    """
    Then I should see a 500 status code in response
    And I should see "Internal Server Error" in response