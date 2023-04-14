package com.ll.gramgram.base.emailSender;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailSenderService {
    private final JavaMailSender mailSender;

    @Async
    public void mailSend(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("gramgram 회원가입을 축하드립니다!");
        message.setText("회원가입을 축하드립니다!");

        mailSender.send(message);
    }


    @Async
    public void mailSend(String email, String findInfo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("gramgram 내 정보 찾기");
        message.setText("찾으시려는 것은 " + findInfo + " 입니다.");

        mailSender.send(message);
    }

}
