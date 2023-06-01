package io.yangbob.order.domain.common.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@EqualsAndHashCode
public abstract class PrimaryKey implements Serializable {
    public PrimaryKey() {
        id = UlidCreator.getMonotonicUlid().toUuid();
    }

    public PrimaryKey(String uuid) {
        this.id = UUID.fromString(uuid);
    }

    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    protected UUID id;

    @Override
    public String toString() {
        return id.toString();
    }
}
