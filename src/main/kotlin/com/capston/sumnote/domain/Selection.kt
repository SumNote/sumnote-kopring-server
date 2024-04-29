package com.capston.sumnote.domain

import jakarta.persistence.*

@Entity
@Table(name = "selection")
class Selection (
    @Id
    @GeneratedValue
    @Column(name = "selection_id")
    var id : Long? = null,

    var selection : String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_page_id")
    var quizPage : QuizPage,
)