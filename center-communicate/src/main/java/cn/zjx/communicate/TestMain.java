package cn.zjx.communicate;

import cn.zjx.communicate.pojo.EnumStatus;
import cn.zjx.communicate.pojo.Udp;

import java.io.UnsupportedEncodingException;

public class TestMain {
    public static void main(String[] args) {
        Udp udp = new Udp(56666) {
            @Override
            public void statusChanged(EnumStatus status) {
                System.out.println(status.toString());
            }

            @Override
            public void receiveData(byte[] data) {
                try {
                    String msg = new String(data,0, data.length,"UTF-8");
                    System.out.println(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
        udp.start();
        try {
            Thread.sleep(1000);
            udp.sendData("123321".getBytes(),"127.0.0.1",57777);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        udp.stop();
    }
}
