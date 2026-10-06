package com.anastasia.adventurebook.model;

import com.anastasia.adventurebook.exception.BadRequestException;
import com.anastasia.adventurebook.exception.ConflictException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.anastasia.adventurebook.model.ConsequenceType.GAIN_HEALTH;
import static com.anastasia.adventurebook.model.ConsequenceType.LOSE_HEALTH;
import static com.anastasia.adventurebook.model.SectionType.BEGIN;
import static com.anastasia.adventurebook.model.SectionType.END;
import static com.anastasia.adventurebook.model.SectionType.NODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

    @Test
    void startsAtBeginningWithFullHealth() {
        Game game = new Game(book(
                section(1, BEGIN, option(2)),
                section(2, END)), 1);

        assertThat(game.getCurrentSectionNumber()).isEqualTo(1);
        assertThat(game.getHealth()).isEqualTo(10);
        assertThat(game.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
    }

    @Test
    void movesToSectionOfChosenOption() {
        Game game = new Game(book(
                section(1, BEGIN, option(500), option(20)),
                section(20, NODE, option(1000)),
                section(500, NODE, option(1)),
                section(1000, END)), 1);

        game.choose(1);

        assertThat(game.getCurrentSectionNumber()).isEqualTo(20);
        assertThat(game.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
    }

    @Test
    void reachingEndingCompletesGame() {
        Game game = new Game(book(
                section(1, BEGIN, option(2)),
                section(2, END)), 1);

        game.choose(0);

        assertThat(game.getCurrentSectionNumber()).isEqualTo(2);
        assertThat(game.getStatus()).isEqualTo(GameStatus.COMPLETED);
    }

    @Test
    void loseHealthReducesHealth() {
        Game game = new Game(book(
                section(1, BEGIN, option(2, LOSE_HEALTH, 6)),
                section(2, NODE, option(3)),
                section(3, END)), 1);

        game.choose(0);

        assertThat(game.getHealth()).isEqualTo(4);
        assertThat(game.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
    }

    @Test
    void gainHealthHasNoUpperLimit() {
        Game game = new Game(book(
                section(1, BEGIN, option(2, GAIN_HEALTH, 3)),
                section(2, NODE, option(3)),
                section(3, END)), 1);

        game.choose(0);

        assertThat(game.getHealth()).isEqualTo(13);
    }

    @Test
    void healthExactlyZeroKillsPlayer() {
        Game game = new Game(book(
                section(1, BEGIN, option(2, LOSE_HEALTH, 10)),
                section(2, NODE, option(3)),
                section(3, END)), 1);

        game.choose(0);

        assertThat(game.getHealth()).isZero();
        assertThat(game.getStatus()).isEqualTo(GameStatus.DEAD);
    }

    @Test
    void deadPlayerStaysInSectionWhereChoiceWasMade() {
        Game game = new Game(book(
                section(1, BEGIN, option(2, LOSE_HEALTH, 12)),
                section(2, NODE, option(3)),
                section(3, END)), 1);

        game.choose(0);

        assertThat(game.getHealth()).isEqualTo(-2);
        assertThat(game.getStatus()).isEqualTo(GameStatus.DEAD);
        assertThat(game.getCurrentSectionNumber()).isEqualTo(1);
    }

    @Test
    void deathTakesPrecedenceOverEnding() {
        Game game = new Game(book(
                section(1, BEGIN, option(2, LOSE_HEALTH, 10)),
                section(2, END)), 1);

        game.choose(0);

        assertThat(game.getStatus()).isEqualTo(GameStatus.DEAD);
    }

    @Test
    void cannotChooseAfterGameIsCompleted() {
        Game game = new Game(book(
                section(1, BEGIN, option(2)),
                section(2, END)), 1);
        game.choose(0);

        assertThatThrownBy(() -> game.choose(0))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("COMPLETED");
    }

    @Test
    void cannotChooseAfterDeath() {
        Game game = new Game(book(
                section(1, BEGIN, option(2, LOSE_HEALTH, 10)),
                section(2, END)), 1);
        game.choose(0);

        assertThatThrownBy(() -> game.choose(0))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("DEAD");
    }

    @Test
    void rejectsNonExistentOption() {
        Game game = new Game(book(
                section(1, BEGIN, option(2), option(2)),
                section(2, END)), 1);

        assertThatThrownBy(() -> game.choose(5))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Option 5 does not exist in section 1. Valid options: 0..1");
        assertThat(game.getCurrentSectionNumber()).isEqualTo(1);
    }

    private static Book book(Section... sections) {
        Book book = new Book("Test book", "Test author", Difficulty.EASY);
        for (Section section : sections) {
            book.addSection(section);
        }
        return book;
    }

    private static Section section(int number, SectionType type, SectionOption... options) {
        return new Section(number, "Section " + number, type, List.of(options));
    }

    private static SectionOption option(int gotoNumber) {
        return new SectionOption("Go to " + gotoNumber, gotoNumber, null);
    }

    private static SectionOption option(int gotoNumber, ConsequenceType type, int value) {
        return new SectionOption("Go to " + gotoNumber, gotoNumber,
                new Consequence(type, value, type + " " + value));
    }
}