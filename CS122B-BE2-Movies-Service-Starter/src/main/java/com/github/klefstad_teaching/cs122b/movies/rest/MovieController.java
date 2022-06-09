package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.model.movie;
import com.github.klefstad_teaching.cs122b.movies.model.movieSearchByIdResponse;
import com.github.klefstad_teaching.cs122b.movies.model.movieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;

import java.text.ParseException;
import java.util.List;

@RestController
public class MovieController
{
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public MovieController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @GetMapping("/movie/search")
    public ResponseEntity<movieSearchResponse> movieSearch(
            @AuthenticationPrincipal SignedJWT user,
            @RequestParam(name = "title",required = false) String title,
            @RequestParam(name = "year",required = false) Integer year,
            @RequestParam(name = "director",required = false) String director,
            @RequestParam(name = "genre",required = false) String genre,
            @RequestParam(name = "limit",required = false) Integer limit,
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "orderBy",required = false) String orderBy,
            @RequestParam(name = "direction",required = false) String direction
        ) throws ParseException {

        if(direction==null)direction="asc";
        else if(!direction.equals("asc")&&!direction.equals("desc"))
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        if(limit==null)limit=10;
        else if(limit!=10&&limit!=25&&limit!=50&&limit!=100)
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        if(page==null)page=1;
        else if(page<=0)
            throw new ResultError(MoviesResults.INVALID_PAGE);
        if(orderBy==null)orderBy="title";
        else if(!orderBy.equals("title")&&!orderBy.equals("rating")&&!orderBy.equals("year"))
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);




        user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        boolean hasAccess=false;
        for(String Role:user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES)) {
            if(Role.equalsIgnoreCase("Admin")
            ||Role.equalsIgnoreCase("Employee")) {
                hasAccess = true;
                break;
            }
        }


        movieSearchResponse body=new movieSearchResponse();
        List<movie>searchResult=this.repo.searchMovies(
                hasAccess,
                title,
                year,
                director,
                genre,
                limit, //has default
                page, //has default
                orderBy,
                direction //has default
        );
        if(searchResult.size()==0) {
            throw new ResultError(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH);
        }
        else {
            body.setMovies(searchResult);
            body.setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH);
        }
        return ResponseEntity.status(200)
                .body(body);
    }

    @GetMapping("/movie/search/person/{personId}")
    public ResponseEntity<movieSearchResponse> movieSearchByPid(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long personId,
            @RequestParam(name = "limit",required = false) Integer limit,
            @RequestParam(name = "page",required = false) Integer page,
            @RequestParam(name = "orderBy",required = false) String orderBy,
            @RequestParam(name = "direction",required = false) String direction
    ) throws ParseException {

        if(direction==null)direction="asc";
        else if(!direction.equals("asc")&&!direction.equals("desc"))
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        if(limit==null)limit=10;
        else if(limit!=10&&limit!=25&&limit!=50&&limit!=100)
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        if(page==null)page=1;
        else if(page<=0)
            throw new ResultError(MoviesResults.INVALID_PAGE);
        if(orderBy==null)orderBy="title";
        else if(!orderBy.equals("title")&&!orderBy.equals("rating")&&!orderBy.equals("year"))
            throw new ResultError(MoviesResults.INVALID_ORDER_BY);




        user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        boolean hasAccess=false;
        for(String Role:user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES)) {
            if(Role.equalsIgnoreCase("Admin")
                    ||Role.equalsIgnoreCase("Employee")) {
                hasAccess = true;
                break;
            }
        }


        movieSearchResponse body=new movieSearchResponse();

        List<movie>searchResult=this.repo.searchMoviesByPid(
                hasAccess,
                personId,
                limit, //has default
                page, //has default
                orderBy, //has default
                direction //has default
        );
        if(searchResult.size()==0) {
            throw new ResultError(MoviesResults.NO_MOVIES_WITH_PERSON_ID_FOUND);
        }
        else {
            body.setMovies(searchResult);
            body.setResult(MoviesResults.MOVIES_WITH_PERSON_ID_FOUND);
        }

        return ResponseEntity.status(200)
                .body(body);
    }
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<movieSearchByIdResponse> movieSearchById(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long movieId) throws ParseException {

        user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        boolean hasAccess=false;
        for(String Role:user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES)) {
            if(Role.equalsIgnoreCase("Admin")
                    ||Role.equalsIgnoreCase("Employee")) {
                hasAccess = true;
                break;
            }
        }

        movie searchResult=this.repo.searchMovieById(hasAccess, movieId);
        if(searchResult==null) {
            throw new ResultError(MoviesResults.NO_MOVIE_WITH_ID_FOUND);
        }
        movieSearchByIdResponse body=new movieSearchByIdResponse();
        body.setResult(MoviesResults.MOVIE_WITH_ID_FOUND);
        body.setMovie(searchResult);
        body.setPersons(this.repo.searchPeopleByMid(movieId));
        body.setGenres(this.repo.searchGenresByMid(movieId));
        return ResponseEntity.status(200)
                .body(body);
    }
}
