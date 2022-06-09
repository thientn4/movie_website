import React from 'react';
import { Text, StyleSheet, View, Image, ScrollView } from 'react-native';
import {movie_search_by_id} from '../backend/movies';

const MovieDetails = ({ route, navigation }) => {
    const {accessToken, refreshToken, movieId} = route.params;
    const [title, setTitle] = React.useState("");
    const [poster, setPoster] = React.useState("https:/reactnative.dev/img/tiny_logo.png");
    const [year, setYear] = React.useState(0);
    const [director, setDirector] = React.useState("");
    const [rating, setRating] = React.useState(0.0);
    const [vote, setVote] = React.useState(0);
    const [budget, setBudget] = React.useState(0);
    const [revenue, setRevenue] = React.useState(0);
    const [overview, setOverview] = React.useState("");
    React.useEffect(()=>{
        movie_search_by_id(movieId,accessToken)
            .then((response) => response.json() )
            .then((json) => {
                setTitle(json.movie.title);
                setYear(json.movie.year);
                setDirector(json.movie.director);
                setRating(json.movie.rating);
                setVote(json.movie.numVotes)
                setBudget(json.movie.budget)
                setRevenue(json.movie.revenue)
                setOverview(json.movie.overview);
                setPoster("https://image.tmdb.org/t/p/original/"+json.movie.posterPath);
            })
            .catch((error) => {
                alert("ERROR:\n"+JSON.stringify(error, null, 2));
            });
    },[])
    return (
        <ScrollView>
            <View style={styles.container}>
                <View style={styles.subContainer}>
                    <Text style={styles.titleText}>{title}</Text>
                </View>
                <View style={styles.imageContainer}>
                    <View style={styles.imageWrapper}>
                        <Image
                            style={styles.image}
                            source={{uri: poster}}
                        />
                    </View>
                </View>
                <View style={styles.subContainer}>
                    <Text style={{fontSize: 25}}>year: {year}</Text>
                    <Text style={{fontSize: 25}}>director: {director}</Text>
                    <Text style={{fontSize: 25}}>rating: {rating}/10</Text>
                    <Text style={{fontSize: 25}}>#vote: {vote}</Text>
                    <Text style={{fontSize: 25}}>budget: ${budget}</Text>
                    <Text style={{fontSize: 25}}>revenue: ${revenue}</Text>
                </View>
                <View>
                    <Text style={styles.titleText}>Story</Text>
                    <Text style={{fontSize: 25}}>{overview}</Text>
                </View>
            </View>
        </ScrollView>
    )
}

const styles = StyleSheet.create({
    container: {
        margin:30,
        flex: 1,
        justifyContent: 'center',
        display:"flex",
        flexDirection:"column"
    },
    subContainer: {
        //flex: 1,
        borderBottomWidth: 2,
        display:"flex",
        flexDirection:"column"
    },
    imageContainer: {
        //flex: 1,
        justifyContent: 'center',
        borderBottomWidth: 2,
        display:"flex",
        flexDirection:"column",
        height:500
    },
    imageWrapper: {
        //flex: 1,
        justifyContent: 'center',
        display:"flex",
        flexDirection:"row"
    },
    image:{
        width:300,
        height:450
    },
    buttonContainer: {
        margin: 20
    },
    titleText: {
        fontSize: 35,
        fontWeight: "bold"
    }
});

export default MovieDetails;