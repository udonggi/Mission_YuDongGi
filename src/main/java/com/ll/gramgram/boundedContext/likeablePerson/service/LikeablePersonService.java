package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.base.event.EventAfterModifyAttractiveType;
import com.ll.gramgram.base.event.EventBeforeCancelLike;
import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final ApplicationEventPublisher publisher;
    private final Rq rq;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if (!member.hasConnectedInstaMember()) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }


        InstaMember fromInstaMember = member.getInstaMember();

        RsData<LikeablePerson> likeDuplicateRsData = checkAttractiveAndUpdate(username, attractiveTypeCode, fromInstaMember); // 호감 중복체크
        if (likeDuplicateRsData != null) return likeDuplicateRsData;

        if (member.getInstaMember().getFromLikeablePeople().size() >= AppConfig.getLikeablePersonFromMax()) { //변경 밑에 있는 이유는 변경은 가능하게 해야 하므로
            return RsData.of("F-4", "호감상대는 10명까지만 등록할 수 있습니다.");
        }

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

        publisher.publishEvent(new EventAfterLike(this, likeablePerson));

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    private RsData<LikeablePerson> checkAttractiveAndUpdate(String username, int attractiveTypeCode, InstaMember fromInstaMember) {
        Optional<LikeablePerson> likeablePerson = likeablePersonRepository.findByFromInstaMemberAndToInstaMember_username(fromInstaMember, username);


        if (likeablePerson.isPresent()) {
            if (likeablePerson.get().getAttractiveTypeCode() == attractiveTypeCode) {
                return RsData.of("F-3", "같은 사유로 이미 호감상대로 등록되어 있습니다.");
            } else {
                modifyAttractionTypeCode(likeablePerson.get(), attractiveTypeCode);
                return RsData.of("S-2", "%s에 대한 호감사유를 변경하였습니다.".formatted(username));
            }
        }
        return null;
    }


    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    @Transactional
    public RsData<LikeablePerson> cancel(Long id) {
        Optional<LikeablePerson> person = likeablePersonRepository.findById(id);

        if (person.isEmpty()) {
            return RsData.of("F-1", "해당 호감상대가 존재하지 않습니다.");
        }

        LikeablePerson likeablePerson = person.get();

        if (likeablePerson.getFromInstaMember().getId() != rq.getMember().getInstaMember().getId()) { //수정: rq클래스를 활용하여 현재 로그인한 멤버의 인스타멤버 아이디를 가져옴
            return RsData.of("F-2", "해당 호감상대를 삭제할 권한이 없습니다.");
        }

        publisher.publishEvent(new EventBeforeCancelLike(this, likeablePerson));


        // 너가 생성한 좋아요가 사라졌어.
        likeablePerson.getFromInstaMember().removeFromLikeablePerson(likeablePerson);

        // 너가 받은 좋아요가 사라졌어.
        likeablePerson.getToInstaMember().removeToLikeablePerson(likeablePerson);

        likeablePersonRepository.delete(likeablePerson);

        return RsData.of("S-1", "해당 호감상대가 삭제되었습니다.", likeablePerson);
    }


    public Optional<LikeablePerson> findById(Long id) {
        return likeablePersonRepository.findById(id);
    }

    @Transactional
    public RsData<LikeablePerson> modifyAttractiveAtList(Member actor, Long id, int attractiveTypeCode) {
        LikeablePerson likeablePerson = findById(id).orElseThrow();
        RsData canModifyRsData = canModifyAttractiveAtList(actor, likeablePerson);

        if (canModifyRsData.isFail()) {
            return canModifyRsData;
        }

        modifyAttractionTypeCode(likeablePerson, attractiveTypeCode);

        return RsData.of("S-1", "호감사유를 수정하였습니다.");
    }

    private void modifyAttractionTypeCode(LikeablePerson likeablePerson, int attractiveTypeCode) {
        int oldAttractiveTypeCode = likeablePerson.getAttractiveTypeCode();
        RsData rsData = likeablePerson.updateAttractionTypeCode(attractiveTypeCode);

        if (rsData.isSuccess()) {
            publisher.publishEvent(new EventAfterModifyAttractiveType(this, likeablePerson, oldAttractiveTypeCode, attractiveTypeCode));
        }
    }

    public RsData canModifyAttractiveAtList(Member actor, LikeablePerson likeablePerson) {
        if (!actor.hasConnectedInstaMember()) {
            return RsData.of("F-1", "먼저 본인의 인스타그램 아이디를 입력해주세요.");
        }

        InstaMember fromInstaMember = actor.getInstaMember();

        if (!Objects.equals(likeablePerson.getFromInstaMember().getId(), fromInstaMember.getId())) {
            return RsData.of("F-2", "해당 호감표시를 취소할 권한이 없습니다.");
        }


        return RsData.of("S-1", "호감표시취소가 가능합니다.");
    }
}
