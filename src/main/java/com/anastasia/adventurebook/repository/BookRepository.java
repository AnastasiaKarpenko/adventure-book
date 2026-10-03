package com.anastasia.adventurebook.repository;

import com.anastasia.adventurebook.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByTitleAndAuthor(String title, String author);
}