package com.anastasia.adventurebook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Locale;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    protected Category() {
    }

    public Category(String name) {
        this.name = normalizeName(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static String normalizeName(String name) {
        return name.trim().toUpperCase(Locale.ROOT);
    }
}