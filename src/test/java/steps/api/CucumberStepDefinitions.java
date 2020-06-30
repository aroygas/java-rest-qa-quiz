package steps.api;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import utils.ConfigReader;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * All Cucumber steps are implemented in this class.
 */
public class CucumberStepDefinitions {
    private final String hostAPIBaseURL;
    private String personalAPIToken;
    private String endPoint;
    private Response response;

    /**
     * Constructor reads property file to set values for private fields
     * @throws Exception - in case property file is missing
     */
    public CucumberStepDefinitions() throws Exception {
        Properties properties = new ConfigReader().getPropValues();
        hostAPIBaseURL = properties.getProperty("hostAPIBaseURL");
        personalAPIToken = properties.getProperty("personalAPIToken");
        endPoint = "triangle";
        iDeleteAllTriangles();
    }

    /**
     * According to Cucumber philosophy checks should be done in separate steps that start with @Then annotation
     * Check that actual code matches
     * @param statusCode - desired status code
     */
    @Then("I should see a {int} status code in response")
    public void iShouldSeeAStatusCodeInResponse(int statusCode) {
        response.then().assertThat().statusCode(statusCode);
    }


    /**
     * Check that text of a body in response contains message
     * @param message - expected message
     */
    @And("I should see {string} in response")
    public void iShouldSeeInResponse(String message) {
        response.then().assertThat().body(containsString(message));
    }

    @When("I create a triangle with sides: {float}, {float}, {float}")
    public void iCreateATriangleWithSidesIntIntInt(float sideA, float sideB, float sideC) {
        response = createATriangleWithData("{\"separator\": \";\", \"input\": \"" + sideA + ";" + sideB + ";" + sideC + "\"}");
    }

    @When("I create a triangle with data:")
    public void iCreateATriangleWithData(String body) {
        response = createATriangleWithData(body);
    }

    /**
     * Actually sends a create new triangle POST request with
     * @param body - parameters of a new triangle
     * @return response object
     */
    private Response createATriangleWithData(String body) {
        return given().
                header("X-User", personalAPIToken).
                contentType(JSON).
                body(body).
                post(hostAPIBaseURL + endPoint);
    }

    /**
     * Get triangle perimeter or area by Id
     * @param triangleId - Id of a triangle
     * @return response object
     */
    private Response getTrianglePerimeter(String triangleId, String property) {
        return given().
                header("X-User", personalAPIToken).
                contentType(JSON).
                get(hostAPIBaseURL + "triangle/" + triangleId + "/" + property);
    }

    /**
     * Delete triangle by Id
     * @param triangleId - Id of a triangle
     * @return response object
     */
    private Response deleteTriangleById(String triangleId) {
        return given().
                header("X-User", personalAPIToken).
                contentType(JSON).
                delete(hostAPIBaseURL + "triangle/" + triangleId);
    }

    /**
     * Get all triangles list
     * @return response object
     */
    private Response getALlTriangles() {
        return given().
                header("X-User", personalAPIToken).
                contentType(JSON).
                get(hostAPIBaseURL + "triangle/all");
    }

    /**
     * Change token to given string
     * @param token - new token value
     */
    @And("I set {string} API token")
    public void iSetAPItoken(String token) {
        this.personalAPIToken = token;
    }

    /**
     * Change end-point to given string
     * @param endPoint - new end-point value
     */
    @And("I set {string} end-point")
    public void iSetEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    @Then("I should see first side {float}, second side {float} and third side {float}")
    public void iShouldSeeFirstSideIntSecondSideIntAndThirdSideInt(float sideA, float sideB, float sideC) {
        assertEquals(sideA, response.jsonPath().getFloat("firstSide"),  0.001);
        assertEquals(sideB, response.jsonPath().getFloat("secondSide"), 0.001);
        assertEquals(sideC, response.jsonPath().getFloat("thirdSide"), 0.001);
    }

    @Then("I should see {float} {string}")
    public void iShouldSeePerimeter(float value, String property) {
        String lastCreatedTriangleId = response.jsonPath().getString("id");

        response = getTrianglePerimeter(lastCreatedTriangleId, property);
        assertEquals(value, response.jsonPath().getFloat("result"), 0.001);
    }

    @Then("I should see {int} created triangles")
    public void iShouldSeeCreatedTriangles(int numberOfTriangles) {
        response = getALlTriangles();
        List<String> jsonResponse = response.jsonPath().getList("$");
        assertEquals(numberOfTriangles, jsonResponse.size());
    }

    @When("I delete this triangle")
    public void iDeleteThisTriangle() {
        String lastCreatedTriangleId = response.jsonPath().getString("id[0]");

        response = deleteTriangleById(lastCreatedTriangleId);
    }

    @When("I delete all triangles")
    public void iDeleteAllTriangles() {
        response = getALlTriangles();
        List<String> jsonResponse = response.jsonPath().getList("$");
        int sizeOfList = jsonResponse.size();

        if (sizeOfList>0){
            for (int i = 0; i < sizeOfList; i++) {
                String triangle_id = response.jsonPath().getString("id[" + i + "]");

                deleteTriangleById(triangle_id);
            }
        }
    }
}
