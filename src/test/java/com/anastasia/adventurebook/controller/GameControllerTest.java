package com.anastasia.adventurebook.controller;

import com.anastasia.adventurebook.repository.BookRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void playsBookToVictory() throws Exception {
        int gameId = startGame("The Prisoner (fixed)");

        choose(gameId, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.section.number").value(20))
                .andExpect(jsonPath("$.health").value(10))
                .andExpect(jsonPath("$.consequence").value(nullValue()));

        choose(gameId, 0)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.section.number").value(30))
                .andExpect(jsonPath("$.health").value(4))
                .andExpect(jsonPath("$.consequence").value(startsWith("As you move your hands")));

        choose(gameId, 0)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.section.number").value(1000))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.section.options", hasSize(0)));

        choose(gameId, 0)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Game " + gameId + " is already finished (COMPLETED)"));
    }

    @Test
    void playerDiesAfterLosingAllHealth() throws Exception {
        int gameId = startGame("The Prisoner (fixed)");

        for (int move = 1; move <= 7; move++) {
            choose(gameId, 0).andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }

        choose(gameId, 0)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEAD"))
                .andExpect(jsonPath("$.health").value(-2))
                .andExpect(jsonPath("$.section.number").value(500))
                .andExpect(jsonPath("$.section.options", hasSize(0)));

        choose(gameId, 0).andExpect(status().isConflict());
    }

    @Test
    void returnsCurrentStateOfGame() throws Exception {
        int gameId = startGame("The Prisoner (fixed)");
        choose(gameId, 1);

        mockMvc.perform(get("/games/{id}", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.section.number").value(20))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.section.options", hasSize(1)));
    }

    @Test
    void rejectsStartOfInvalidBook() throws Exception {
        mockMvc.perform(post("/books/{id}/games", bookId("The Prisoner")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(startsWith("Book 'The Prisoner' is invalid")));
    }

    @Test
    void returns404ForUnknownGame() throws Exception {
        mockMvc.perform(get("/games/{id}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Game with id 999999 not found"));
    }

    @Test
    void rejectsNonExistentOption() throws Exception {
        int gameId = startGame("The Prisoner (fixed)");

        choose(gameId, 5)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Option 5 does not exist in section 1. Valid options: 0..1"));
    }

    @Test
    void rejectsChoiceWithoutOption() throws Exception {
        int gameId = startGame("The Prisoner (fixed)");

        mockMvc.perform(post("/games/{id}/choices", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private int startGame(String bookTitle) throws Exception {
        String body = mockMvc.perform(post("/books/{id}/games", bookId(bookTitle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.health").value(10))
                .andExpect(jsonPath("$.section.number").value(1))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.gameId");
    }

    private ResultActions choose(int gameId, int option) throws Exception {
        return mockMvc.perform(post("/games/{id}/choices", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"option\": " + option + "}"));
    }

    private Long bookId(String title) {
        return bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals(title))
                .findFirst()
                .orElseThrow()
                .getId();
    }
}