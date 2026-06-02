package com.hp.utilities;

import io.restassured.response.Response;
import io.restassured.RestAssured;

public class ApiUtil {

    //Method to send GET request to the API endpoint and return the response
    public static Response sendGetRequest(String endpoint) {
        return RestAssured.get(endpoint);
    }

    //Method to send POST request to the API endpoint with a JSON body and return the response
    public static Response sendPostRequest(String endpoint, String jsonBody) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .post(endpoint);
    }

    //Method to verify the status code of the API response
    public static boolean validateStatusCode(Response response, int expectedStatusCode) {
        return response.getStatusCode() == expectedStatusCode;
    }

    //Method to extract a specific field value from the API response
    public static String extractJsonFieldValue(Response response, String fieldName) {
        return response.jsonPath().getString(fieldName);
    }
    
}
