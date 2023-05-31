package io.yangbob.order.domain.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Setter(AccessLevel.PROTECTED)
@Getter
public abstract class BaseEntity {
    @Column(updatable = false, columnDefinition = "timestamp(3)")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "timestamp(3)")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
