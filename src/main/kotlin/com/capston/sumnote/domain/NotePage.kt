package com.capston.sumnote.domain

import com.capston.sumnote.util.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "NOTE_PAGES")
class NotePage () : BaseEntity()