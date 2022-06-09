import Config from "./config.json";

export async function movie_search(movieRequest,accessToken) {
    return await fetch('http://10.0.2.2:8082/movie/search?'+new URLSearchParams({
        title: movieRequest.title,
        year: movieRequest.year,
        director: movieRequest.director,
        genre: movieRequest.genre,
        limit: movieRequest.limit,
        page: movieRequest.page,
        orderBy: movieRequest.orderBy,
        direction: movieRequest.direction
    }),
    {
        method: 'GET',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            Authorization: "Bearer "+accessToken
        }
    })
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

    //return Axios.request(options);
    //return await Axios.request(options);
    return await fetch('http://10.0.2.2:8082/movie/'+movieId, {
        method: 'GET',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            Authorization: "Bearer "+accessToken
        },
    })
}