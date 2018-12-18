package cn.x5456.netty.websocket.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WebSocketMsg implements Serializable {

    private String from;

    private String msg;

    private Date sendTime;

    private Integer state;

    public WebSocketMsg(String from, String msg, Date sendTime, Integer state) {
        this.from = from;
        this.msg = msg;
        this.sendTime = sendTime;
        this.state = state;
    }

    public WebSocketMsg(String msg, Integer state) {
        this.msg = msg;
        this.state = state;
    }

    public WebSocketMsg() {
    }
}
