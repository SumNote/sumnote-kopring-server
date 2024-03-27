package com.capston.sumnote.domain

import jakarta.persistence.*
import lombok.Getter

@Entity
@Table(name = "quiz_page")
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
)