package com.anastasia.adventurebook.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private boolean valid;

    @ElementCollection
    @CollectionTable(name = "book_validation_error",
            joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "message", nullable = false)
    private List<String> validationErrors = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "book_id", nullable = false)
    private List<Section> sections = new ArrayList<>();

    protected Book() {
    }

    public Book(String title, String author, Difficulty difficulty) {
        this.title = title;
        this.author = author;
        this.difficulty = difficulty;
    }

    public void applyValidation(List<String> errors) {
        this.validationErrors.clear();
        this.validationErrors.addAll(errors);
        this.valid = errors.isEmpty();
    }

    public boolean addCategory(Category category) {
        return categories.add(category);
    }

    public boolean removeCategory(Category category) {
        return categories.remove(category);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Difficulty getDifficulty() { return difficulty; }
    public boolean isValid() { return valid; }

    public List<String> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    public Set<Category> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<String> getCategoryNames() {
        return categories.stream()
                .map(Category::getName)
                .sorted()
                .toList();
    }
    public Optional<Section> findSection(int number) {
        return sections.stream()
                .filter(section -> section.getNumber() == number)
                .findFirst();
    }

    public Optional<Section> findBeginning() {
        return sections.stream()
                .filter(section -> section.getType() == SectionType.BEGIN)
                .findFirst();
    }
}