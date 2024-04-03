package com.capston.sumnote.domain

import com.capston.sumnote.util.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "NOTE_PAGES")
class NotePage (

    @Id
    @GeneratedValue
    @Column(name = "note_page_id")
    var id: Long? = null,

    // TODO : 노트 문서와 연결

    @Column(name = "note_page_title")
    var title: String? = null,

    @Column(name = "note_page_contnet", columnDefinition = "TEXT")
    var content: String? = null,

    @Column(name = "is_quiz_exists")
    var isQuizExists: Boolean? = false


) : BaseEntity()