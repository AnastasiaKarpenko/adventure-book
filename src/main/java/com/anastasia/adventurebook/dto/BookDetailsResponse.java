package com.anastasia.adventurebook.dto;

import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Difficulty;

import java.util.List;

public record BookDetailsResponse(
        Long id,
        String title,
        String author,
        Difficulty difficulty,
        List<String> categories,
        boolean valid,
        List<String> validationErrors
) {

    public static BookDetailsResponse from(Book book) {
        return new BookDetailsResponse(book.getId(), book.getTitle(), book.getAuthor(),
                book.getDifficulty(), book.getCategoryNames(), book.isValid(),
                List.copyOf(book.getValidationErrors()));
    }
}