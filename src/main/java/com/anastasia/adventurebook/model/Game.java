package com.anastasia.adventurebook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.List;
import com.anastasia.adventurebook.exception.BadRequestException;
import com.anastasia.adventurebook.exception.ConflictException;

@Entity
@Table(name = "game")
public class Game {

    public static final int STARTING_HEALTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "current_section_number", nullable = false)
    private int currentSectionNumber;

    @Column(nullable = false)
    private int health;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;

    @Version
    private long version;

    protected Game() {
    }

    public Game(Book book, int startSectionNumber) {
        this.book = book;
        this.currentSectionNumber = startSectionNumber;
        this.health = STARTING_HEALTH;
        this.status = GameStatus.IN_PROGRESS;
    }

    public Long getId() { return id; }
    public Book getBook() { return book; }
    public int getCurrentSectionNumber() { return currentSectionNumber; }
    public int getHealth() { return health; }
    public GameStatus getStatus() { return status; }

    public Section getCurrentSection() {
        return book.findSection(currentSectionNumber)
                .orElseThrow(() -> new IllegalStateException(
                        "Section " + currentSectionNumber + " not found in book " + book.getId()));
    }

    public SectionOption choose(int optionIndex) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new ConflictException("Game " + id + " is already finished (" + status + ")");
        }
        List<SectionOption> options = getCurrentSection().getOptions();
        if (optionIndex < 0 || optionIndex >= options.size()) {
            throw new BadRequestException("Option " + optionIndex + " does not exist in section "
                    + currentSectionNumber + ". Valid options: 0.." + (options.size() - 1));
        }

        SectionOption chosen = options.get(optionIndex);
        currentSectionNumber = chosen.getGotoNumber();

        if (getCurrentSection().getType() == SectionType.END) {
            status = GameStatus.COMPLETED;
        }
        return chosen;
    }
}