package com.infobank.multiagentplatform.commons.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;


@Getter
@NoArgsConstructor(access = PROTECTED)
public class TimeBaseEntity {

    @Column("is_deleted")
    private boolean isDeleted;

    @CreatedDate
    @Column("created_date_time")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column("last_modified_date_time")
    private LocalDateTime lastModifiedDateTime;

    protected TimeBaseEntity(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void remove() {
        this.isDeleted = true;
    }
}
