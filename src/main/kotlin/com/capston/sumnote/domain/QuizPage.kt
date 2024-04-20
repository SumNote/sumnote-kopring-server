package com.capston.sumnote.domain

import com.capston.sumnote.util.entity.BaseEntity
import jakarta.persistence.*
import lombok.Getter

@Entity
@Table(name = "QUIZ_PAGES")
class QuizPage (

    @Id
    @GeneratedValue
    @Column(name = "quiz_page_id")
    var id : Long,

    var question : String,

    var answer : String,

    var commentary : String,

    @Embedded
    var choice: Choice,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    var quiz : Quiz

) : BaseEntity()