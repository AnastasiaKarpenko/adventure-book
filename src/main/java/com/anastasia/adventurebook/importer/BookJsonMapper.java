package com.anastasia.adventurebook.importer;

import com.anastasia.adventurebook.importer.BookJson.ConsequenceJson;
import com.anastasia.adventurebook.importer.BookJson.OptionJson;
import com.anastasia.adventurebook.importer.BookJson.SectionJson;
import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Consequence;
import com.anastasia.adventurebook.model.Section;
import com.anastasia.adventurebook.model.SectionOption;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BookJsonMapper {

    public Book toEntity(BookJson json) {
        String title = requireText(json.title(), "title");
        String author = requireText(json.author(), "author");
        if (json.difficulty() == null) {
            throw new BookImportException("Missing difficulty");
        }

        Book book = new Book(title, author, json.difficulty());
        Set<Integer> seenNumbers = new HashSet<>();
        for (SectionJson sectionJson : nullToEmpty(json.sections())) {
            book.addSection(toSection(sectionJson, seenNumbers));
        }
        return book;
    }

    private Section toSection(SectionJson json, Set<Integer> seenNumbers) {
        if (json.id() == null) {
            throw new BookImportException("Section without id");
        }
        if (!seenNumbers.add(json.id())) {
            throw new BookImportException("Duplicate section id " + json.id());
        }
        if (json.type() == null) {
            throw new BookImportException("Section " + json.id() + " has no type");
        }
        String text = requireText(json.text(), "text of section " + json.id());

        List<SectionOption> options = nullToEmpty(json.options()).stream()
                .map(option -> toOption(option, json.id()))
                .toList();
        return new Section(json.id(), text, json.type(), options);
    }

    private SectionOption toOption(OptionJson json, int sectionNumber) {
        if (json.gotoId() == null) {
            throw new BookImportException("Option in section " + sectionNumber + " has no gotoId");
        }
        String description = requireText(json.description(),
                "description of option in section " + sectionNumber);
        return new SectionOption(description, json.gotoId(), toConsequence(json.consequence(), sectionNumber));
    }

    private Consequence toConsequence(ConsequenceJson json, int sectionNumber) {
        if (json == null) {
            return null;
        }
        if (json.type() == null || json.value() == null) {
            throw new BookImportException("Incomplete consequence in section " + sectionNumber);
        }
        return new Consequence(json.type(), json.value(), json.text());
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new BookImportException("Missing " + field);
        }
        return value.trim();
    }

    private static <T> List<T> nullToEmpty(List<T> list) {
        return list == null ? List.of() : list;
    }
}