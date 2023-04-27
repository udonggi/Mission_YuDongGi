package com.ll.gramgram.boundedContext.likeablePerson.controller;


import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LikeablePersonControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private LikeablePersonRepository likeablePersonRepository;
    @Autowired
    private Rq rq;
    @Autowired
    private LikeablePersonService likeablePersonService;

    @Test
    @DisplayName("등록 폼(인스타 인증을 안해서 폼 대신 메세지)")
    @WithUserDetails("user1")
    void t001() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        먼저 본인의 인스타그램 아이디를 입력해주세요.
                        """.stripIndent().trim())))
        ;
    }

    @Test
    @DisplayName("등록 폼")
    @WithUserDetails("user2")
    void t002() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    @DisplayName("등록 폼 처리(user2가 user3에게 호감표시(외모))")
    @WithUserDetails("user2")
    void t003() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user3")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());
        ;
    }

    @Test
    @DisplayName("등록 폼 처리(user2가 abcd에게 호감표시(외모), abcd는 아직 우리 서비스에 가입하지 않은상태)")
    @WithUserDetails("user2")
    void t004() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "abcd")
                        .param("attractiveTypeCode", "2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());
        ;
    }

    @Test
    @DisplayName("호감목록")
    @WithUserDetails("user3")
    void t005() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showList"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        <span class="toInstaMember_username">insta_user4</span>
                        """.stripIndent().trim())))
        ;
    }

    @Test
    @DisplayName("user3이 user4에게 호감표시(외모)를 삭제한다. (user3이 user4에게 호감표시(외모)를 한 상태)")
    @WithUserDetails("user3")
    void t006() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(
                        delete("/likeablePerson/1")
                                .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("cancel"))
                .andExpect(status().is3xxRedirection());

        assertThat(likeablePersonRepository.findById(1L).isEmpty()).isTrue(); //추가: 삭제가 되었는지 확인

    }

    @Test
    @DisplayName("없는 호감 삭제(삭제가 안됨)")
    @WithUserDetails("user3")
    void t007() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(
                        delete("/likeablePerson/10")
                                .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("cancel"))
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    @DisplayName("user3가 한 것을 user2가 삭제하려고 시도한다. (삭제 실패)")
    @WithUserDetails("user2")
    void t008() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(
                        delete("/likeablePerson/1")
                                .with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("cancel"))
                .andExpect(status().is4xxClientError())
        ;

        assertThat(likeablePersonRepository.findById(1L).isPresent()).isEqualTo(true);
    }

    @Test
    @DisplayName("user3이 user4에게 외모 중복 호감표시를 했을 때")
    @WithUserDetails("user3")
    void t009() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user4")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @DisplayName("한명의 instaMember가 11명 이상의 호감상대를 등록할 수 없다.")
    @WithUserDetails("user5")
    void t010() throws Exception{
        Member memberUser5 = rq.getMember();
        IntStream.range(0, (int) AppConfig.getLikeablePersonFromMax())
                .forEach(index -> {
                    likeablePersonService.like(memberUser5, "insta_userr%30d".formatted(index), 1);
                });
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")  //11번째 추가
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user500")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError()); // 11번째는 추가가 되지 않고 Historyback

    }

    @Test
    @DisplayName("같은 회원 중복 호감표시할 때 다른 유형의 호감표시는 가능하다.")
    @WithUserDetails("user3")
    void t011() throws Exception{
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user4")
                        .param("attractiveTypeCode", "2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());

        assertThat(likeablePersonRepository.findById(1L).get().getAttractiveTypeCode()).isEqualTo(2);
    }


}
