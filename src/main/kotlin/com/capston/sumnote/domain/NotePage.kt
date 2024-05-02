package com.capston.sumnote.domain

import com.capston.sumnote.util.entity.BaseEntity
import jakarta.persistence.*
import lombok.Builder

@Entity
@Table(name = "NOTE_PAGES")
class NotePage(
    @Id
    @GeneratedValue
    @Column(name = "note_page_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    var note: Note? = null,

    @Column(name = "note_page_title")
    var title: String? = null,

    @Column(name = "note_page_content", columnDefinition = "TEXT")
    var content: String? = null,

) : BaseEntity()
