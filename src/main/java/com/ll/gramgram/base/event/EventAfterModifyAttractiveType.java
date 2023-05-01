package com.ll.gramgram.base.event;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterModifyAttractiveType {
    private final LikeablePerson likeablePerson;
    private final int oldAttractiveTypeCode;

    public EventAfterModifyAttractiveType( LikeablePerson likeablePerson, int oldAttractiveTypeCode, int newAttractiveTypeCode) {
        this.likeablePerson = likeablePerson;
        this.oldAttractiveTypeCode = oldAttractiveTypeCode;
    }
}