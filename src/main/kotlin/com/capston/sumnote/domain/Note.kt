package com.capston.sumnote.domain

import com.capston.sumnote.note.dto.ChangeTitleDto
import com.capston.sumnote.util.entity.BaseEntity
import jakarta.persistence.*
import lombok.Builder
@Entity
@Table(name = "NOTE_DOCS")
class Note(
    @Id
    @GeneratedValue
    @Column(name = "note_id")
    var id: Long? = null,

    @Column(name = "title")
    var title: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @OneToMany(mappedBy = "note", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var notePages: MutableList<NotePage> = mutableListOf(),

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    var quiz: Quiz? = null,

) : BaseEntity() {

    fun updateTitle(dto: ChangeTitleDto) {
        this.title = dto.changeTitle
    }
}
