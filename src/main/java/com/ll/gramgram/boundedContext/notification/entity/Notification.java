package com.ll.gramgram.boundedContext.notification.entity;

import com.ll.gramgram.base.baseEntity.BaseEntity;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Notification extends BaseEntity {
    @Setter
    private LocalDateTime readDate;
    @ManyToOne
    @ToString.Exclude
    private InstaMember toInstaMember; // 메세지 받는 사람(호감 받는 사람)
    @ManyToOne
    @ToString.Exclude
    private InstaMember fromInstaMember; // 메세지를 발생시킨 행위를 한 사람(호감표시한 사람)
    private String typeCode; // 호감표시=Like, 호감사유변경=ModifyAttractiveType
    private String gender; // 해당사항 없으면 null
    private int oldAttractiveTypeCode; // 해당사항 없으면 0
    private int newAttractiveTypeCode; // 해당사항 없으면 0

    public String getNotificationDateStr() {
        return "%s분전".formatted(ChronoUnit.MINUTES.between(this.getCreateDate(), LocalDateTime.now()));
    }

    public boolean isTypeCodeLike() {
        return typeCode.equals("Like");
    }

    public boolean isTypeCodeModifyAttractiveType() {
        return typeCode.equals("ModifyAttractiveType");
    }

    public String getNewAttractiveTypeDisplayName() {
        return switch (newAttractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            default -> "능력";
        };
    }

    public String getOldAttractiveTypeDisplayName() {
        return switch (oldAttractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            default -> "능력";
        };
    }

    public String getGenderDisplayName() {
        return switch (gender) {
            case "W" -> "여성";
            default -> "남성";
        };
    }
}