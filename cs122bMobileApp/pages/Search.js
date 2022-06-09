import React from 'react';
import { Button, Text, TextInput, StyleSheet, View, TouchableHighlight, ScrollView } from 'react-native';
import SelectDropdown from 'react-native-select-dropdown';
import {movie_search} from '../backend/movies';

const genres=[
    'None','Adventure','Fantasy','Animation','Drama','Horror','Action','Comedy',
    'History','Western','Thriller','Crime','Documentary','Science Fiction',
    'Mystery','Music','Romance','Family','War','TV Movie'
];
const curYear=new Date().getFullYear();


const movieDivider = () => {
    return (
        <View
            style={{
                height: 1,
                width: "100%",
                backgroundColor: "#607D8B",
            }}
        />
    );
}

let curPage=1;
const Search = ({ route, navigation }) => {
    const { accessToken, refreshToken } = route.params;
    const [navAllow, onChangeNavAllow] = React.useState(false);
    const [title, onChangeTitle] = React.useState("");
    const [director, onChangeDirector] = React.useState("");
    const [year, onChangeYear] = React.useState("None");
    const [genre, onChangeGenre] = React.useState("None");
    const [limit, onChangeLimit] = React.useState(10);
    const [orderBy, onChangeOrderBy] = React.useState("title");
    const [direction, onChangeDirection] = React.useState("asc");
    const [movieList,setMovieList]=React.useState([]);

    const submitMovieSearch = () => {
        const payLoad = {
            title: ((title==="")?null:title),
            year: ((year==="None")?null:year),
            director: ((director==="")?null:director),
            genre: ((genre==="None")?null:genre),
            limit: limit,
            page: curPage,
            orderBy: orderBy,
            direction: direction
        }
        movie_search(payLoad,accessToken)
            .then((response) => response.json() )
            .then((json) => {
                if(json.movies!==undefined)
                    setMovieList(json.movies);
                else {
                    if(curPage>1) {
                        curPage -= 1;
                    }
                    alert("no movies found");
                }

            })
            .catch((error) => {
                alert("ERROR:\n"+JSON.stringify(error, null, 2));
            });
    }


    const reSearch=()=>{
        /*
        nav_allow=true;
        in_title = getValues("title");
        in_director = getValues("director");
        in_year = getValues("year")
        in_genre =  getValues("genre")
        in_limit = getValues("limit")
        in_orderBy = getValues("order")
        in_direction = getValues("direction")
        */
        onChangeNavAllow(true);
        curPage=1;
        submitMovieSearch();
    }

    const submitPrev=()=>{
        if(curPage>1 && navAllow===true) {
            curPage-=1;
            submitMovieSearch();
        }
    }

    const submitNext=()=>{
        if(navAllow===true) {
            curPage+=1
            submitMovieSearch();
        }
    }

    return (
        <ScrollView>
        <View style={styles.container}>
            <Text style={{ fontSize: 20, marginLeft: 10, marginTop:10}}>Title:</Text>
            <TextInput
                style={styles.input}
                onChangeText={onChangeTitle}
                placeholder="Title"
                value={title}
            />
            <View>
                <Text style={{ fontSize: 20, marginLeft: 10}}>Director name:</Text>
                <TextInput
                    style={styles.input}
                    onChangeText={onChangeDirector}
                    placeholder="Director name"
                    value={director}
                />
            </View>
            <View style={styles.pairContainer}>
                <View>
                    <Text style={{ fontSize: 20, marginLeft: 10}}>Genre:</Text>
                    <SelectDropdown
                        data={genres}
                        defaultValue={'None'}
                        onSelect={(item,index)=>{
                            onChangeGenre(item)
                        }}
                    />
                </View>
                <View>
                    <Text style={{ fontSize: 20, marginLeft: 10}}>Year:</Text>
                    <SelectDropdown
                        data={['None'].concat([...Array(10).keys()].map(year=>(curYear-year)))}
                        defaultValue={'None'}
                        onSelect={(item,index)=>{
                            onChangeYear(item)
                        }}
                    />
                </View>
            </View>
            <View style={styles.pairContainer}>
                <View>
                    <Text style={{ fontSize: 20, marginLeft: 10}}>Order by:</Text>
                    <SelectDropdown
                        data={['title','rating','year']}
                        defaultValue={'title'}
                        onSelect={(item,index)=>{
                            onChangeOrderBy(item)
                        }}
                    />
                </View>
                <View>
                    <Text style={{ fontSize: 20, marginLeft: 10}}>Direction:</Text>
                    <SelectDropdown
                        data={['asc','desc']}
                        defaultValue={'asc'}
                        onSelect={(item,index)=>{
                            onChangeDirection(item)
                        }}
                    />
                </View>
            </View>
            <View>
                <Text style={{ fontSize: 20, marginLeft: 10}}>Movies/page:</Text>
                <SelectDropdown
                    data={[10,25,50,100]}
                    defaultValue={10}
                    onSelect={(item,index)=>{
                        onChangeLimit(item)
                    }}
                />
            </View>

            <View style={styles.buttonContainer}>
                <Button
                    title="SEARCH"
                    color="#841584"
                    onPress = {
                        async () => {
                            reSearch();
                        }
                    }
                />
            </View>
            {movieList.map((item) => (
                <View style={styles.container} key={item.id}>
                        <TouchableHighlight
                            onPress={() => {
                                //alert("id: " + item.id+"\naccess token: "+accessToken);
                                navigation.navigate("MovieDetails",{
                                    accessToken:accessToken,
                                    refreshToken:refreshToken,
                                    movieId:item.id
                                });
                            }}
                            underlayColor="white">
                            <View style={styles.subContainer}>
                                    <Text style={{ fontSize: 22, margin: 10}}>
                                        {item.title}
                                    </Text>
                                    <Text style={{ fontSize: 22, margin: 10}}>
                                        {item.year}
                                    </Text>
                                    <Text style={{ fontSize: 22, margin: 10}}>
                                        {item.director}
                                    </Text>
                            </View>
                        </TouchableHighlight>
                    </View>
            ))}
            <View style={styles.navContainer}>
                <Button
                    title="PREV"
                    color="#841584"
                    onPress = {
                        async () => {
                            submitPrev();
                        }
                    }
                />
                <Text style={{ fontSize: 20}}>page {curPage}</Text>
                <Button
                    title="NEXT"
                    color="#841584"
                    onPress = {
                        async () => {
                            submitNext();
                        }
                    }
                />
            </View>
        </View>
        </ScrollView>
    );
};

const styles = StyleSheet.create({
    view: {
        margin: 10,
    },
    container: {
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
    buttonContainer: {
        margin: 20
    },
    input: {
        height: 40,
        margin: 12,
        borderWidth: 1,
        padding: 10
    },baseText: {
        fontFamily: "Cochin"
    },
    titleText: {
        fontSize: 20,
        fontWeight: "bold"
    },
    thumbnail: {
        width: 50,
        height: 50,
        margin: 10,
    },
    pairContainer: {
        display:"flex",
        flexDirection:"row"
    },
    navContainer: {
        display:"flex",
        flexDirection:"row",
        justifyContent: 'space-around',
        margin: 10
    }
});

export default Search;