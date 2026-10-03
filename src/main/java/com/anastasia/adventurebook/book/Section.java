package com.anastasia.adventurebook.book;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "section",
        uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "number"}))
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false, length = 4000)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SectionType type;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "section_id", nullable = false)
    @OrderColumn(name = "position")
    private List<SectionOption> options = new ArrayList<>();

    protected Section() {
    }

    public Section(int number, String text, SectionType type, List<SectionOption> options) {
        this.number = number;
        this.text = text;
        this.type = type;
        this.options.addAll(options);
    }

    public Long getId() { return id; }
    public int getNumber() { return number; }
    public String getText() { return text; }
    public SectionType getType() { return type; }

    public List<SectionOption> getOptions() {
        return Collections.unmodifiableList(options);
    }
}