package com.qa.api.tests.POST;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CreateUserWithJsonFileTest {
    public static String emailID;

    @BeforeTest
    public void setup(){
        playwright = Playwright.create();
        request = playwright.request();
        requestContext = request.newContext();
    }


    //public static String getRandomEmail(){
       // emailID = "testpalwright"+ System.currentTimeMillis() + "@gmail.com";
       // return emailID;
    //}

    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;


    @Test
    public void createUserTest() throws IOException {

        // get json file:
        byte[] fileBytes = null;
        File file = new File("./src/test/data/user.json");
        fileBytes = Files.readAllBytes(file.toPath());



        /// POST call: create user
        APIResponse apiPostResponse =  requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        .setHeader("Authorization", "Bearer e8ce3d9e6b1df2aad6b810c2432f8ff19f8f4f30df35850993a17829d726ed9a")
                        .setData(fileBytes));

        System.out.println(apiPostResponse.status());
        Assert.assertEquals(apiPostResponse.status(), 201);
        Assert.assertEquals(apiPostResponse.statusText(), "Created");

        System.out.println(apiPostResponse.text());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode postApiJsonResponse = objectMapper.readTree(apiPostResponse.body());
        System.out.println(postApiJsonResponse.toPrettyString());

        //capture id from the post json response:
        String userID = postApiJsonResponse.get("id").asText();
        System.out.println("user id : " + userID);

        // GET Call: Fetch the same user by id:
        System.out.println("=================get call response =================");

        APIResponse apiGetResponse =
                requestContext.get("https://gorest.co.in/public/v2/users/" + userID,
                        RequestOptions.create()
                                .setHeader("Authorization", "Bearer e8ce3d9e6b1df2aad6b810c2432f8ff19f8f4f30df35850993a17829d726ed9a"));
        Assert.assertEquals(apiGetResponse.status(), 200);

        System.out.println(apiGetResponse.text());
        Assert.assertTrue(apiGetResponse.text().contains(userID));
        Assert.assertTrue(apiGetResponse.text().contains("Naveen"));
        //Assert.assertTrue(apiGetResponse.text().contains("testapi2@gmail.com"));

    }

    @AfterTest
    public void tearDown(){
        playwright.close();
    }


}
