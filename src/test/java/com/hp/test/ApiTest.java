package com.hp.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.hp.utilities.ApiUtil;
import com.hp.utilities.ExtentManager;

import io.restassured.response.Response;


public class  ApiTest {
	
	@Test
	public void verifyGetUserAPI() {

		//1.Define endpoint URL
		String endpoint = "https://jsonplaceholder.typicode.com/users/2";

		//2.Send GET request and store the response
		ExtentManager.logStep("Sending GET request to endpoint: " + endpoint);
		Response response = ApiUtil.sendGetRequest(endpoint);

		//3.Validate the status code
		ExtentManager.logStep("Validating the status code of the response");
		boolean isStatusCodeValid = ApiUtil.validateStatusCode(response, 200);
		Assert.assertTrue(isStatusCodeValid, "Status code is not matching!!!");
		ExtentManager.logStep(Status.PASS,"Status code: " + response.getStatusCode() + " is matching"); 

		//4.Validate specific field value in the response
		//username field value should be "Antonette"
		ExtentManager.logStep("Validating the 'username' field value in the response");
		String ExpectedUsername = "Antonette";
		String username = ApiUtil.extractJsonFieldValue(response, "username");
		Assert.assertEquals(username, ExpectedUsername, "Username is not matching!!!");
		ExtentManager.logStep(Status.PASS,"Username: " + ExpectedUsername + " is matching");

		//email field value should be "Shanna@melissa.tv"
		ExtentManager.logStep("Validating the 'email' field value in the response");
		String ExpectedEmail = "Shanna@melissa.tv";
		String email = ApiUtil.extractJsonFieldValue(response, "email");
		Assert.assertEquals(email, ExpectedEmail, "Email is not matching!!!");
		ExtentManager.logStep(Status.PASS,"Email: " + email + " is matching");

	}
	
}
