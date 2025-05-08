package com.infobank.multiagentplatform.commons.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TimeBaseEntity {

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private LocalDateTime lastModifiedDateTime;

    protected TimeBaseEntity(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void remove() {
        this.isDeleted = true;
    }
}
