package com.github.klefstad_teaching.cs122b.movies.model;
import com.github.klefstad_teaching.cs122b.core.result.Result;

import java.util.List;

public class movieSearchByIdResponse {
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public com.github.klefstad_teaching.cs122b.movies.model.movie getMovie() {
        return movie;
    }

    public void setMovie(com.github.klefstad_teaching.cs122b.movies.model.movie movie) {
        this.movie = movie;
    }

    public List<genresOrPersons> getGenres() {
        return genres;
    }

    public void setGenres(List<genresOrPersons> genres) {
        this.genres = genres;
    }

    public List<genresOrPersons> getPersons() {
        return persons;
    }

    public void setPersons(List<genresOrPersons> persons) {
        this.persons = persons;
    }

    private Result result;
    private movie movie;
    private List<genresOrPersons>genres;
    private List<genresOrPersons> persons;
}
