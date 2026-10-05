package com.anastasia.adventurebook.controller;

import com.anastasia.adventurebook.dto.BookDetailsResponse;
import com.anastasia.adventurebook.dto.BookSummaryResponse;
import com.anastasia.adventurebook.model.Difficulty;
import com.anastasia.adventurebook.service.BookService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookSummaryResponse> listBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Difficulty difficulty) {
        return bookService.search(title, author, category, difficulty);
    }

    @GetMapping("/{id}")
    public BookDetailsResponse getBook(@PathVariable Long id) {
        return bookService.getDetails(id);
    }

    @PutMapping("/{id}/categories/{name}")
    public BookDetailsResponse addCategory(@PathVariable Long id, @PathVariable String name) {
        return bookService.addCategory(id, name);
    }

    @DeleteMapping("/{id}/categories/{name}")
    public BookDetailsResponse removeCategory(@PathVariable Long id, @PathVariable String name) {
        return bookService.removeCategory(id, name);
    }
}