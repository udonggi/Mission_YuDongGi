package com.ll.gramgram.boundedContext.likeablePerson.service;


import com.ll.gramgram.TestUt;
import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class LikeablePersonServiceTests {
    @Autowired
    private MemberService memberService;
    @Autowired
    private LikeablePersonService likeablePersonService;
    @Autowired
    private LikeablePersonRepository likeablePersonRepository;

    @Test
    @DisplayName("설정파일에서 호감표시에 대한 수정쿨타임 가져오기")
    void t001() throws Exception {
        System.out.println("likeablePersonModifyCoolTime : " + AppConfig.getLikeablePersonModifyCoolTime());
        assertThat(AppConfig.getLikeablePersonModifyCoolTime()).isGreaterThan(0);
    }

    @Test
    @DisplayName("호감표시를 하면 쿨타임이 지정된다.")
    void t002() throws Exception {
        LocalDateTime coolTime = AppConfig.genLikeablePersonModifyUnlockDate();

        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();
        LikeablePerson likeablePersonToBts = likeablePersonService.like(memberUser3, "bts", 3).getData();

        assertThat(
                likeablePersonToBts.getModifyUnlockDate().isAfter(coolTime)
        ).isTrue();
    }

    @Test
    @DisplayName("호감사유를 변경하면 쿨타임이 갱신된다.")
    void t008() throws Exception {
        // 현재시점 기준에서 쿨타임이 다 차는 시간을 구한다.(미래)
        LocalDateTime coolTime = AppConfig.genLikeablePersonModifyUnlockDate();

        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();
        // 호감표시를 생성한다.
        LikeablePerson likeablePersonToBts = likeablePersonService.like(memberUser3, "bts", 3).getData();

        // 호감표시를 생성하면 쿨타임이 지정되기 때문에, 그래서 바로 수정이 안된다.
        // 그래서 강제로 쿨타임이 지난것으로 만든다.
        // 테스트를 위해서 억지로 값을 넣는다.
        TestUt.setFieldValue(likeablePersonToBts, "modifyUnlockDate", LocalDateTime.now().minusSeconds(-1));

        // 수정을 하면 쿨타임이 갱신된다.
        likeablePersonService.modifyAttractiveAtList(memberUser3, likeablePersonToBts.getId(), 1 );

        // 갱신 되었는지 확인
        assertThat(
                likeablePersonToBts.getModifyUnlockDate().isBefore(coolTime)
        ).isTrue();
    }
}