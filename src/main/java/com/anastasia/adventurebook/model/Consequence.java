package com.anastasia.adventurebook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record Consequence(
        @Enumerated(EnumType.STRING)
        @Column(name = "consequence_type")
        ConsequenceType type,

        @Column(name = "consequence_value")
        Integer value,

        @Column(name = "consequence_text", length = 1000)
        String text
) {
}