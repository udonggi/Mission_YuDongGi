package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeablePersonRepository extends JpaRepository<LikeablePerson, Long>, LikeablePersonRepositoryCustom {
    List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId);

//    boolean existsByFromInstaMemberAndToInstaMemberUsernameAndAttractiveTypeCode(InstaMember fromInstaMember, String username, int attractiveTypeCode);

    Optional<LikeablePerson> findByFromInstaMemberAndToInstaMember_username(InstaMember fromInstaMember, String username);
}
