import React, {useState} from "react";
import {useParams} from "react-router-dom";
import {movie_search_by_id} from "backend/movies";
import {useUser} from "../hook/User";
import {useForm} from "react-hook-form";
import {cartInsert} from "../backend/billing";

const MovieDetails=()=>{
    const{
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();
    const {register, getValues, handleSubmit} = useForm();

    const [title,setTitle]=useState("None");
    const [year,setYear]=useState("None");
    const [director,setDirector]=useState("None");
    const [rating,setRating]=useState("None");
    const [numVotes,setNumVotes]=useState("None");
    const [budget,setBudget]=useState("None");
    const [revenue,setRevenue]=useState("None");
    const [overview,setOverview]=useState("None");
    const [backdropPath,setBackdropPath]=useState("None");
    const [posterPath,setPosterPath]=useState("None");

    const movieId=useParams().id;
        //return a JSON object of parameters from the path leading to this page
    React.useEffect(()=>{
        movie_search_by_id(movieId,accessToken).then(
            response => {
                setTitle(response.data.movie.title);
                setYear(response.data.movie.year);
                setDirector(response.data.movie.director);
                setRating(response.data.movie.rating);
                setNumVotes(response.data.movie.numVotes)
                setBudget(response.data.movie.budget)
                setRevenue(response.data.movie.revenue)
                setOverview(response.data.movie.overview);
                setBackdropPath(response.data.movie.backdropPath)
                setPosterPath(response.data.movie.posterPath);
            }
        )
    },[])

    const addToCart=()=>{
        const quantity=getValues("quantity");
        cartInsert(movieId,quantity,accessToken)
            .then(response => {
                alert(JSON.stringify(response.data.result.message, null, 2));
            })
            .catch(error => alert(error.response.data.result.message))
    }

    const posterStyle={
        width:"200px",
        height:"300px"
    }
    const statisticAndPosterStyle={
        display:"flex",
        flexDirection: "row",
        justifyContent:"space-between"
    }
    const statisticStyle={
        width:"220px",
        display:"flex",
        flexDirection: "column",
        justifyContent:"space-around",
        fontSize:"20px"
    }
    const borderStyle={
        borderBottom:"solid white"
    }
    const backgroundStyle={
        width:"450px"
    }
    const quantityStyle={
        border:"none",
        height:"29px",
        width:"85px",
        fontSize:"20px",
        borderRadius:"5px",
        paddingLeft:"5px",
        paddingRight:"5px",
        outline: "none",
    }
    const addCartStyle={
        width:"220px",
        display:"flex",
        flexDirection: "row",
        justifyContent:"space-between"
    }
    const buttonStyle={
        background:"#DB8C16",
        border:"none",
        height:"30px",
        width:"120px",
        fontSize:"18px",
        color:"white",
        borderRadius:"5px",
    }
    return (
      <div style={backgroundStyle}>
          <div style={borderStyle}>
              <h1>{title}</h1>
              <br></br>
          </div>
          <br></br>
          <div style={borderStyle}>
              <div style={statisticAndPosterStyle}>
                  <img style={posterStyle}
                       src={"https://image.tmdb.org/t/p/original/"+posterPath}
                  ></img>
                  <div style={statisticStyle}>
                      <p>year: {year}</p>
                      <p>director: {director}</p>
                      <p>rating: {rating}/10</p>
                      <p>#vote: {numVotes}</p>
                      <p>budget: ${budget}</p>
                      <p>revenue: ${revenue}</p>
                      <div style={addCartStyle}>
                          <button onClick={handleSubmit(addToCart)} style={buttonStyle}>Add</button>
                          <select style={quantityStyle} {...register("quantity")}>
                              {[...Array(10).keys()].map((quantity) => (
                                  <option value={quantity+1}>{quantity+1}</option>
                              ))}
                          </select>
                      </div>
                  </div>
              </div>
              <br></br>
          </div>
          <br></br>
          <div>
              <h1>Story</h1>
              <br></br>
              <p>{overview}</p>
              <br></br>
          </div>
      </div>
    );
}
export default MovieDetails;