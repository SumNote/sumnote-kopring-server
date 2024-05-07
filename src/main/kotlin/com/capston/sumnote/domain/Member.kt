package com.capston.sumnote.domain

import com.capston.sumnote.util.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "MEMBERS")
class Member(
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    var id: Long? = null,

    @Column(name = "member_email", nullable = false, unique = true)
    var email: String? = null,

    @Column(name = "member_name")
    var name: String? = null,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var notes: MutableList<Note> = mutableListOf(),

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var quiz: MutableList<Quiz> = mutableListOf()

) : BaseEntity()