package com.anastasia.adventurebook.service;

import com.anastasia.adventurebook.dto.BookDetailsResponse;
import com.anastasia.adventurebook.dto.BookSummaryResponse;
import com.anastasia.adventurebook.exception.NotFoundException;
import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Difficulty;
import com.anastasia.adventurebook.repository.BookRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.anastasia.adventurebook.repository.BookSpecifications.authorContains;
import static com.anastasia.adventurebook.repository.BookSpecifications.hasCategory;
import static com.anastasia.adventurebook.repository.BookSpecifications.hasDifficulty;
import static com.anastasia.adventurebook.repository.BookSpecifications.titleContains;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<BookSummaryResponse> search(String title, String author, String category,
                                            Difficulty difficulty) {
        List<Specification<Book>> filters = new ArrayList<>();
        if (StringUtils.hasText(title)) {
            filters.add(titleContains(title));
        }
        if (StringUtils.hasText(author)) {
            filters.add(authorContains(author));
        }
        if (StringUtils.hasText(category)) {
            filters.add(hasCategory(category));
        }
        if (difficulty != null) {
            filters.add(hasDifficulty(difficulty));
        }

        return bookRepository.findAll(Specification.allOf(filters), Sort.by("title")).stream()
                .map(BookSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookDetailsResponse getDetails(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));
        return BookDetailsResponse.from(book);
    }
}