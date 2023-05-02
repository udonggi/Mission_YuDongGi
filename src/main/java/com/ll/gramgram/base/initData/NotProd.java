package com.ll.gramgram.base.initData;

import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile({"dev", "test"})
public class NotProd {


    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            InstaMemberService instaMemberService,
            LikeablePersonService likeablePersonService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                Member memberAdmin = memberService.join("admin", "1234", "").getData();
                Member memberUser1 = memberService.join("user1", "1234", "").getData();
                Member memberUser2 = memberService.join("user2", "1234", "").getData();
                Member memberUser3 = memberService.join("user3", "1234", "dongki1882@naver.com").getData();
                Member memberUser4 = memberService.join("user4", "1234", "").getData();
                Member memberUser5 = memberService.join("user5", "1234", "").getData();


                Member memberUser6ByKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__2733176712").getData();
                Member memberUser7ByGoogle = memberService.whenSocialLogin("GOOGLE", "GOOGLE__101539669540183516919").getData();
                Member memberUser8ByNaver = memberService.whenSocialLogin("NAVER", "NAVER__KvykG_FvNSMl9etrRX__1NZb-LQRki99oMdQelcMbBg").getData();
                Member memberUser9ByFacebook = memberService.whenSocialLogin("FACEBOOK", "FACEBOOK__104255525999749").getData();


                instaMemberService.connect(memberUser2, "insta_user2", "M");
                instaMemberService.connect(memberUser3, "insta_user3", "W");
                instaMemberService.connect(memberUser4, "insta_user4", "M");
                instaMemberService.connect(memberUser5, "insta_user5", "M");

                likeablePersonService.like(memberUser3, "insta_user4", 1);
                likeablePersonService.like(memberUser3, "insta_user100", 2);
            }
        };
    }
}
