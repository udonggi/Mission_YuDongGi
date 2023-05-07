package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        make(likeablePerson, "ModifyAttractiveType", oldAttractiveTypeCode);

    }

    @Transactional
    public void whenAfterLike(LikeablePerson likeablePerson) {
        make(likeablePerson, "Like", 0);
    }

    private void make(LikeablePerson likeablePerson, String typeCode, int oldAttractiveTypeCode) {
        InstaMember toInstaMember = likeablePerson.getToInstaMember();
        InstaMember fromInstaMember = likeablePerson.getFromInstaMember();
        Notification notification = Notification.builder()
                .toInstaMember(toInstaMember)
                .fromInstaMember(fromInstaMember)
                .readDate(null)
                .gender(fromInstaMember.getGender())
                .typeCode(typeCode)
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .newAttractiveTypeCode(likeablePerson.getAttractiveTypeCode())
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void updateReadDate(List<Notification> notifications) {
        //알림 읽으면 readDate 읽은 시간으로 세팅
        notifications.forEach(notification -> {
            if(notification.getReadDate() == null) {
                notification.setReadDate(LocalDateTime.now());
            }
        });
    }

//    public boolean countUnreadNotificationsByToInstaMember(InstaMember instaMember) {
//        return notificationRepository.countByToInstaMemberAndReadDateIsNull(instaMember) > 0;
//    } // 안 읽은 알림 체크할때 사용하는데 나중에 속도 더 빠른거로 바꾸기

}