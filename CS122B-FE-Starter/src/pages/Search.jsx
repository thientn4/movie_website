import React, {useState} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {movie_search} from "backend/movies";
import {useNavigate} from "react-router-dom";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const StyledH1 = styled.h1`
`

const StyledInput = styled.input`
`

const StyledButton = styled.button`
`
/**
 * useUser():
 * <br>
 * This is a hook we will use to keep track of our accessToken and
 * refreshToken given to use when the user calls "login".
 * <br>
 * For now, it is not being used, but we recommend setting the two tokens
 * here to the tokens you get when the user completes the login call (once
 * you are in the .then() function after calling login)
 * <br>
 * These have logic inside them to make sure the accessToken and
 * refreshToken are saved into the local storage of the web browser
 * allowing you to keep values alive even when the user leaves the website
 * <br>
 * <br>
 * useForm()
 * <br>
 * This is a library that helps us with gathering input values from our
 * users.
 * <br>
 * Whenever we make a html component that takes a value (<input>, <select>,
 * ect) we call this function in this way:
 * <pre>
 *     {...register("email")}
 * </pre>
 * Notice that we have "{}" with a function call that has "..." before it.
 * This is just a way to take all the stuff that is returned by register
 * and <i>distribute</i> it as attributes for our components. Do not worry
 * too much about the specifics of it, if you would like you can read up
 * more about it on "react-hook-form"'s documentation:
 * <br>
 * <a href="https://react-hook-form.com/">React Hook Form</a>.
 * <br>
 * Their documentation is very detailed and goes into all of these functions
 * with great examples. But to keep things simple: Whenever we have a html with
 * input we will use that function with the name associated with that input,
 * and when we want to get the value in that input we call:
 * <pre>
 * getValue("email")
 * </pre>
 * <br>
 * To Execute some function when the user asks we use:
 * <pre>
 *     handleSubmit(ourFunctionToExecute)
 * </pre>
 * This wraps our function and does some "pre-checks" before (This is useful if
 * you want to do some input validation, more of that in their documentation)
 */

