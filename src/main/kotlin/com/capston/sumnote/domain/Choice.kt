package com.capston.sumnote.domain

import jakarta.persistence.Embeddable
import lombok.Getter
import lombok.Setter

@Embeddable
@Getter @Setter
class Choice {

    var choice1: String
    var choice2: String
    var choice3: String
    var choice4: String

    constructor() : this("", "", "", "")

    constructor(choice1: String, choice2: String, choice3: String, choice4: String) {
        this.choice1 = choice1
        this.choice2 = choice2
        this.choice3 = choice3
        this.choice4 = choice4
    }
}