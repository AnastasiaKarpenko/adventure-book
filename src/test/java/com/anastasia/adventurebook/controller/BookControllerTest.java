package com.anastasia.adventurebook.controller;

import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Category;
import com.anastasia.adventurebook.repository.BookRepository;
import com.anastasia.adventurebook.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void listsAllImportedBooksSortedByTitle() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].title").value("Pirates of the Jade Sea"))
                .andExpect(jsonPath("$[1].title").value("The Crystal Caverns"))
                .andExpect(jsonPath("$[2].title").value("The Prisoner"))
                .andExpect(jsonPath("$[3].title").value("The Prisoner (fixed)"));
    }

    @Test
    void filtersByAuthorIgnoringCase() throws Exception {
        mockMvc.perform(get("/books").param("author", "DANIEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void filtersByPartOfTitle() throws Exception {
        mockMvc.perform(get("/books").param("title", "cave"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("The Crystal Caverns"));
    }

    @Test
    void filtersByDifficulty() throws Exception {
        mockMvc.perform(get("/books").param("difficulty", "MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Pirates of the Jade Sea"));
    }

    @Test
    void combinesFilters() throws Exception {
        mockMvc.perform(get("/books").param("author", "daniel").param("title", "fixed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("The Prisoner (fixed)"))
                .andExpect(jsonPath("$[0].valid").value(true));
    }

    @Test
    @Transactional
    void filtersByCategory() throws Exception {
        Book caverns = bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals("The Crystal Caverns"))
                .findFirst()
                .orElseThrow();
        Category adventure = categoryRepository.findByName("ADVENTURE").orElseThrow();
        caverns.addCategory(adventure);

        mockMvc.perform(get("/books").param("category", "adventure"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("The Crystal Caverns"))
                .andExpect(jsonPath("$[0].categories[0]").value("ADVENTURE"));
    }

    @Test
    void rejectsUnknownDifficulty() throws Exception {
        mockMvc.perform(get("/books").param("difficulty", "VERY_HARD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Invalid value 'VERY_HARD' for parameter 'difficulty'. Allowed: EASY, MEDIUM, HARD"));
    }
}