package com.ll.gramgram.base.event;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventBeforeCancelLike {
    private final LikeablePerson likeablePerson;

    public EventBeforeCancelLike(LikeablePerson likeablePerson) {
        this.likeablePerson = likeablePerson;
    }
}