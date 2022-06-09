import Config from "backend/config.json";
import Axios from "axios";

export async function movie_search(movieRequest,accessToken) {
    const requestBody = {
        title: movieRequest.title,
        year: movieRequest.year,
        director: movieRequest.director,
        genre: movieRequest.genre,
        limit: movieRequest.limit,
        page: movieRequest.page,
        orderBy: movieRequest.orderBy,
        direction: movieRequest.direction
    };
    const options = {
        method: "GET", // Method type ("POST", "GET", "DELETE", ect)
        baseURL: Config.moviesUrl, // Base URL (localhost:8081 for example)
        url: Config.movies.movie_search, // Path of URL ("/register")
        params: requestBody, // Data to send in Body (The RequestBody to send)
        headers:{
            Authorization: "Bearer "+accessToken,
        }
    }

    return Axios.request(options);
}

export async function movie_search_by_id(movieId,accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.moviesUrl,
        url: Config.movies.movie_id_search+movieId,
        headers:{
            Authorization: "Bearer "+accessToken,
        }
    }

    return Axios.request(options);
}