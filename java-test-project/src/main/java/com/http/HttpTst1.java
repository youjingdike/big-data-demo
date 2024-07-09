package com.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpTst1 {
//    {"mobile":"18406555989","type":"0","code":"0086"}
//    https://passport.csdn.net/v1/register/pc/sendVerifyCode
//    application/json;charset=UTF-8
    public static void main(String[] args) throws Exception {
        String[] number = {
                "{\"accountSystem\":\"customer\",\"smsType\":\"sms\",\"sceneKey\":\"WHEN_REGISTER\",\"credential\":{\"username\":\"18406555989\",\"captchaToken\":\"DY7Fj9rcj8RHTP9i2cjgq9uKHqguD8r9\",\"captchaScene\":\"login_slider\",\"ticketId\":\"GTPDi5gfYYgx8vezlQWdups8lQpzv4rN\"},\"context\":{},\"version\":\"2.0\",\"service\":\"https://ajax.api.lianjia.com/login/login/getuserinfo\"}"
                ,
                "{\"accountSystem\":\"customer\",\"smsType\":\"sms\",\"sceneKey\":\"WHEN_REGISTER\",\"credential\":{\"username\":\"19834317742\",\"captchaToken\":\"8Fn979pGcH82uu8nuDDUc8mnr4PiP47D\",\"captchaScene\":\"login_slider\",\"ticketId\":\"0RVvUqsjMJMWjsdp3SslDLAJfrn9GWWI\"},\"context\":{},\"version\":\"2.0\",\"service\":\"https://ajax.api.lianjia.com/login/login/getuserinfo\"}"
            };
        int i = 0;
        int num = 50;
        while (true) {
            if (i == 0) {
                Thread.sleep(1000*65);
            }
//            int i = new Random().nextInt(number.length);
            System.out.println(i);
            sendHttp(number[0]);
//            Thread.sleep(1000*20);
            sendHttp(number[1]);
            i++;
            if (i == num) {

                break;
            }
            Thread.sleep(1000*65);
        }
    }

    private static void sendHttp(String urlParameters) throws IOException {
        String url = "https://clogin.lianjia.com/authentication/mfa/sms";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        con.setDoOutput(true);
        con.setFixedLengthStreamingMode(postDataLength);

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
        }

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response);
        } else {
            System.out.println("POST request not worked");
        }
    }

    private static void sendHttp1(String number) throws IOException {

        String url = "https://www.chuanglan.com/zzt_api/cloudApi/api/v1/sms/register/sendSmsCode";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        String urlParameters = "{\"phone\":\""+number+"\",\"ticket\":\"tr03SH-wmsiww3Bucq0MbsegCKamQwha8m5sj9EByr2qP0oat-9MORIO4exg4fW32vXxqJjwaL96kII-_XszmPS_X1TdG1DN5oKaZLWOD9NjoxQwAnrzHY2fNs-NzZKg-nxbivYS4FqQKJE*\",\"randstr\":\"@DhT\",\"country_prex\":\"86\"}";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        con.setDoOutput(true);
        con.setFixedLengthStreamingMode(postDataLength);

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
        }

        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response);
        } else {
            System.out.println("POST request not worked");
        }
    }
}
