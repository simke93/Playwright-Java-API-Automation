package com.qa.api.booking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class TokenTest {

    public static String emailID;

    @BeforeTest
    public void setup(){
        playwright = Playwright.create();
        request = playwright.request();
        requestContext = request.newContext();
    }

    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    @Test
    public void getTokenTest() throws IOException {
        //String json:
        String reqTokenJsonBody = "{\n" +
                "    \"username\" : \"admin\",\n" +
                "    \"password\": \"password123\"\n" +
                "}";

        /// POST call: create a token
        APIResponse apiPostTokenResponse =  requestContext.post("https://restful-booker.herokuapp" +
                        ".com/auth",
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        .setData(reqTokenJsonBody));

        System.out.println(apiPostTokenResponse.status());
        Assert.assertEquals(apiPostTokenResponse.status(), 200);
        Assert.assertEquals(apiPostTokenResponse.statusText(), "OK");

        System.out.println(apiPostTokenResponse.text());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode postApiJsonResponse = objectMapper.readTree(apiPostTokenResponse.body());
        System.out.println(postApiJsonResponse.toPrettyString());

        //capture token from the post json response:
        String tokenId = postApiJsonResponse.get("token").asText();
        System.out.println("token id : " + tokenId);

        Assert.assertNotNull(tokenId);

    }


    @AfterTest
    public void tearDown(){
        playwright.close();
    }


}
