package com.needhelp.aips.infrastructure.message;

import org.springframework.stereotype.Service;

/**
 * 消息中心服务（空壳）。
 * TODO: 对接 APP 推送 / 短信 / 站内信渠道。
 */
@Service
public class MessageService {

    /** 消息渠道：1 APP推送 2 短信 3 站内信 */

    /**
     * 发送消息。
     * TODO: 实际对接推送 SDK、短信接口
     */
    public void send(Long userId, String title, String content, int channel) {
        // TODO: 根据 channel 选择推送渠道
    }

    /**
     * 发送用药提醒。
     * TODO: 定时任务调用此方法
     */
    public void sendMedicineReminder(Long userId, String medicineName, String dosage) {
        String content = "请按时服用 " + medicineName + "，" + dosage;
        send(userId, "用药提醒", content, 1); // APP推送
    }

    /**
     * 发送订单状态通知。
     * TODO: 订单状态变更时调用
     */
    public void sendOrderNotification(Long userId, String orderNo, String statusText) {
        String content = "您的订单 " + orderNo + " 状态已更新为：" + statusText;
        send(userId, "订单通知", content, 1);
    }
}
