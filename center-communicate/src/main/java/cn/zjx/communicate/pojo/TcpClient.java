package cn.zjx.communicate.pojo;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public abstract class TcpClient implements ICommunicate, Runnable {

    Selector selector;                          // 选择器
    boolean isRun;                              // 线程是否启动
    SocketChannel channel;                      // tcp客户端通道
    InetSocketAddress address;                  // 本地通信地址
    InetSocketAddress remoteAddress;            // 服务端地址
    Thread thread;                              // 执行线程
    EnumStatus status = EnumStatus.UNSTART;     // 当前状态


    public TcpClient(int localPort, String targetIp, int targetPort) {
        this.address = new InetSocketAddress(localPort);
        this.remoteAddress = new InetSocketAddress(targetIp, targetPort);
    }

    public TcpClient(String localIp, int localPort, String targetIp, int targetPort) {
        this.address = new InetSocketAddress(localIp, localPort);
        this.remoteAddress = new InetSocketAddress(targetIp, targetPort);
    }

    //------------------没有做自动重连，因为socketchannel的connect内部做了
    @Override
    public void start() {
        if (isRun){
//            msgNotified("客户端已启动",null);
            return;
        }
        try {
            selector = Selector.open();
            channel = SocketChannel.open();
            channel.bind(address);
            // 配置为非阻塞模式
            channel.configureBlocking(false);
            // 连接服务端
            boolean isConnect = channel.connect(remoteAddress);
            while(!channel.finishConnect()){
                setStatusAndMsg(EnumStatus.UNSTART, "连接服务端失败，正在重连。。。", null);
                Thread.sleep(2*1000);
            }
            setStatusAndMsg(EnumStatus.STARTED, "已连接上服务端"+channel.getRemoteAddress().toString(), null);

            // 将通道注册到选择器中
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            setStatusAndMsg(EnumStatus.UNSTART, "启动失败，通道启动失败，请重启", e);
        }
        isRun = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() {
        if(!isRun){
//            msgNotified("客户端已关闭",null);
            return;
        }
        isRun = false;
        try {
            // 快速关闭socket,不然端口还是会在一定时间内被占用，状态会编程TIME_WAIT、FIN_WAIT_2
            channel.socket().setSoLinger(true,0);
            channel.close();
            selector.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!channel.isOpen()){
            setStatusAndMsg(EnumStatus.UNSTART, "客户端已关闭", null);
        }
        thread.interrupted();
        thread = null;
    }

    @Override
    public void restart() {
        stop();
        msgNotified("正在重启...",null);
        start();
    }

    @Override
    public void run() {
        while (isRun) {
            try {
                if (selector.select() > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        // 判断是否可读
                        if (key.isReadable()) {
                            // 读取数据
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            buffer.order(ByteOrder.LITTLE_ENDIAN);
                            try {
                                // 如果服务端关闭了，在调用read函数时，就会报错，此处将read报错做为断开连接的依据
                                int count = sc.read(buffer);
                                // 接收到的数大于0，才做处理
                                if(count>0){
                                    buffer.flip();
                                    byte[] data = new byte[buffer.limit()];
                                    buffer.get(data);
                                    // 将数据输出
                                    receive(data);
                                }
                            }catch (Exception e){
                                setStatusAndMsg(EnumStatus.UNSTART,"服务端"+sc.getRemoteAddress().toString()+"已断开连接",e);
                                sc.close();
                            }
                        }
                    }
                    iterator.remove();
                }
                Thread.sleep(1 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(byte[] data){
        try {
            channel.write(ByteBuffer.wrap(data));
        } catch (IOException e) {
            msgNotified("信息发送到"+remoteAddress+"失败",e);
        }
    }

    public abstract void receive(byte[] data);

    public abstract void statusChanged(EnumStatus status);

    public abstract void msgNotified(String msg, Exception e);

    private void setStatusAndMsg(EnumStatus status, String msg, Exception e) {
        if (!this.status.equals(status)) {
            this.status = status;
            statusChanged(status);
        }
        if (!(msg.isBlank() && msg.isEmpty())) {
            msgNotified(msg, e);
        }
    }
}
