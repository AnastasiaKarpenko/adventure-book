package com.anastasia.adventurebook.service;

import com.anastasia.adventurebook.dto.CategoryResponse;
import com.anastasia.adventurebook.exception.ConflictException;
import com.anastasia.adventurebook.model.Category;
import com.anastasia.adventurebook.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll(Sort.by("name")).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse create(String name) {
        String normalized = Category.normalizeName(name);
        if (categoryRepository.existsByName(normalized)) {
            throw new ConflictException("Category '" + normalized + "' already exists");
        }
        Category saved = categoryRepository.save(new Category(normalized));
        return CategoryResponse.from(saved);
    }
}