package io.yangbob.order.domain.member.repository;

import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.member.entity.MemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, MemberId> {

}
