package com.anastasia.adventurebook.validation;

import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Difficulty;
import com.anastasia.adventurebook.model.Section;
import com.anastasia.adventurebook.model.SectionOption;
import com.anastasia.adventurebook.model.SectionType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.anastasia.adventurebook.model.SectionType.BEGIN;
import static com.anastasia.adventurebook.model.SectionType.END;
import static com.anastasia.adventurebook.model.SectionType.NODE;
import static org.assertj.core.api.Assertions.assertThat;

class BookValidatorTest {

    private final BookValidator validator = new BookValidator();

    @Test
    void validBookHasNoErrors() {
        Book book = book(
                section(1, BEGIN, 2),
                section(2, END));

        List<String> errors = validator.validate(book);

        assertThat(errors).isEmpty();
    }

    @Test
    void bookWithoutSectionsHasNoBeginningAndNoEnding() {
        Book book = book();

        assertThat(validator.validate(book))
                .containsExactly("Book has no beginning", "Book has no ending");
    }

    @Test
    void reportsMissingBeginning() {
        Book book = book(
                section(1, NODE, 2),
                section(2, END));

        assertThat(validator.validate(book)).containsExactly("Book has no beginning");
    }

    @Test
    void reportsMoreThanOneBeginning() {
        Book book = book(
                section(1, BEGIN, 3),
                section(2, BEGIN, 3),
                section(3, END));

        assertThat(validator.validate(book)).containsExactly("Book has 2 beginnings");
    }

    @Test
    void reportsMissingEnding() {
        Book book = book(
                section(1, BEGIN, 2),
                section(2, NODE, 1));

        assertThat(validator.validate(book)).containsExactly("Book has no ending");
    }

    @Test
    void reportsOptionPointingToNonExistentSection() {
        Book book = book(
                section(1, BEGIN, 2, 999),
                section(2, END));

        assertThat(validator.validate(book))
                .containsExactly("Section 1, option 1 points to non-existent section 999");
    }

    @Test
    void reportsNonEndingSectionWithoutOptions() {
        Book book = book(
                section(1, BEGIN, 2),
                section(2, END),
                section(666, NODE));

        assertThat(validator.validate(book))
                .containsExactly("Section 666 is not an ending but has no options");
    }

    @Test
    void endingSectionWithoutOptionsIsValid() {
        Book book = book(
                section(1, BEGIN, 2),
                section(2, END));

        assertThat(validator.validate(book)).isEmpty();
    }

    @Test
    void collectsAllErrorsInSectionOrder() {
        Book book = book(
                section(1, BEGIN, 666, 999),
                section(666, NODE),
                section(1400, END));

        assertThat(validator.validate(book)).containsExactly(
                "Section 1, option 1 points to non-existent section 999",
                "Section 666 is not an ending but has no options");
    }

    private static Book book(Section... sections) {
        Book book = new Book("Test book", "Test author", Difficulty.EASY);
        for (Section section : sections) {
            book.addSection(section);
        }
        return book;
    }

    private static Section section(int number, SectionType type, int... gotoNumbers) {
        List<SectionOption> options = Arrays.stream(gotoNumbers)
                .mapToObj(target -> new SectionOption("Go to " + target, target, null))
                .toList();
        return new Section(number, "Section " + number, type, options);
    }
}