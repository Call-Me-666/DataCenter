package cn.zjx.communicate;

import cn.zjx.communicate.pojo.EnumStatus;
import cn.zjx.communicate.pojo.TcpClient;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class TcpClientTest {
    public static void main(String[] args) {
        TcpClient client = new TcpClient(56789,"192.168.1.12",59876) {
            @Override
            public void receive(byte[] data) {
                try {
                    System.out.println(new String(data,0, data.length,"UTF-8"));
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
        client.start();
//        client.start();
//
//        client.stop();

//        client.start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String text = scanner.nextLine();
            try {
                client.send(text.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
