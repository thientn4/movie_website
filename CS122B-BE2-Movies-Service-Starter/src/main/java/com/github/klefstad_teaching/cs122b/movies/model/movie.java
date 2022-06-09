package com.github.klefstad_teaching.cs122b.movies.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class movie {
    public Long getId() {
        return id;
    }

    public movie setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public movie setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public movie setYear(Integer year) {
        this.year = year;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public movie setDirector(String director) {
        this.director = director;
        return this;
    }

    public Double getRating() {
        return rating;
    }

    public movie setRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public movie setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public movie setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public movie setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    private Long id;
    private String title;
    private Integer year;
    private String director;
    private Double rating;
    private String backdropPath;
    private String posterPath;
    private Boolean hidden;

    public Integer getNumVotes() {
        return numVotes;
    }

    public movie setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
        return this;
    }

    public BigInteger getBudget() {
        return budget;
    }

    public movie setBudget(BigInteger budget) {
        this.budget = budget;
        return this;
    }

    public BigInteger getRevenue() {
        return revenue;
    }

    public movie setRevenue(BigInteger revenue) {
        this.revenue = revenue;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public movie setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    private Integer numVotes;
    private BigInteger budget;
    private BigInteger revenue;
    private String overview;
}
