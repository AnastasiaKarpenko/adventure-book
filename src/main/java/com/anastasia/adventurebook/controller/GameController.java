package com.anastasia.adventurebook.controller;

import com.anastasia.adventurebook.dto.GameResponse;
import com.anastasia.adventurebook.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/books/{bookId}/games")
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse startGame(@PathVariable Long bookId) {
        return gameService.start(bookId);
    }

    @GetMapping("/games/{gameId}")
    public GameResponse getGame(@PathVariable Long gameId) {
        return gameService.get(gameId);
    }
}