const genres=[
    'None',
    'Adventure',
    'Fantasy',
    'Animation',
    'Drama',
    'Horror',
    'Action',
    'Comedy',
    'History',
    'Western',
    'Thriller',
    'Crime',
    'Documentary',
    'Science Fiction',
    'Mystery',
    'Music',
    'Romance',
    'Family',
    'War',
    'TV Movie'
]
let cur_page=1
let nav_allow=false;
let in_title = "";
let in_director = "";
let in_year = "None";
let in_genre =  "None";
let in_limit = 10;
let in_orderBy = "title";
let in_direction = "asc";
const Search = () => {
    const{
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const navigate = useNavigate()

    const {register, getValues, handleSubmit} = useForm();

    const [movies,setMovies]=useState([])

    const submitMovieSearch = () => {

        let title=(in_title==="")?null:in_title;
        let director=(in_director==="")?null:in_director;
        let year=(in_year==="None")?null:in_year;
        let genre=(in_genre==="None")?null:in_genre;

        const payLoad = {
            title: title,
            year: year,
            director: director,
            genre: genre,
            limit: in_limit,
            page: cur_page,
            orderBy: in_orderBy,
            direction: in_direction
        }
        movie_search(payLoad,accessToken)
            .then(response => {
                if(response.data.movies!==undefined)
                    setMovies(response.data.movies);
                else {
                    if(cur_page>1)cur_page -= 1;
                    alert("no movies found");
                }
            })
            .catch(error => {
                alert(JSON.stringify(error.response.result, null, 2));
            })
    }


    const reSearch=()=>{
        nav_allow=true;
        in_title = getValues("title");
        in_director = getValues("director");
        in_year = getValues("year")
        in_genre =  getValues("genre")
        in_limit = getValues("limit")
        in_orderBy = getValues("order")
        in_direction = getValues("direction")
        cur_page=1;
        submitMovieSearch();
    }

    const submitPrev=()=>{
        if(cur_page>1 && nav_allow===true) {
            cur_page-=1;
            submitMovieSearch();
        }
    }

    const submitNext=()=>{
        if(nav_allow===true) {
            cur_page += 1;
            submitMovieSearch();
        }
    }

    const searchStyle={
        textAlign:"center",
        minHeight:"100vh",
        justifyContent:"space-between",

        width: "1000px",
        display:"flex",
        flexDirection: "column",
    }
    const searchInputStyle={
        textAlign:"center",
        height:"70px",
        justifyContent:"space-between",

        width: "1000px",
        display:"flex",
        flexDirection: "row",
    }
    const pageNavStyle={
        textAlign:"center",
        height:"30px",
        justifyContent:"space-around",

        width: "1000px",
        display:"flex",
        flexDirection: "row",
        borderTop: "5px solid #DB8C16",
        paddingTop:"15px"
    }
    const buttonStyle={
        background:"#DB8C16",
        border:"none",
        height:"30px",
        width:"80px",
        fontSize:"18px",
        color:"white",
        borderRadius:"5px",
    }
    const inputTitleStyle={
        border:"none",
        height:"30px",
        width:"210px",
        fontSize:"15px",
        borderRadius:"5px",
        paddingLeft:"5px",
        paddingRight:"5px",
        outline: "none"
    }
    const inputDirectorStyle={
        border:"none",
        height:"30px",
        width:"120px",
        fontSize:"15px",
        borderRadius:"5px",
        paddingLeft:"5px",
        paddingRight:"5px",
        outline: "none"
    }
    const holderStyle={
        textAlign:"left",
        height:"55px",
        display:"flex",
        flexDirection:"column",
        justifyContent:"space-between"
    }
    const genreStyle={
        border:"none",
        height:"30px",
        width:"135px",
        fontSize:"15px",
        borderRadius:"5px",
        paddingLeft:"5px",
        paddingRight:"5px",
        outline: "none"
    }
    const yearOrderStyle={
        border:"none",
        height:"30px",
        width:"80px",
        fontSize:"15px",
        borderRadius:"5px",
        paddingLeft:"5px",
        paddingRight:"5px",
        outline: "none"
    }
    const directionStyle={
        border:"none",
        height:"30px",
        width:"105px",
        fontSize:"15px",
        borderRadius:"5px",
        paddingLeft:"5px",
        paddingRight:"5px",
        outline: "none"
    }
    const moviePageStyle={
        border:"none",
        height:"30px",
        width:"100px",
        fontSize:"15px",
        borderRadius:"5px",
        paddingLeft:"5px",
        paddingRight:"5px",
        outline: "none",
    }
    const nextPrevStyle={
        background:"#DB8C16",
        border:"none",
        height:"30px",
        width:"30px",
        fontSize:"18px",
        color:"white",
        borderRadius:"5px",
    }
    const pageNavHolderStyle={
        width:"150px",
        display:"flex",
        justifyContent:"space-between"
    }
    const tableStyle={
        width: "100%",
        fontSize:"17px",
        tableLayout:"fixed",
        borderCollapse:"collapse"
    }
    const tableTitleStyle={
        textAlign:"left",
        fontSize:"17px",
        height:"25px",
        background:"#DB8C16"
    }
    const tableTitleYearStyle={
        textAlign:"center",
        fontSize:"17px",
        height:"25px",
        background:"#DB8C16"
    }
    const colStyle={
        textAlign:"left",
        fontSize:"17px",
        borderBottom: "grey 2px solid",
        height:"25px",
        width:"50%",
        padding:"3px"
    }
    const yearColStyle={
        textAlign:"center",
        fontSize:"17px",
        borderBottom: "grey 2px solid",
        height:"25px",
        width:"10%"
    }
    const directorColStyle={
        textAlign:"left",
        fontSize:"17px",
        borderBottom: "grey 2px solid",
        height:"25px",
        width:"40%"
    }
    const curYear=new Date().getFullYear()

    if(accessToken==null)return(
        <div>
            <h1>Please login first</h1>
        </div>
    )

    return (
        <StyledDiv style={searchStyle}>
            <div>
                <div style={searchInputStyle}>
                    <div style={holderStyle}>
                        <p>Movie title</p>
                        <input {...register("title")} type={"text"} style={inputTitleStyle}/>
                    </div>
                    <div style={holderStyle}>
                        <p>Director name</p>
                        <input {...register("director")} type={"text"} style={inputDirectorStyle}/>
                    </div>


                    <div style={holderStyle}>
                        <p>Genre</p>
                        <select style={genreStyle} {...register("genre")}>
                            {genres.map((genre) => (
                                <option value={genre}>{genre}</option>
                            ))}
                        </select>
                    </div>
                    <div style={holderStyle}>
                        <p>Year</p>
                        <select style={yearOrderStyle} {...register("year")}>
                            <option value="None">None</option>
                            {[...Array(100).keys()].map((year) => (
                                <option value={curYear-year}>{curYear-year}</option>
                            ))}
                        </select>
                    </div>
                    <div style={holderStyle}>
                        <p>Order by</p>
                        <select style={yearOrderStyle} {...register("order")}>
                            <option value={"title"}>{"title"}</option>
                            <option value={"rating"}>{"rating"}</option>
                            <option value={"year"}>{"year"}</option>
                        </select>
                    </div>
                    <div style={holderStyle}>
                        <p>Direction</p>
                        <select style={directionStyle} {...register("direction")}>
                            <option value={"asc"}>{"ascending"}</option>
                            <option value={"desc"}>{"descending"}</option>
                        </select>
                    </div>
                    <div style={holderStyle}>
                        <p>Movies/page</p>
                        <select style={moviePageStyle} {...register("limit")}>
                            <option value={10}>{"10"}</option>
                            <option value={25}>{"25"}</option>
                            <option value={50}>{"50"}</option>
                            <option value={100}>{"100"}</option>
                        </select>
                    </div>
                    <div style={holderStyle}>
                        <p></p>
                        <button onClick={handleSubmit(reSearch)} style={buttonStyle}>Search</button>
                    </div>
                </div>


                <table style={tableStyle}>
                    <tbody>
                        <tr>
                            <th style={tableTitleStyle}>Title</th>
                            <th style={tableTitleYearStyle}>Year</th>
                            <th style={tableTitleStyle}>Director</th>
                        </tr>
                        {movies.map((movie) => (
                            <tr onClick={()=>navigate("../movie/"+movie.id)}>
                                <td style={colStyle}>{movie.title}</td>
                                <td style={yearColStyle}>{movie.year}</td>
                                <td style={directorColStyle}>{movie.director}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <br></br>
            <div style={pageNavStyle}>
                <div style={pageNavHolderStyle}>
                    <button onClick={handleSubmit(submitPrev)} style={nextPrevStyle}>{"<"}</button>
                    <h2>{cur_page}</h2>
                    <button onClick={handleSubmit(submitNext)} style={nextPrevStyle}>{">"}</button>
                </div>
            </div>
        </StyledDiv>
    );
}

export default Search;
