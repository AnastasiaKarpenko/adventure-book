package com.anastasia.adventurebook.importer;

import com.anastasia.adventurebook.importer.BookJson.ConsequenceJson;
import com.anastasia.adventurebook.importer.BookJson.OptionJson;
import com.anastasia.adventurebook.importer.BookJson.SectionJson;
import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.ConsequenceType;
import com.anastasia.adventurebook.model.Difficulty;
import com.anastasia.adventurebook.model.Section;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static com.anastasia.adventurebook.model.SectionType.BEGIN;
import static com.anastasia.adventurebook.model.SectionType.END;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookJsonMapperTest {

    private final BookJsonMapper mapper = new BookJsonMapper();

    @Test
    void mapsBookWithSectionsOptionsAndConsequences() {
        BookJson json = new BookJson(" The Prisoner ", "Daniel El Fuego", Difficulty.HARD, List.of(
                new SectionJson(1, "Start", BEGIN, List.of(
                        new OptionJson("Look under the bed", 2,
                                new ConsequenceJson(ConsequenceType.LOSE_HEALTH, 6, "Rusty nail")))),
                new SectionJson(2, "Free", END, null)));

        Book book = mapper.toEntity(json);

        assertThat(book.getTitle()).isEqualTo("The Prisoner");
        assertThat(book.getSections()).hasSize(2);
        Section start = book.getSections().get(0);
        assertThat(start.getOptions()).hasSize(1);
        assertThat(start.getOptions().get(0).getGotoNumber()).isEqualTo(2);
        assertThat(start.getOptions().get(0).getConsequence()).hasValueSatisfying(consequence -> {
            assertThat(consequence.type()).isEqualTo(ConsequenceType.LOSE_HEALTH);
            assertThat(consequence.value()).isEqualTo(6);
        });
        assertThat(book.getSections().get(1).getOptions()).isEmpty();
    }

    @Test
    void rejectsBookWithoutAuthor() {
        BookJson json = new BookJson("Title", "  ", Difficulty.EASY, List.of());

        assertThatThrownBy(() -> mapper.toEntity(json))
                .isInstanceOf(BookImportException.class)
                .hasMessage("Missing author");
    }

    @Test
    void rejectsBookWithoutDifficulty() {
        BookJson json = new BookJson("Title", "Author", null, List.of());

        assertThatThrownBy(() -> mapper.toEntity(json))
                .isInstanceOf(BookImportException.class)
                .hasMessage("Missing difficulty");
    }

    @Test
    void rejectsDuplicateSectionIds() {
        BookJson json = new BookJson("Title", "Author", Difficulty.EASY, List.of(
                new SectionJson(1, "A", BEGIN, List.of(new OptionJson("Go", 2, null))),
                new SectionJson(1, "B", END, null)));

        assertThatThrownBy(() -> mapper.toEntity(json))
                .isInstanceOf(BookImportException.class)
                .hasMessage("Duplicate section id 1");
    }

    @Test
    void rejectsOptionWithoutTarget() {
        BookJson json = new BookJson("Title", "Author", Difficulty.EASY, List.of(
                new SectionJson(1, "A", BEGIN, List.of(new OptionJson("Go", null, null)))));

        assertThatThrownBy(() -> mapper.toEntity(json))
                .isInstanceOf(BookImportException.class)
                .hasMessage("Option in section 1 has no gotoId");
    }

    @Test
    void jsonWithStringIdsAndUnknownFieldsIsParsed() {
        String file = """
                {
                  "title": "T", "author": "A", "difficulty": "EASY", "type": "",
                  "sections": [
                    { "id": "500", "text": "S", "type": "END" }
                  ]
                }
                """;

        BookJson json = JsonMapper.builder().build().readValue(file, BookJson.class);

        assertThat(json.sections().get(0).id()).isEqualTo(500);
    }

    @Test
    void emptyFileIsNotValidJson() {
        assertThatThrownBy(() -> JsonMapper.builder().build().readValue("", BookJson.class))
                .isInstanceOf(JacksonException.class);
    }
}