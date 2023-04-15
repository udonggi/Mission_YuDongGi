package com.ll.gramgram.boundedContext.member.service;

import com.ll.gramgram.base.emailSender.EmailSenderService;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 아래 메서드들이 전부 readonly 라는 것을 명시, 나중을 위해
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final EmailSenderService emailSenderService;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }


    @Transactional // SELECT 이외의 쿼리에 대한 가능성이 아주 조금이라도 있으면 붙인다.
    // 일반 회원가입(소셜 로그인을 통한 회원가입이 아님)
    public RsData<Member> join(String username, String password, String email) {
        // "GRAMGRAM" 해당 회원이 일반회원가입으로 인해 생성되었다는걸 나타내기 위해서
        return join("GRAMGRAM", username, password, email);
    }

    // 내부 처리함수, 일반회원가입, 소셜로그인을 통한 회원가입(최초 로그인 시 한번만 발생)에서 이 함수를 사용함
    private RsData<Member> join(String providerTypeCode, String username, String password, String email) {
        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        if (findByEmail(email).isPresent()) {
            return RsData.of("F-2", "해당 이메일(%s)은 이미 사용중입니다.".formatted(email));
        }

        // 소셜 로그인을 통한 회원가입에서는 비번이 없다.
        if (StringUtils.hasText(password)) password = passwordEncoder.encode(password);

        Member member = Member
                .builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .password(password)
                .build();
        if (!email.isBlank()) {
            member.setEmail(email);
            emailSenderService.mailSend(email);
        } else {
            member.setEmail("null");
        }

        memberRepository.save(member);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    private Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }


    // 해당 회원에게 인스타 계정을 연결시킨다.
    // 1:1 관계
    @Transactional
    public void updateInstaMember(Member member, InstaMember instaMember) {
        member.setInstaMember(instaMember);
        memberRepository.save(member); // 여기서 실제로 UPDATE 쿼리 발생
    }

    // 소셜 로그인(카카오, 구글, 네이버) 로그인이 될 때 마다 실행되는 함수
    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username) {
        Optional<Member> opMember = findByUsername(username); // username 예시 : KAKAO__1312319038130912, NAVER__1230812300

        if (opMember.isPresent()) return RsData.of("S-2", "로그인 되었습니다.", opMember.get());

        // 소셜 로그인를 통한 가입시 비번은 없다.
        return join(providerTypeCode, username, "", ""); // 최초 로그인 시 딱 한번 실행
    }


    public RsData<Member> findLoginId(String email) {
        Optional<Member> member = findByEmail(email);
        if (member.isPresent()) {
            emailSenderService.mailSend(email, member.get().getUsername());
            return RsData.of("S-1", "아이디를 메일로 발송하였습니다.", member.get());
        }
        return RsData.of("F-1", "해당 이메일로 가입된 회원이 없습니다.");
    }


    @Transactional
    public RsData<Member> findLoginPw(String username, String email) {
        Optional<Member> member = findByUsername(username);
        if (member.isPresent() && member.get().getEmail().equals(email)) {
            String tempPw = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10); // 임시 비밀번호 생성
            emailSenderService.mailSend(email, tempPw); // 임시 비밀번호 메일로 발송
            member.get().setPassword(passwordEncoder.encode(tempPw)); // 임시 비밀번호 암호화
            return RsData.of("S-1", "비밀번호를 메일로 발송하였습니다.", member.get());
        }
        return RsData.of("F-1", "해당 이메일로 가입된 회원이 없습니다.");
    }

}
