package cn.zjx.communicate.pojo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;

/**
 * udp通信
 */
public abstract class Udp implements ICommunicate, Runnable {
    DatagramChannel channel;    // udp通道
    InetSocketAddress address;  // 本地通信信息
    Thread thread;              // 接收数据的线程
    boolean isRun;              // 线程是否启动
    int cacheSize = 65535;      // 缓存大小
    EnumStatus status = EnumStatus.UNSTART;  // 默认未启动


    public Udp(int port) {
        this.address = new InetSocketAddress(port);
    }

    public Udp(String ip, int port) {
        this.address = new InetSocketAddress(ip, port);
    }


    @Override
    public void start() {
        // 如果已经启动,就不需要再次启动了
        if (isRun) {
            return;
        }
        try {
            channel = DatagramChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRun = true;
        thread = null;
        thread = new Thread(this);
        thread.start();
        setStatus(EnumStatus.STARTING);
    }

    @Override
    public void stop() {
        if (!isRun) {
            return;
        }
        isRun = false;

        // 停止线程，释放资源
        thread.interrupt();
        thread = null;

        try {
            channel.socket().close();
            channel.close();
            channel = null;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            setStatus(EnumStatus.UNSTART);
        }
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public void run() {
        if(channel.isOpen()){
            try {
                channel.bind(address);
            } catch (IOException e) {
                e.printStackTrace();
                setStatus(EnumStatus.UNSTART);
            }
            setStatus(EnumStatus.STARTED);
            while (isRun) {
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(cacheSize);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);      // 设置小端序
                    channel.receive(buffer);
                    buffer.flip();  // 切换到读模式
                    byte[] data = new byte[buffer.limit()];
                    buffer.get(data);
                    // 抛出数据
                    receiveData(data);
                }catch (ClosedByInterruptException e){
                    // 关闭线程时，导致通道报错，吃掉这个异常
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 状态改变的通知
     *
     * @param status
     */
    public abstract void statusChanged(EnumStatus status);

    private void setStatus(EnumStatus status){
        this.status = status;
        statusChanged(this.status);
    }

    public EnumStatus getStatus() {
        return status;
    }

    /**
     * 接收数据
     * @param data
     */
    public abstract void receiveData(byte[] data);

    public void sendData(byte[] data,String targetIp,int targetPort){
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(data);
        buffer.flip();
        InetSocketAddress targetAddress;
        if(targetIp.isEmpty()||targetIp.isBlank()){
            targetAddress = new InetSocketAddress(targetPort);
        }else{
            targetAddress = new InetSocketAddress(targetIp,targetPort);
        }
        try {
            channel.send(buffer,targetAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
