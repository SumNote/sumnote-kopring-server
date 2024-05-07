package com.capston.sumnote.domain

import jakarta.persistence.*

@Entity
@Table(name = "quiz_page")
class QuizPage (

    @Id
    @GeneratedValue
    @Column(name = "quiz_page_id")
    var id : Long? = null,

    var question : String,

    var answer : String,

    var commentary : String,

    @OneToMany(mappedBy = "quizPage", cascade = [CascadeType.ALL])
    var selections: MutableList<Selection> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    var quiz : Quiz
) {
    //==연관관계편의 메소드==//
    fun addSelection(selection: Selection) {
        selections.add(selection)
        selection.quizPage = this
    }
}