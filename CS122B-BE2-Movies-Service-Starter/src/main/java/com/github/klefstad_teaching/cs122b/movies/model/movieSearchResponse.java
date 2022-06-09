package com.github.klefstad_teaching.cs122b.movies.model;
import com.github.klefstad_teaching.cs122b.core.result.Result;

import java.util.List;

public class movieSearchResponse {
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<movie> getMovies() {
        return movies;
    }

    public void setMovies(List<movie> movies) {
        this.movies = movies;
    }

    private Result result;
    private List<movie> movies;
}
