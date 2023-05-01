package com.ll.gramgram.boundedContext.member.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usr/member") // 액션 URL의 공통 접두어
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final Rq rq;


    @PreAuthorize("isAnonymous()")
    @GetMapping("/login") // 로그인 폼, 로그인 폼 처리는 스프링 시큐리티가 구현, 폼 처리시에 CustomUserDetailsService 가 사용됨
    public String showLogin() {
        return "usr/member/login";
    }

    @PreAuthorize("isAuthenticated()") // 로그인 해야만 접속가능
    @GetMapping("/me") // 로그인 한 나의 정보 보여주는 페이지
    public String showMe() {

        return "usr/member/me";
    }

    @AllArgsConstructor
    @Getter
    public static class FindLoginIdForm {
        @Email
        @NotBlank
        private final String email;
    }

    @GetMapping("/findLoginId")
    public String showFindLoginId() {
        return "usr/member/findLoginId";
    }

    @PostMapping("/findLoginId")
    public String findLoginId(@Valid FindLoginIdForm findLoginIdForm) {
        RsData<Member> findLoginIdRs = memberService.findLoginId(findLoginIdForm.getEmail());

        if (findLoginIdRs.isFail()) {
            return rq.historyBack(findLoginIdRs);
        }

        return rq.redirectWithMsg("/usr/member/login", findLoginIdRs);
    }


    @AllArgsConstructor
    @Getter
    public static class FindLoginPwForm {
        @NotBlank
        @Size(min = 4, max = 30)
        private final String username;

        @NotBlank
        @Email
        private final String email;
    }


    @GetMapping("/findLoginPw")
    public String showFindLoginPw() {
        return "usr/member/findLoginPw";
    }


    @PostMapping("/findLoginPw")
    public String findLoginPw(@Valid FindLoginPwForm findLoginPwForm) {
        RsData<Member> findLoginPwRs = memberService.findLoginPw(findLoginPwForm.getUsername(), findLoginPwForm.getEmail());

        if (findLoginPwRs.isFail()) {
            return rq.historyBack(findLoginPwRs);
        }

        return rq.redirectWithMsg("/usr/member/login", findLoginPwRs);
    }

}
