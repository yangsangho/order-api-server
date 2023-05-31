package io.yangbob.order.domain.member.entity;

import io.yangbob.order.domain.common.entity.PrimaryKeyEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter(AccessLevel.PROTECTED)
@Getter
public class Member extends PrimaryKeyEntity<MemberId> {

    protected Member() {
        super(new MemberId());
    }

    public Member(String name, String phoneNumber) {
        this();
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Column(columnDefinition = "varchar", nullable = false)
    private String name;

    @Column(columnDefinition = "char(11)", nullable = false, length = 11)
    private String phoneNumber;
}
