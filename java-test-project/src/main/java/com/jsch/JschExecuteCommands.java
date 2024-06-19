package com.jsch;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;

public class JschExecuteCommands {
    public static void main(String[] args) throws Exception {
        tst();
//        tst2();
    }

    private static void tst() throws JSchException, IOException, InterruptedException {
        String username = "root";
        String host = "xxx.xxx.xxx.xxx";
        int port = 22; // SSH default port
        String password = "xxx";

        JSch jsch = new JSch();

        // 设置SSH连接的用户名、主机和端口
        // 创建SSH会话
        Session session = jsch.getSession(username, host, port);
        // 设置SSH会话的密码
        session.setPassword(password);
        System.out.println(host+":"+username+":"+password);
        // 禁用SSH主机密钥检查
        session.setConfig("StrictHostKeyChecking", "no");
        // 建立SSH连接
        session.connect();

        // 打开SSH通道
        Channel channel = session.openChannel("exec");
        String name = "auto-tst-pi-"+System.currentTimeMillis();
        // 设置执行的命令
//        String command = "source ~/.bashrc;export JAVA_HOME=/usr/local/java; export HADOOP_CONF_DIR=/opt/hadoop-3.2.0/etc/hadoop; ";
        String command = "export JAVA_HOME=/usr/local/java\n";
        String command1 = "export HADOOP_CONF_DIR=/opt/hadoop-3.2.0/etc/hadoop\n";

        String command2 = "/opt/spark/bin/spark-submit  --master yarn --deploy-mode cluster     --queue yarn-queue-spark  --name="+name+"  --class org.apache.spark.examples.SparkPi     --driver-memory 1g     --num-executors 2     --executor-memory 1g     --executor-cores 1     --conf spark.yarn.stagingDir=\"alluxio:///kdl/kcde/spark/staging\"  /opt/spark/examples/jars/spark-examples_2.12-3.1.2.jar 10\n";
        String command3 = "echo '@@@@@@@@@@@@tst@@@@@@@@@@@@'\n";
//        command += "kubectl exec -it yarnify-yarnify-yarnify-0 -n default-yarnify -- echo \"tst\"";
        System.out.println(command);
        // 在SSH通道中执行命令
        ((ChannelExec) channel).setCommand("/bin/bash");

        // 获取命令执行的输出流
//        channel.setInputStream(null);
//        ((ChannelExec) channel).setErrStream(System.out);
        //channel.setPty(true);
        // 读取命令执行的输出
        InputStream in = channel.getInputStream();
        OutputStream out = channel.getOutputStream();
        InputStream errStream = ((ChannelExec) channel).getErrStream();
        // 连接SSH通道
        channel.connect();

        out.write("sudo su - yarn\n".getBytes());
//        out.write(command.getBytes());
//        out.write(command1.getBytes());
        out.write(command3.getBytes());
        out.write(command2.getBytes());
        out.write(command3.getBytes());
        // Flush the output stream to ensure that all data is sent
        out.flush();
        out.close();

        System.out.println("命令发送完毕。。。");

        Thread thread = new Thread(() -> {
            String line1;
            StringBuilder errStringBuilder = new StringBuilder();
            BufferedReader errBufferedReader = new BufferedReader(new InputStreamReader(errStream));
            while (true) {
                try {
                    if (!((line1 = errBufferedReader.readLine()) != null)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                errStringBuilder.append(line1 + "\n");
//                System.out.println(line1 + "\n");
            }
            System.out.println("error:" + errStringBuilder);
        });
        thread.start();

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line+"\n");
            System.out.println(line+"\n");
        }
        System.out.println("info:"+stringBuilder);
//        thread.join();

        //vars.put("pi", stringBuilder.toString());

        /*StringBuilder errStringBuilder = new StringBuilder();
        BufferedReader errBufferedReader = new BufferedReader(new InputStreamReader(errStream));
        while ((line = errBufferedReader.readLine()) != null) {
            errStringBuilder.append(line+"\n");
        }
        System.out.println("error:"+ errStringBuilder);*/
        // 关闭SSH通道和会话
        channel.disconnect();
        session.disconnect();
    }

    private static void tst2() throws JSchException, IOException, InterruptedException {
        String user = "root";
        String host = "xxx.xxx.xxx.xxx";
        int port = 22; // SSH default port
        String password = "xxx";

        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        Channel channel = session.openChannel("exec");
        ((ChannelExec)channel).setCommand("/bin/bash");

        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();
        InputStream errIn = ((ChannelExec) channel).getErrStream();

        // Connect the channel
        channel.connect();

        // Send commands through the bash shell
        String command1 = "ls -l\n";
        String command2 = "pwd\n";
        String command3 = "echo 'Hello World'\n";

        out.write(command1.getBytes());
        out.write(command2.getBytes());
        out.write(command3.getBytes());

        // Flush the output stream to ensure that all data is sent
        out.flush();
        //注意：必须关掉否则BufferedReader会一直阻塞
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        BufferedReader errReader = new BufferedReader(new InputStreamReader(errIn));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
//            System.out.println(channel.getExitStatus());
        }
        System.out.println("#####");

        while ((line = errReader.readLine()) != null) {
            System.out.println(line);
//            System.out.println(channel.getExitStatus());
        }
        System.out.println("%%%%%%%%");
        // Disconnect the channel and session
        channel.disconnect();
        session.disconnect();
    }

    private static void tst1() throws JSchException, IOException, InterruptedException {
        String user = "root";
        String host = "xxx.xxx.xxx.xxx";
        int port = 22; // SSH default port
        String password = "xxx";

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
