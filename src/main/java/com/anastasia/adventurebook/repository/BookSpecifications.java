package com.anastasia.adventurebook.repository;

import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Category;
import com.anastasia.adventurebook.model.Difficulty;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class BookSpecifications {

    private BookSpecifications() {
    }

    public static Specification<Book> titleContains(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), likePattern(title));
    }

    public static Specification<Book> authorContains(String author) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("author")), likePattern(author));
    }

    public static Specification<Book> hasDifficulty(Difficulty difficulty) {
        return (root, query, cb) -> cb.equal(root.get("difficulty"), difficulty);
    }

    public static Specification<Book> hasCategory(String category) {
        return (root, query, cb) -> {
            Join<Book, Category> categories = root.join("categories");
            return cb.equal(categories.get("name"), Category.normalizeName(category));
        };
    }

    private static String likePattern(String value) {
        return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
    }
}