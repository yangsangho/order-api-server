package io.yangbob.order.domain.common.entity;

import jakarta.persistence.*;
import lombok.NonNull;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@MappedSuperclass
public abstract class PrimaryKeyEntity<ID extends Serializable> extends BaseEntity implements Persistable<ID> {
    public PrimaryKeyEntity(ID id) {
        this.id = id;
    }

    @EmbeddedId
    private ID id;

    @Override
    @NonNull
    public ID getId() {
        return id;
    }

    @Transient
    private boolean _isNew = true;

    @Override
    public boolean isNew() {
        return _isNew;
    }

    @PostLoad
    protected void postLoad() {
        load();
    }

    @PostPersist
    protected void postPersist() {
        load();
    }

    private void load() {
        _isNew = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof HibernateProxy) && !(this.getClass().equals(obj.getClass()))) return false;

        return id.equals(getIdentifier(obj));
    }

    private Object getIdentifier(Object obj) {
        if (obj instanceof HibernateProxy) {
            return ((HibernateProxy) obj).getHibernateLazyInitializer().getIdentifier();
        } else {
            return ((PrimaryKeyEntity<?>) obj).getId();
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
