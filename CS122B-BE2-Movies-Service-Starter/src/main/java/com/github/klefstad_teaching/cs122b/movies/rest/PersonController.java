package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.model.*;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;

import java.text.ParseException;
import java.util.List;

@RestController
public class PersonController
{
    private final MovieRepo repo;

    @Autowired
    public PersonController(MovieRepo repo)
    {
        this.repo = repo;
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<personSearchByIdResponse> personSearchById(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long personId){
        personSearchByIdResponse body = new personSearchByIdResponse();
        body.setResult(MoviesResults.PERSON_WITH_ID_FOUND);
        body.setPerson(this.repo.searchPersonById(personId));
        if(body.getPerson()==null)
            throw new ResultError(MoviesResults.NO_PERSON_WITH_ID_FOUND);
        return ResponseEntity.status(200)
                .body(body);
    }

    @GetMapping("/person/search")
    public ResponseEntity<personSearchResponse> personSearch(
            @AuthenticationPrincipal SignedJWT user,
            @RequestParam(name = "name",required = false) String name,
            @RequestParam(name = "birthday",required = false) String birthday,
            @RequestParam(name = "movieTitle",required = false) String movieTitle,
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
        if(orderBy==null)orderBy="name";
        else if(!orderBy.equals("name")&&!orderBy.equals("popularity")&&!orderBy.equals("birthday"))
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


        personSearchResponse body=new personSearchResponse();
        List<person> searchResult=this.repo.searchPerson(
                hasAccess,
                name,
                birthday,
                movieTitle,
                limit, //has default
                page, //has default
                orderBy,
                direction //has default
        );
        if(searchResult.size()==0) {
            throw new ResultError(MoviesResults.NO_PERSONS_FOUND_WITHIN_SEARCH);
        }
        else {
            body.setPersons(searchResult);
            body.setResult(MoviesResults.PERSONS_FOUND_WITHIN_SEARCH);
        }
        return ResponseEntity.status(200)
                .body(body);
    }
}
