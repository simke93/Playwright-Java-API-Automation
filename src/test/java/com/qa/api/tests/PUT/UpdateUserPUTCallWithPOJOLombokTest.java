package com.qa.api.tests.PUT;

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

public class UpdateUserPUTCallWithPOJOLombokTest {

    //1. post - user id = 123
    //2. put - user id - /123
    //3. get -- user id /123

    public static String emailID;

    @BeforeTest
    public void setup(){
        playwright = Playwright.create();
        request = playwright.request();
        requestContext = request.newContext();
    }
    @AfterTest
    public void tearDown(){
        playwright.close();
    }

    public static String getRandomEmail(){
        emailID = "testpalwright"+ System.currentTimeMillis() + "@gmail.com";
        return emailID;
    }

    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;


    @Test
    public void createUserTest() throws IOException {

        //Create users object: using builder pattern:
        Users users = Users.builder()
                .name("naveen Automation")
                .email(getRandomEmail())
                .gender("male")
                .status("active").build();

        ///1. POST call: create user
        APIResponse apiPostResponse =  requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        .setHeader("Authorization", "Bearer e8ce3d9e6b1df2aad6b810c2432f8ff19f8f4f30df35850993a17829d726ed9a")
                        .setData(users));

        System.out.println(apiPostResponse.status());
        Assert.assertEquals(apiPostResponse.status(), 201);
        Assert.assertEquals(apiPostResponse.statusText(), "Created");

        String responseText = apiPostResponse.text();
        System.out.println(responseText);

        //convert response text/json to POJO -- deserialization
        ObjectMapper objectMapper = new ObjectMapper();
        User actUser = objectMapper.readValue(responseText, User.class);
        System.out.println("actual user from the response ----->");
        System.out.println(actUser);

        Assert.assertEquals(actUser.getName(), users.getName());
        Assert.assertEquals(actUser.getEmail(), users.getEmail());
        Assert.assertEquals(actUser.getStatus(), users.getStatus());
        Assert.assertEquals(actUser.getGender(), users.getGender());
        Assert.assertNotNull(actUser.getId());

        String userID = actUser.getId();
        System.out.println("new user id is : " + userID);

        //update status active to inactive
        users.setStatus("inactive");
        users.setName("Automation Playwright");

        System.out.println("-----------------------------------------------------");


        // 2. PUT CALL - update user:
        APIResponse apiPutResponse =
                requestContext.put("https://gorest.co.in/public/v2/users/" + userID,
                RequestOptions.create()
                        .setHeader("Content-Type","application/json")
                        .setHeader("Authorization", "Bearer e8ce3d9e6b1df2aad6b810c2432f8ff19f8f4f30df35850993a17829d726ed9a")
                        .setData(users));

        System.out.println(apiPutResponse.status() + " : " + apiPutResponse.statusText());
        Assert.assertEquals(apiPutResponse.status(), 200);

        String putResponseText = apiPutResponse.text();
        System.out.println("update user : " + putResponseText);


        Users actPutUser = objectMapper.readValue(putResponseText, Users.class);
        Assert.assertEquals(actPutUser.getId(), userID);
        Assert.assertEquals(actPutUser.getName(), users.getName());
        Assert.assertEquals(actPutUser.getStatus(), users.getStatus());


        //3. Get the updates user with GET CALL:
        APIResponse apiGETResponse =
                requestContext.get("https://gorest.co.in/public/v2/users/" +userID,
                        RequestOptions.create()
                                .setHeader("Authorization", "Bearer e8ce3d9e6b1df2aad6b810c2432f8ff19f8f4f30df35850993a17829d726ed9a"));

        int statusCode = apiGETResponse.status();
        System.out.println("resposne status code:" + statusCode);
        Assert.assertEquals(statusCode, 200);
        Assert.assertEquals(apiGETResponse.ok(),true);

        String statusGetStatusText = apiGETResponse.statusText();
        System.out.println(statusGetStatusText);

        String getResponseText = apiGETResponse.text();


        Users actGetUser = objectMapper.readValue(getResponseText, Users.class);
        Assert.assertEquals(actGetUser.getId(), userID);
        Assert.assertEquals(actGetUser.getName(), users.getName());
        Assert.assertEquals(actGetUser.getStatus(), users.getStatus());


    }




}
