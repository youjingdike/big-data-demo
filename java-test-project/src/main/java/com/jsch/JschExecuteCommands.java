package com.jsch;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

public class JschExecuteCommands {
    public static void main(String[] args) throws Exception {
        String user = "root";
        String host = "10.69.75.206";
        int port = 22; // SSH default port
        String password = "Bigdata@KCDE922";

        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        Channel channel = session.openChannel("exec");
        String ns = "default-argo-managed";
        ((ChannelExec)channel).setCommand("kubectl create -f /data/compile/xq/argo/workflow/hello-world.yaml -n "+ns);

        InputStream in = channel.getInputStream();
        InputStream errStream = ((ChannelExec) channel).getErrStream();

        // Connect the channel
        channel.connect();

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        System.out.println("!!!!!!!!");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        System.out.println(stringBuilder.toString());
        String workflowName = stringBuilder.toString().split(" ")[0].split("/")[1];
        System.out.println("workflowName:"+workflowName);

        System.out.println("@@@@@@@");
        // Disconnect the channel and session
        channel.disconnect();

        /*while (!"Completed".equalsIgnoreCase(getResult(session,workflowName,ns))){
            Thread.sleep(10000);
        }*/
        int minute = 2;
        int count = 50;
        int num = 0;
        while (true) {
            num++;
            System.out.println("查寻次数:"+num);
            channel = session.openChannel("exec");

            ((ChannelExec)channel).setCommand("kubectl get po -n "+ns+" | grep "+workflowName +" |awk '{print $3}'");

            in = channel.getInputStream();

            // Connect the channel
            channel.connect();

            stringBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(in));

            System.out.println("!!!!!!!!");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            System.out.println(stringBuilder.toString());
            System.out.println("@@@@@@@");
            // Disconnect the channel and session
            channel.disconnect();
            if ("Completed".equalsIgnoreCase(stringBuilder.toString()) || num == count) {
                break;
            } else {
                Thread.sleep(minute*60*1000/count);
            }
        }
        session.disconnect();
    }

    /*private static String getResult(Session session,String workflowName,String ns) throws Exception {
        Channel channel = session.openChannel("exec");

        ((ChannelExec)channel).setCommand("kubectl get po -n "+ns+" | grep "+workflowName +" |awk '{print $3}'");

        InputStream in = channel.getInputStream();

        // Connect the channel
        channel.connect();

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        System.out.println("!!!!!!!!");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        System.out.println(stringBuilder.toString());
        System.out.println("@@@@@@@");
        // Disconnect the channel and session
        channel.disconnect();
        return stringBuilder.toString();
    }*/
}
