package com.af.tourism.service.helper;

import com.af.tourism.mq.producer.DiaryInteractionNotificationMessageProducer;
import com.af.tourism.pojo.dto.common.DiaryInteractionNotifyCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryInteractionNotificationPublishService {

    private final DiaryInteractionNotificationMessageProducer diaryInteractionNotificationMessageProducer;
    private final DiaryInteractionNotificationService diaryInteractionNotificationService;

    /**
     * 提交事务之后发送消息
     * @param command 日记互动通知命令
     */
    public void publishAfterCommit(DiaryInteractionNotifyCommand command) {
        // 1.检查当前线程是否存在 Spring 事务
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // 1.1.存在事务注册事务回调函数
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 1.2.事务提交发送消息
                    publish(command);
                }
            });
            return;
        }

        // 2.直接发送消息
        publish(command);
    }

    /**
     * 发送消息
     * @param command 日记互动通知命令
     */
    private void publish(DiaryInteractionNotifyCommand command) {
        try {
            diaryInteractionNotificationMessageProducer.send(command);
        } catch (Exception ex) {
            log.warn("发送日记互动通知 MQ 消息失败，降级为同步创建通知, type={}, triggerUserId={}, recipientUserId={}, diaryId={}",
                    command == null ? null : command.getType(),
                    command == null ? null : command.getTriggerUserId(),
                    command == null ? null : command.getRecipientUserId(),
                    command == null ? null : command.getRelatedDiaryId(),
                    ex);
            diaryInteractionNotificationService.notifyInteraction(command);
        }
    }
}
