package com.anastasia.adventurebook.importer;

import com.anastasia.adventurebook.model.Category;
import com.anastasia.adventurebook.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CategorySeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CategorySeeder.class);

    private static final List<String> DEFAULT_CATEGORIES =
            List.of("FICTION", "SCIENCE", "HORROR", "ADVENTURE");

    private final CategoryRepository categoryRepository;

    public CategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int created = 0;
        for (String name : DEFAULT_CATEGORIES) {
            if (!categoryRepository.existsByName(name)) {
                categoryRepository.save(new Category(name));
                created++;
            }
        }
        log.info("Default categories: {} created, {} already present",
                created, DEFAULT_CATEGORIES.size() - created);
    }
}