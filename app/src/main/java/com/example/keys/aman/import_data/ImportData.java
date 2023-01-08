//package com.example.keys.aman.import_data;
//
//import com.example.keys.aman.home.addpassword.AddPasswordDataHelperClass;
//import com.fasterxml.jackson.core.JsonFactory;
//import com.google.api.services.passwords.v1.model.ListPasswordsResponse;
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//
//import java.util.List;
//
//public class ImportData {
//    public void getPassword(){
//        // Replace "ACCESS_TOKEN" with the user's OAuth 2.0 access token
//        String accessToken = "ACCESS_TOKEN";
//
//        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
//        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//
//        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
//        Passwords passwordsService = new Passwords.Builder(httpTransport, jsonFactory, credential)
//                .setApplicationName("MyApplication")
//                .build();
//
//        ListPasswordsResponse response = passwordsService.passwords().list().execute();
//        List<AddPasswordDataHelperClass> passwords = response.getPasswords();
//
//        for (AddPasswordDataHelperClass password : passwords) {
//            String username = password.getUsername();
//            String url = password.getUrl();
//            String passwordValue = password.getPassword();
//
//            // Store the password in your application's password database
//            // ...
//        }
//
//    }
//
//}
