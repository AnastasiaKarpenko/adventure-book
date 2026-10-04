package com.anastasia.adventurebook.repository;

import com.anastasia.adventurebook.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    boolean existsByTitleAndAuthor(String title, String author);
}