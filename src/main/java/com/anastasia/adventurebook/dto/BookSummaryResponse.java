package com.anastasia.adventurebook.dto;

import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Category;
import com.anastasia.adventurebook.model.Difficulty;

import java.util.List;

public record BookSummaryResponse(
        Long id,
        String title,
        String author,
        Difficulty difficulty,
        List<String> categories,
        boolean valid
) {

    public static BookSummaryResponse from(Book book) {
        List<String> categories = book.getCategories().stream()
                .map(Category::getName)
                .sorted()
                .toList();
        return new BookSummaryResponse(book.getId(), book.getTitle(), book.getAuthor(),
                book.getDifficulty(), categories, book.isValid());
    }
}