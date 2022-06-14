package com.sms.test.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sms.test.models.SmsRequest;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/sms/send")
public class SmsController {
    protected static Logger logger = LoggerFactory.getLogger(SmsController.class);



    @PostMapping(value = "",  produces = "application/json", consumes = "application/json")
    public Object sendSms(HttpServletRequest httpServletRequest) throws  Exception{
        String requestId = httpServletRequest.getSession().getId();


        String json = "{ \"to\":\"+233209390930\", \"content\": \"Hi There\", \"from\": \"LocQar\"}";
        Map<String,Object> obj = new ObjectMapper().readValue(json, HashMap.class);


        try{
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            String auth = "wavrymhs" + ":" + "ztjsbhvp";
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.US_ASCII));

            String authHeader = "Basic " + new String( encodedAuth );

            headers.add( "Authorization", authHeader );

            headers.setContentType(MediaType.APPLICATION_JSON);

  
            HttpEntity<Map> entity = new HttpEntity<>(obj, headers);
            logger.info(String.format("Headers: [%s]", headers.toString()));
            logger.info(String.format("[%s] About to make submit request to hubtel. URL: %s, payload: %s", requestId, "https://smsc.hubtel.com/v1/messages/send",
                    obj.toString()));
            ResponseEntity<String> response = restTemplate.postForEntity("https://smsc.hubtel.com/v1/messages/send", entity, String.class);

            HttpHeaders responseHeaders = response.getHeaders();
            if (responseHeaders != null) {
                logger.error(String.format("response headers: [%s]", responseHeaders.toString()));
            }
            if(response.getStatusCode() ==  HttpStatus.CREATED && response.getBody() != null){
                logger.info(String.format("[%s] Request to submit request to sms api successful. Response is: %s",
                        requestId, response.getBody()));
                Gson gson = new Gson();
                return gson.fromJson(response.getBody(), Object.class);
            }
            else
                logger.info(String.format("[%s] Request to  submit sms request api failed.",
                        requestId));
            return null;
        }catch(HttpStatusCodeException e){
            logger.error(String.format("[%s] Encountered error while trying to to send sms", requestId));
            // get response body
            System.out.println(e.getResponseBodyAsString());
            logger.error(String.format("Error response body: [%s]", e.getResponseBodyAsString()));


            // get http headers
            HttpHeaders headers = e.getResponseHeaders();
            if (headers != null) {
                System.out.println(headers.get("Content-Type"));
                System.out.println(headers.get("Server"));
                logger.error(String.format("Error response headers: [%s]", headers.toString()));
            }
            e.printStackTrace();
            return null;
        }
    }

}
