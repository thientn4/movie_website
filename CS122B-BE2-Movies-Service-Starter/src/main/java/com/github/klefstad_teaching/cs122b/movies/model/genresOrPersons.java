package com.github.klefstad_teaching.cs122b.movies.model;

public class genresOrPersons {
    public Long getId() {
        return id;
    }

    public genresOrPersons setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public genresOrPersons setName(String name) {
        this.name = name;
        return this;
    }

    private Long id;
    private String name;
}
