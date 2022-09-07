package cn.zjx.communicate;

import cn.zjx.communicate.pojo.EnumStatus;
import cn.zjx.communicate.pojo.TcpServer;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class TcpServerTest {
    public static void main(String[] args) {
        TcpServer tcpServer = new TcpServer(59876) {
            @Override
            public void receive(byte[] data) {
                try {
                    System.out.println(new String(data,0,data.length,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void statusChanged(EnumStatus status) {
                System.out.println(status.toString());
            }

            @Override
            public void msgNotified(String msg, Exception e) {
                System.out.println(msg);
            }
        };
        tcpServer.start();
        tcpServer.stop();
        tcpServer.start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String text = scanner.nextLine();
            try {
                tcpServer.send(text.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
