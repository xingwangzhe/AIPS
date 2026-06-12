package com.needhelp.aips.dto.consult;

public class MessageRequest {
    private String content;
    private Integer msgType = 1; // 1文本 2语音

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getMsgType() { return msgType; }
    public void setMsgType(Integer msgType) { this.msgType = msgType; }
}
