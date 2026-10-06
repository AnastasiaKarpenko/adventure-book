package com.anastasia.adventurebook.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChoiceRequest(
        @NotNull
        @Min(0)
        Integer option
) {
}