package com.capston.sumnote.util.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    @Column(name = "last_modified_at")
    lateinit var lastModifiedAt: LocalDateTime
}
