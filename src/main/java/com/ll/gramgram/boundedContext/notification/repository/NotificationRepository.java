package com.ll.gramgram.boundedContext.notification.repository;

import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByToInstaMember(InstaMember toInstaMember);

//    int countByToInstaMemberAndReadDateIsNull(InstaMember instaMember); //나중에 쓸 수 있음 (안읽은 알림 개수)
}