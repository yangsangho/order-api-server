package io.yangbob.order.domain.member.entity;

import io.yangbob.order.domain.common.entity.PrimaryKey;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@AttributeOverride(
        name = "id",
        column = @Column(name = "member_id")
)
public class MemberId extends PrimaryKey {
    public MemberId() {
        super();
    }

    public MemberId(String uuid) {
        super(uuid);
    }
}
