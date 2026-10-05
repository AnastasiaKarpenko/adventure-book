package com.anastasia.adventurebook.dto;

import com.anastasia.adventurebook.model.Book;
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
        return new BookSummaryResponse(book.getId(), book.getTitle(), book.getAuthor(),
                book.getDifficulty(), book.getCategoryNames(), book.isValid());
    }
}