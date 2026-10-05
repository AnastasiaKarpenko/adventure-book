package com.anastasia.adventurebook.service;

import com.anastasia.adventurebook.dto.BookDetailsResponse;
import com.anastasia.adventurebook.dto.BookSummaryResponse;
import com.anastasia.adventurebook.exception.NotFoundException;
import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.model.Category;
import com.anastasia.adventurebook.model.Difficulty;
import com.anastasia.adventurebook.repository.BookRepository;
import com.anastasia.adventurebook.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
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
        return BookDetailsResponse.from(findBook(id));
    }

    @Transactional
    public BookDetailsResponse addCategory(Long bookId, String categoryName) {
        Book book = findBook(bookId);
        book.addCategory(findCategory(categoryName));
        return BookDetailsResponse.from(book);
    }

    @Transactional
    public BookDetailsResponse removeCategory(Long bookId, String categoryName) {
        Book book = findBook(bookId);
        book.removeCategory(findCategory(categoryName));
        return BookDetailsResponse.from(book);
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));
    }

    private Category findCategory(String name) {
        String normalized = Category.normalizeName(name);
        return categoryRepository.findByName(normalized)
                .orElseThrow(() -> new NotFoundException("Category '" + normalized + "' not found"));
    }
}