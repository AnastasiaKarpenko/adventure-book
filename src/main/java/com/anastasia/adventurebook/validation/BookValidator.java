package com.anastasia.adventurebook.validation;

import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Section;
import com.anastasia.adventurebook.model.SectionOption;
import com.anastasia.adventurebook.model.SectionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookValidator {

    public List<String> validate(Book book) {
        List<Section> sections = book.getSections();
        List<String> errors = new ArrayList<>();

        checkBeginning(sections, errors);
        checkEnding(sections, errors);
        checkSections(sections, errors);

        return errors;
    }

    private void checkBeginning(List<Section> sections, List<String> errors) {
        long beginnings = countOfType(sections, SectionType.BEGIN);
        if (beginnings == 0) {
            errors.add("Book has no beginning");
        } else if (beginnings > 1) {
            errors.add("Book has " + beginnings + " beginnings");
        }
    }

    private void checkEnding(List<Section> sections, List<String> errors) {
        if (countOfType(sections, SectionType.END) == 0) {
            errors.add("Book has no ending");
        }
    }

    private void checkSections(List<Section> sections, List<String> errors) {
        Set<Integer> existingNumbers = sections.stream()
                .map(Section::getNumber)
                .collect(Collectors.toSet());

        for (Section section : sections) {
            List<SectionOption> options = section.getOptions();

            if (section.getType() != SectionType.END && options.isEmpty()) {
                errors.add("Section " + section.getNumber()
                        + " is not an ending but has no options");
            }

            for (int i = 0; i < options.size(); i++) {
                int target = options.get(i).getGotoNumber();
                if (!existingNumbers.contains(target)) {
                    errors.add("Section " + section.getNumber() + ", option " + i
                            + " points to non-existent section " + target);
                }
            }
        }
    }

    private static long countOfType(List<Section> sections, SectionType type) {
        return sections.stream()
                .filter(section -> section.getType() == type)
                .count();
    }
}