package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.movies.model.genresOrPersons;
import com.github.klefstad_teaching.cs122b.movies.model.movie;
import com.github.klefstad_teaching.cs122b.movies.model.person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.Types;
import java.util.List;

@Component
public class MovieRepo
{
    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper objectMapper;
    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template)
    {
        this.template=template;
        this.objectMapper=objectMapper;
    }

    public List<movie> searchMovies(
            boolean hasAccess,  //-------> TODO
            String title,
            Integer year,
            String director,
            String genre,
            Integer limit, //has default 10
            Integer page, //has default 1
            String orderBy, //has default "title"
            String direction //has default ASC
    ){
        String query=
                /*
                "select distinct M.id, M.title, M.year, P.name, M.rating, M.backdrop_path, M.poster_path, M.hidden " +
                "from movies.movie M, movies.person P, movies.movie_genre MG, movies.genre G " +
                "where M.director_id=P.id and M.id=MG.movie_id and MG.genre_id=G.id ";
                 */
                "select distinct M.id, M.title, M.year, P.name, M.rating, M.backdrop_path, M.poster_path, M.hidden " +
                "from " +
                        " movies.movie M join " +
                        " movies.person P on  M.director_id=P.id left join " +
                        " (select * from movies.movie_genre MG join movies.genre G on MG.genre_id=G.id) as G " +
                "on M.id=G.movie_id where 1=1 ";
        if(hasAccess==false) {
            query += ("AND M.hidden = false ");
        }
        if(title!=null){
            query += ("AND M.title LIKE '%"+title+"%' ");
        }
        if(director!=null){
            query += ("AND P.name LIKE '%"+director+"%' ");
        }
        if(genre!=null){
            query += ("AND G.name LIKE '%"+genre+"%' ");
        }
        if(year!=null){
            query += ("AND M.year = "+year+" ");
        }
        query+=("order by M."+orderBy+" "+direction+", M.id asc ");
        query+=("limit "+limit+" offset "+(page-1)*limit+" ");
        List<movie> movies = this.template.query(
                query,
                new MapSqlParameterSource(),
                (rs, rowNum) ->
                        new movie()
                                .setId(rs.getLong("id"))
                                .setTitle(rs.getString(("title")))
                                .setYear(rs.getInt("year"))
                                .setDirector(rs.getString("name"))
                                .setRating(rs.getDouble("rating"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
                                .setHidden(rs.getBoolean("hidden"))
        );

        return movies;
    }
    public List<movie> searchMoviesByPid(
            boolean hasAccess,  //-------> TODO
            Long personId,
            Integer limit, //has default 10
            Integer page, //has default 1
            String orderBy, //has default "title"
            String direction //has default ASC
    ){
        String query=
                "select distinct M.id, M.title, M.year, P.name, M.rating, M.backdrop_path, M.poster_path, M.hidden " +
                        "from movies.movie M, movies.person P, movies.movie_genre MG, movies.genre G, movies.movie_person MP " +
                        "where M.director_id=P.id and M.id=MG.movie_id and MG.genre_id=G.id ";
        if(hasAccess==false) {
            query += ("and M.hidden = false ");
        }
        query+=("and MP.person_id = "+personId+" AND MP.movie_id=M.id ");
        query+=("order by M."+orderBy+" "+direction+", M.id asc ");
        query+=("limit "+limit+" offset "+(page-1)*limit+" ");
        List<movie> movies = this.template.query(
                query,
                new MapSqlParameterSource(),
                (rs, rowNum) ->
                        new movie()
                                .setId(rs.getLong("id"))
                                .setTitle(rs.getString(("title")))
                                .setYear(rs.getInt("year"))
                                .setDirector(rs.getString("name"))
                                .setRating(rs.getDouble("rating"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
                                .setHidden(rs.getBoolean("hidden"))
        );

        return movies;
    }

    public person searchPersonById(Long personId){

        List<person> persons = this.template.query(
                "select id, name, birthday, biography, birthplace, popularity, profile_path from movies.person "+
                "where id = :id;",
                new MapSqlParameterSource().addValue("id",personId,Types.INTEGER),
                (rs, rowNum) ->
                        new person()
                                .setId(rs.getLong("id"))
                                .setName(rs.getString(("name")))
                                .setBirthday(rs.getString("birthday"))
                                .setBiography(rs.getString("biography"))
                                .setBirthplace(rs.getString("birthplace"))
                                .setPopularity(rs.getFloat("popularity"))
                                .setProfilePath(rs.getString("profile_path"))
        );
        if(persons.size()==0) return null;
        return persons.get(0);
    }

    public List<person> searchPerson(
            boolean hasAccess,
            String name,
            String birthday,
            String movieTitle,
            Integer limit, //has default
            Integer page, //has default
            String orderBy,
            String direction //has default
    ){
        boolean chained=false;
        String query=
                "select distinct P.id, P.name, P.birthday, P.biography, P.birthplace, P.popularity, P.profile_path\n" +
                "from movies.person P ";
        if(movieTitle!=null) {
            query += (", movies.movie M, movies.movie_person MP where P.id=MP.person_id and M.id=MP.movie_id\n" +
                    "and M.title like '%" + movieTitle + "%' ");
            chained = true;
        }
        if(name!=null){
            if(chained)query += " and ";
            else query+=" where ";
            query += (" P.name like '%"+name+"%' ");
            chained=true;
        }
        if(birthday!=null){
            if(chained)query += " and ";
            else query+=" where ";
            query += (" P.birthday = '"+birthday+"' ");
        }
        query+=("order by P."+orderBy+" "+direction+", P.id asc ");
        query+=("limit "+limit+" offset "+(page-1)*limit+" ");
        List<person> people = this.template.query(
                query,
                new MapSqlParameterSource(),
                (rs, rowNum) ->
                        new person()
                                .setId(rs.getLong("id"))
                                .setName(rs.getString(("name")))
                                .setBirthday(rs.getString("birthday"))
                                .setBiography(rs.getString("biography"))
                                .setBirthplace(rs.getString("birthplace"))
                                .setPopularity(rs.getFloat("popularity"))
                                .setProfilePath(rs.getString("profile_path"))
        );

        return people;
        //return new ArrayList<person>();
    }

    public movie searchMovieById(boolean hasAccess, Long movieId){
        String query = "select distinct M.id, M.title, M.year, P.name, M.rating, M.backdrop_path, M.poster_path, M.hidden, M.num_votes, M.budget, M.revenue, M.overview  " +
                "from movies.movie M, movies.person P " +
                "where M.director_id=P.id and M.id = :id ";
        if(hasAccess==false) {
            query += ("and M.hidden = false ");
        }
        List<movie> movies = this.template.query(
                query,
                new MapSqlParameterSource().addValue("id",movieId,Types.INTEGER),
                (rs, rowNum) ->
                        new movie()
                                .setId(rs.getLong("id"))
                                .setTitle(rs.getString(("title")))
                                .setYear(rs.getInt("year"))
                                .setDirector(rs.getString("name"))
                                .setRating(rs.getDouble("rating"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
                                .setHidden(rs.getBoolean("hidden"))
                                .setNumVotes(rs.getInt("num_votes"))
                                .setBudget(BigInteger.valueOf(rs.getLong("budget")))
                                .setRevenue(BigInteger.valueOf(rs.getLong("revenue")))
                                .setOverview(rs.getString("overview"))
        );
        if(movies.size()==0) return null;
        return movies.get(0);
    }
    public List<genresOrPersons>searchPeopleByMid(Long movieId){
        List<genresOrPersons> people = this.template.query(
                "select P.id, P.name from movies.person P, movies.movie_person MP "+
                        "where P.id = MP.person_id and MP.movie_id = :id order by P.popularity desc, P.id asc;",
                new MapSqlParameterSource().addValue("id",movieId,Types.INTEGER),
                (rs, rowNum) ->
                        new genresOrPersons()
                                .setId(rs.getLong("id"))
                                .setName(rs.getString(("name")))
        );
        return people;
    }

    public List<genresOrPersons>searchGenresByMid(Long movieId){
        List<genresOrPersons> genres = this.template.query(
                "select G.id, G.name from movies.genre G, movies.movie_genre MG "+
                        "where G.id = MG.genre_id and MG.movie_id = :id order by G.name;",
                new MapSqlParameterSource().addValue("id",movieId,Types.INTEGER),
                (rs, rowNum) ->
                        new genresOrPersons()
                                .setId(rs.getLong("id"))
                                .setName(rs.getString(("name")))
        );
        return genres;
    }
}
