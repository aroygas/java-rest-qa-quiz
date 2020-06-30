# java-rest-api-test-sample

This is a sample Java based project for API tests of Triangle Service. 

It uses REST Assured, Cucumber, JUnit, Allure and Gradle.


---

# Set up #

1. Install Git
2. Install Java 11
3. Install Gradle
4. Install Allure (optionally) to get fancy reports

## Clone this repository

You can clone this repo using git console via https:
```
git clone https://github.com/aroygas/java-rest-qa-quiz.git
```

---

# Running tests #

To run all the tests just call gradle at project's root with "clean test" command:
```
gradle clean test
```

## Tags ##

Each scenario has some set of tags. <br>
Test suites are managed by mentioning tags. <br>
Tags follow the rules: <br>
`@smoke`                                 - run only tests tagged as @smoke <br>
`not @regression`                     - exclude tests tagged as @regression from run <br>
`@negative or @regression`    - run tests tagged as @negative OR tagged as @regression <br>
`@smoke and @negative` - run tests tagged as @smoke AND tagged as @negative <br>
`@smoke and not @clean`  - run tests tagged as @smoke AND exclude tests tagged as @clean from the run <br>

So, to run only smoke tests run:
```
gradle clean test -Dtags="@smoke"
```

---

# Generating html report #

To generate a report using Allure at project's root run:
```
allure serve ./build/allure-results
```

---

# Test scenarios that will fail because of bugs:
```
  1) Limit of 10 triangles can be exceeded
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

  2) It is allowed to create triangle with zero side lengths 
  Scenario: Try to create a triangle with zero length sides
    When I create a triangle with sides: 0.0, 0.0, 0.0
    Then I should see a 422 status code in response
    And I should see "Unprocessable Entity" in response

  3) Negative values can be converted to positive
  Scenario: Try to create a triangle with negative length of a side
    When I create a triangle with sides: -3.0, 4.0, 5.0
    Then I should see a 422 status code in response
    And I should see "Unprocessable Entity" in response
    
  4) NPE is not handled by back-end
  Scenario: Try to create a triangle without mandatory "input" key
    When I create a triangle with data:
    """
    {"separator": ";", "output": "3;4;5"}
    """
    Then I should see a 500 status code in response
    And I should see "Internal Server Error" in response    
```