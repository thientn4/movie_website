package com.github.klefstad_teaching.cs122b.movies.model;
import com.github.klefstad_teaching.cs122b.core.result.Result;

public class personSearchByIdResponse {
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public person getPerson() {
        return person;
    }

    public void setPerson(person person) {
        this.person = person;
    }

    private Result result;
    private person person;
}
