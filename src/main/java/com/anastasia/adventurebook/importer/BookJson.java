package com.anastasia.adventurebook.importer;

import com.anastasia.adventurebook.model.ConsequenceType;
import com.anastasia.adventurebook.model.Difficulty;
import com.anastasia.adventurebook.model.SectionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookJson(
        String title,
        String author,
        Difficulty difficulty,
        List<SectionJson> sections
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SectionJson(Integer id, String text, SectionType type, List<OptionJson> options) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OptionJson(String description, Integer gotoId, ConsequenceJson consequence) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ConsequenceJson(ConsequenceType type, Integer value, String text) {
    }
}