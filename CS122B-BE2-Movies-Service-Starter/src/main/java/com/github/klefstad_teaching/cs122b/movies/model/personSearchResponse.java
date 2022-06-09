package com.github.klefstad_teaching.cs122b.movies.model;
import com.github.klefstad_teaching.cs122b.core.result.Result;

import java.util.List;

public class personSearchResponse {
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<person> getPersons() {
        return persons;
    }

    public void setPersons(List<person> persons) {
        this.persons = persons;
    }

    private Result result;
    private List<person> persons;
}
