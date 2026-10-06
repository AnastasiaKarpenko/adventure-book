package com.anastasia.adventurebook.service;

import com.anastasia.adventurebook.dto.GameResponse;
import com.anastasia.adventurebook.exception.ConflictException;
import com.anastasia.adventurebook.exception.NotFoundException;
import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Consequence;
import com.anastasia.adventurebook.model.Game;
import com.anastasia.adventurebook.model.Section;
import com.anastasia.adventurebook.model.SectionOption;
import com.anastasia.adventurebook.repository.BookRepository;
import com.anastasia.adventurebook.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final BookRepository bookRepository;

    public GameService(GameRepository gameRepository, BookRepository bookRepository) {
        this.gameRepository = gameRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public GameResponse start(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book with id " + bookId + " not found"));
        if (!book.isValid()) {
            throw new ConflictException("Book '" + book.getTitle() + "' is invalid and cannot be played: "
                    + String.join("; ", book.getValidationErrors()));
        }
        Section beginning = book.findBeginning()
                .orElseThrow(() -> new IllegalStateException("Valid book " + bookId + " has no beginning"));

        Game game = gameRepository.save(new Game(book, beginning.getNumber()));
        return GameResponse.from(game, null);
    }

    @Transactional(readOnly = true)
    public GameResponse get(Long gameId) {
        return GameResponse.from(findGame(gameId), null);
    }

    private Game findGame(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Game with id " + id + " not found"));
    }

    @Transactional
    public GameResponse choose(Long gameId, int optionIndex) {
        Game game = findGame(gameId);
        SectionOption chosen = game.choose(optionIndex);
        String consequenceText = chosen.getConsequence()
                .map(Consequence::text)
                .orElse(null);
        return GameResponse.from(game, consequenceText);
    }
}