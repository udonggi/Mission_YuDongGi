package com.ll.gramgram.boundedContext.notification.service;


import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class NotificationServiceTests {
    @Autowired
    private MemberService memberService;
    @Autowired
    private LikeablePersonService likeablePersonService;
    @Autowired
    private NotificationService notificationService;

    @Test
    @DisplayName("호감표시한 후 생성된 알림 확인하기")
    void t001() throws Exception {
        Member memberUser2 = memberService.findByUsername("user2").orElseThrow();
        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();

        // user2 (insta_user2) -> user3(insta_user3) 에게 호감표시
        LikeablePerson likeablePersonToInstaUser = likeablePersonService.like(memberUser2, "insta_user3", 1).getData();

        // user5 에게 전송된 알림들 모두 가져오기
        List<Notification> notifications = notificationService.findByToInstaMember(memberUser3.getInstaMember());

        // 그중에 최신 알림 가져오기
        Notification lastNotification = notifications.get(notifications.size() - 1);

        assertThat(lastNotification.getFromInstaMember().getUsername()).isEqualTo("insta_user2");
        assertThat(lastNotification.getTypeCode()).isEqualTo("Like");
        assertThat(lastNotification.getNewAttractiveTypeCode()).isEqualTo(1);
    }

    @Test
    @DisplayName("호감사유 변경한 후 생성된 알림 확인하기")
    void t002() throws Exception {
        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();
        Member memberUser4 = memberService.findByUsername("user4").orElseThrow();

        //  insta_use3 -> insta_user4, type : 1 -> 2 로 변경
        likeablePersonService.modifyAttractiveAtList(memberUser3, 1L, 2);

        List<Notification> notifications = notificationService.findByToInstaMember(memberUser4.getInstaMember());

        Notification lastNotification = notifications.get(notifications.size() - 1);


        assertThat(lastNotification.getFromInstaMember().getUsername()).isEqualTo("insta_user3");
        assertThat(lastNotification.getTypeCode()).isEqualTo("ModifyAttractiveType");
        assertThat(lastNotification.getOldAttractiveTypeCode()).isEqualTo(1);
        assertThat(lastNotification.getNewAttractiveTypeCode()).isEqualTo(2);
    }
}