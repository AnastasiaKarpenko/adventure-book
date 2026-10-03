package com.anastasia.adventurebook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Optional;

@Entity
@Table(name = "section_option")
public class SectionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "goto_number", nullable = false)
    private int gotoNumber;

    @Embedded
    private Consequence consequence;

    protected SectionOption() {
    }

    public SectionOption(String description, int gotoNumber, Consequence consequence) {
        this.description = description;
        this.gotoNumber = gotoNumber;
        this.consequence = consequence;
    }

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public int getGotoNumber() { return gotoNumber; }

    public Optional<Consequence> getConsequence() {
        return Optional.ofNullable(consequence);
    }
}