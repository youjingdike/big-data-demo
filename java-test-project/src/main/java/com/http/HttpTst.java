package com.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class HttpTst {
//    {"mobile":"18406555989","type":"0","code":"0086"}
//    https://passport.csdn.net/v1/register/pc/sendVerifyCode
//    application/json;charset=UTF-8
    public static void main(String[] args) throws Exception {
        String[] number = {"18406555989","19834317742"};
        int i = 0;
        int num = 50;
        while (true) {
//            if (i == 0) {
//                Thread.sleep(1000*65*20);
//            }
//            int i = new Random().nextInt(number.length);
            System.out.println(i);
            sendHttp(number[0]);
            Thread.sleep(1000*60*5);
            sendHttp(number[1]);
            i++;
            if (i == num) {
                break;
            }
            Thread.sleep(1000*65*5);
        }
    }

    private static void sendHttp(String number) throws IOException {
        String url = "https://passport.csdn.net/v1/register/pc/sendVerifyCode";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        String urlParameters = "{\"mobile\":\"" + number + "\",\"type\":\"0\",\"code\":\"0086\"}";
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
