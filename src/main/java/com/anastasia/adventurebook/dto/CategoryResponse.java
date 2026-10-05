package com.anastasia.adventurebook.dto;

import com.anastasia.adventurebook.model.Category;

public record CategoryResponse(String name) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getName());
    }
}