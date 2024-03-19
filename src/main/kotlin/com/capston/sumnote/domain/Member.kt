package com.capston.sumnote.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AccessLevel
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor

@Entity
@Table(name = "MEMBERS")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    var id: Long? = null

    @Column(name = "member_email", nullable = false, unique = true)
    var email: String? = null

    @Column(name = "member_name")
    var name: String? = null

}