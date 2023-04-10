package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final Rq rq;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if (member.hasConnectedInstaMember() == false) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        if (member.getInstaMember().getFromLikeablePeople().size() >= 10) {
            return RsData.of("F-4", "호감상대는 10명까지만 등록할 수 있습니다.");
        }

        InstaMember fromInstaMember = member.getInstaMember();

        RsData<LikeablePerson> likeDuplicateRsData = canLikeDuplicate(username, attractiveTypeCode, fromInstaMember); // 호감 중복체크
        if (likeDuplicateRsData != null) return likeDuplicateRsData;


        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();


        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(fromInstaMember.getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장


        fromInstaMember.addFromLikeablePerson(likeablePerson); // 내가 호감을 표시한 사람을 추가
        toInstaMember.addToLikeablePerson(likeablePerson); // 나에게 호감을 받은 사람을 추가

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    private RsData<LikeablePerson> canLikeDuplicate(String username, int attractiveTypeCode, InstaMember fromInstaMember) {
        Optional<LikeablePerson> likeablePerson = likeablePersonRepository.findByFromInstaMemberAndToInstaMemberUsername(fromInstaMember, username);

        if (likeablePerson.isPresent()) {
            if(likeablePerson.get().getAttractiveTypeCode() == attractiveTypeCode) {
                return RsData.of("F-3", "같은 사유로 이미 호감상대로 등록되어 있습니다.");
            } else {
                likeablePerson.get().setAttractiveTypeCode(attractiveTypeCode);
                return RsData.of("S-2", "%s에 대한 호감사유를 %s로 변경하였습니다.".formatted(username, attractiveTypeCode));
            }
        }
        return null;
    }


    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    @Transactional
    public RsData<LikeablePerson> delete(Long id) {
        Optional<LikeablePerson> person = likeablePersonRepository.findById(id);

        if (person.isEmpty()) {
            return RsData.of("F-1", "해당 호감상대가 존재하지 않습니다.");
        }

        LikeablePerson likeablePerson = person.get();

        if (likeablePerson.getFromInstaMember().getId() != rq.getMember().getInstaMember().getId()) { //수정: rq클래스를 활용하여 현재 로그인한 멤버의 인스타멤버 아이디를 가져옴
            return RsData.of("F-2", "해당 호감상대를 삭제할 권한이 없습니다.");
        }

        likeablePersonRepository.delete(likeablePerson);

        return RsData.of("S-1", "해당 호감상대가 삭제되었습니다.", likeablePerson);
    }
}
