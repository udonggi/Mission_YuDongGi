package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> findByToInstaMember(InstaMember toInstaMember) {
        return notificationRepository.findByToInstaMember(toInstaMember);
    }

    @Transactional
    public void whenAfterModifyAttractiveType(LikeablePerson likeablePerson, int oldAttractiveTypeCode) {
        InstaMember toInstaMember = likeablePerson.getToInstaMember();
        InstaMember fromInstaMember = likeablePerson.getFromInstaMember();

        Notification notification = Notification.builder()
                .toInstaMember(toInstaMember)
                .fromInstaMember(fromInstaMember)
                .readDate(null)
                .gender(fromInstaMember.getGender())
                .typeCode("ModifyAttractiveType")
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .newAttractiveTypeCode(likeablePerson.getAttractiveTypeCode())
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void whenAfterLike(LikeablePerson likeablePerson) {
        InstaMember toInstaMember = likeablePerson.getToInstaMember();
        InstaMember fromInstaMember = likeablePerson.getFromInstaMember();

        Notification notification = Notification.builder()
                .toInstaMember(toInstaMember)
                .fromInstaMember(fromInstaMember)
                .readDate(null)
                .gender(fromInstaMember.getGender())
                .typeCode("Like")
                .newAttractiveTypeCode(likeablePerson.getAttractiveTypeCode())
                .build();

        notificationRepository.save(notification);
    }
}