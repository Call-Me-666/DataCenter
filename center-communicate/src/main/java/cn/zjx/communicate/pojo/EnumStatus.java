package cn.zjx.communicate.pojo;

public enum EnumStatus {
    CONNECTING,     // 连接中
    CONNECTED,      // 连接成功
    RECONNECTING,   // 重连中
    DISCONNECT,     // 断开连接
    STARTING,       // 启动中
    STARTED,        // 已启动
    UNSTART;        // 未启动


    @Override
    public String toString() {
        switch (this){
            case CONNECTING:
                return "连接中";
            case CONNECTED:
                return "连接成功";
            case RECONNECTING:
                return "重连中";
            case DISCONNECT:
                return "断开连接";
            case STARTING:
                return "启动中";
            case STARTED:
                return "已启动";
            case UNSTART:
                return "未启动";
            default:
                return "未启动";
        }
    }
}
