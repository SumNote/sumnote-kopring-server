package com.capston.sumnote.domain

import com.capston.sumnote.util.entity.BaseEntity
import jakarta.persistence.*
import lombok.Getter
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime


@Entity
@Table(name = "quiz_doc")
class Quiz (
    @Id
    @GeneratedValue
    @Column(name = "quiz_id")
    var id: Long? = null,

    var title : String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @OneToMany(mappedBy = "quiz", cascade = [CascadeType.ALL])
    var quizPages : MutableList<QuizPage> = mutableListOf(),

    @OneToOne(mappedBy = "quiz", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var note: Note? = null,

) : BaseEntity() {
    //==연관관계편의 메소드==//
    fun addQuizPage(quizPage: QuizPage) {
        quizPages.add(quizPage)
        quizPage.quiz = this
    }
}