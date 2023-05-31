package io.yangbob.order.domain.member.repository;

import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.entity.MemberId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("기본적인 저장 및 조회")
    void saveAndFindAndDeleteTest() {
        String name = "yangbob";
        String phoneNumber = "01012341234";
        Member member = new Member(name, phoneNumber);
        MemberId memberId = member.getId();
        assertThat(memberRepository.findById(memberId).isPresent()).isFalse();

        memberRepository.save(member);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getName()).isEqualTo(name);
        assertThat(findMember.getPhoneNumber()).isEqualTo(phoneNumber);

        memberRepository.delete(findMember);
        em.flush();
        em.clear();

        assertThat(memberRepository.findById(memberId).isPresent()).isFalse();
    }
}
