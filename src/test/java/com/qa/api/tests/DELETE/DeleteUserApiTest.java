package com.qa.api.tests.DELETE;

import com.api.data.User;
import com.api.data.Users;
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

public class DeleteUserApiTest {

    //1. create user -- user id -- 201
    //2. delete user -- user id -- 204
    //3. get user -- user id -- 404



    public static String emailID;

    @BeforeTest
    public void setup(){
        playwright = Playwright.create();
        request = playwright.request();
        requestContext = request.newContext();
    }

    public static String getRandomEmail(){
        emailID = "testpalwright"+ System.currentTimeMillis() + "@gmail.com";
        return emailID;
    }

    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;



    @Test
    public void deleteUserTest() throws IOException {
        //1. Create users object: using builder pattern:

        Users users = Users.builder()
                .name("naveen Automation")
                .email(getRandomEmail())
                .gender("male")
                .status("active").build();


        /// POST call: create user
        APIResponse apiPostResponse =  requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        .setHeader("Authorization", "Bearer e8ce3d9e6b1df2aad6b810c2432f8ff19f8f4f30df35850993a17829d726ed9a")
                        .setData(users));

        System.out.println(apiPostResponse.status());
        Assert.assertEquals(apiPostResponse.status(), 201);

        String responseText = apiPostResponse.text();
        System.out.println(responseText);

        //convert response text/json to POJO -- deserialization
        ObjectMapper objectMapper = new ObjectMapper();
        User actUser = objectMapper.readValue(responseText, User.class);
        System.out.println("actual user from the response ----->");
        System.out.println(actUser);

        Assert.assertNotNull(actUser.getId());

        String userID = actUser.getId();
        System.out.println("new user id is : " + userID);

        //2. Delete user

        APIResponse apiDeleteResponse = requestContext.delete("https://gorest.co" +
                        ".in/public/v2/users/" + userID,
                RequestOptions.create()
                .setHeader("Authorization", "Bearer " +
                        "e8ce3d9e6b1df2aad6b810c2432f8ff19f8f4f30df35850993a17829d726ed9a"));

        System.out.println(apiDeleteResponse.status());
        System.out.println(apiDeleteResponse.statusText());

        Assert.assertEquals(apiDeleteResponse.status(), 204);

        System.out.println("delete user response body ===== " + apiDeleteResponse.text());

        // 3. get user -- user id --- 404

        APIResponse apiResponse =
                requestContext.get("https://gorest.co.in/public/v2/users/" + userID, RequestOptions.create()
                        .setHeader("Authorization", "Bearer " +
                                "e8ce3d9e6b1df2aad6b810c2432f8ff19f8f4f30df35850993a17829d726ed9a"));

        int statusCode = apiResponse.status();
        System.out.println("resposne status code:" + statusCode);
        Assert.assertEquals(statusCode, 404);
        Assert.assertEquals(apiResponse.statusText(),"Not Found");

        Assert.assertTrue(apiResponse.text().contains("Resource not found"));





    }

    @AfterTest
    public void tearDown(){
        playwright.close();
    }



}
