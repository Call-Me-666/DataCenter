package cn.zjx.communicate.pojo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.*;
import java.util.Iterator;

public abstract class TcpServer implements ICommunicate,Runnable {
    Selector selector;                          // 选择器
    ServerSocketChannel channel;                // 服务端通道
    boolean isRun;                              // 是否运行
    Thread thread;                              // 服务端线程
    InetSocketAddress address;                  // 服务端地址
    EnumStatus status = EnumStatus.UNSTART;     // 当前通道的状态

    public TcpServer(int port){
        address = new InetSocketAddress(port);
    }

    public TcpServer(String ip,int port){
        address = new InetSocketAddress(ip,port);
    }

    @Override
    public void start() {
        if(isRun){
            return;
        }
        isRun = true;

        thread = null;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() {
        if(!isRun){
            return;
        }
        isRun = false;
        // 停止selector.select()的阻塞
        selector.wakeup();
    }

    /**
     * 打开通道和选择器
     */
    private void open(){
        try {
            selector = Selector.open();
            channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.bind(address);
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            setStatusAndMsg(EnumStatus.UNSTART,"服务端已失败",e);
        }
        setStatusAndMsg(EnumStatus.STARTED,"服务端已启动",null);
    }

    /**
     * 关闭通道和选择器
     */
    private void close(){
        try {
            // 关闭所有的客户端连接
            selector.keys().forEach(p->{
                if(p.channel() instanceof SocketChannel){
                    SocketChannel sc = (SocketChannel) p.channel();
                    try {
                        // System.out.println("关闭了"+sc.getRemoteAddress()+"的连接");
                        sc.socket().setSoLinger(true,0);
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            channel.socket().close();
            channel.close();
            selector.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!channel.isOpen()){
            setStatusAndMsg(EnumStatus.UNSTART, "服务端已关闭", null);
        }
    }

    @Override
    public void restart() {
        stop();
        msgNotified("正在重启...",null);
        start();
    }

    @Override
    public void run() {
        open();

        try{
            while(isRun){
                // !!!!!!selector.select()是阻塞函数
                // 需要调用wakeup()将selector唤醒才会在无触发事件的情况下停止阻塞
                if(selector.select()>0){
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()){
                        SelectionKey selectionKey = iterator.next();
                        // 如果是客户端连接，就获取到客户端通道，注册到selector中
                        if(selectionKey.isAcceptable()){
                            SocketChannel socketChannel = this.channel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector,SelectionKey.OP_READ);
                            msgNotified("客户端"+socketChannel.getRemoteAddress().toString()+"已上线",null);
                        }else if(selectionKey.isReadable()){
                            // 读取数据
                            SocketChannel sc = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            buffer.order(ByteOrder.LITTLE_ENDIAN);
                            try {
                                // 如果客户端断开了连接，在调用read函数时，就会报错，此处将read报错做为断开连接的依据
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
                                msgNotified("客户端"+sc.getRemoteAddress().toString()+"已断开连接",e);
                                // read报错后，直接关闭通道，关闭通道才会释放selector中的通道
                                sc.close();
                            }
                        }
                    }
                    iterator.remove();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
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

    public void send(byte[] data){
        try {
            for (SelectionKey key :selector.keys()) {
                Channel channel = key.channel();
                // 如果遍历的对象不是自己，就发送数据
                if(channel instanceof SocketChannel){
                    SocketChannel socketChannel = (SocketChannel) channel;
                    socketChannel.write(ByteBuffer.wrap(data));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void send(byte[] data,SocketChannel selfChannel){
        try {
            for (SelectionKey key :selector.keys()) {
                Channel channel = key.channel();
                // 如果遍历的对象不是自己，就发送数据
                if(channel instanceof SocketChannel && channel !=selfChannel){
                    SocketChannel socketChannel = (SocketChannel) channel;
                    socketChannel.write(ByteBuffer.wrap(data));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void send(byte[] data,SocketChannel selfChannel,SocketChannel targetChannel){
        //TODO:确认怎么获取到targetChannel
        try {
            for (SelectionKey key :selector.keys()) {
                Channel channel = key.channel();
                // 如果遍历的对象是目标对象，就发送数据
                if(channel instanceof SocketChannel && channel ==targetChannel){
                    SocketChannel socketChannel = (SocketChannel) channel;
                    socketChannel.write(ByteBuffer.wrap(data));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
