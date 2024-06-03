package com.beginvegan.domain.alarm.domain;

import com.beginvegan.domain.common.BaseEntity;
import com.beginvegan.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private Long itemId; // 알람 타입에 따라 review의 id거나 magazine id

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean isRead; // 확인여부

    @Builder
    public Alarm(Long id, AlarmType alarmType, Long itemId, String content, User user) {
        this.id = id;
        this.alarmType = alarmType;
        this.itemId = itemId;
        this.content = content;
        this.user = user;
        this.isRead = false;
    }

    public void updateIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
