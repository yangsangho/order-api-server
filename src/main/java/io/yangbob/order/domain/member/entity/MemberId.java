package io.yangbob.order.domain.member.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class MemberId implements Serializable {
    @Column(name = "member_id", columnDefinition = "uuid", nullable = false)
    private final UUID id = UlidCreator.getMonotonicUlid().toUuid();
}
