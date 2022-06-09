import React, { Component } from 'react';
import { Button, TextInput, StyleSheet, View } from 'react-native';
import {login} from "../backend/idm"

const Login = ( {navigation} ) => {
    const [email, onChangeEmail] = React.useState(null);
    const [password, onChangePassword] = React.useState(null);
    const submitLogin = () => {
        const payLoad = {
            email: email,
            password: password.split('')
        }
        login(payLoad)
            .then((response) => response.json() )
                .then((json) => {
                    alert(json.result.message);
                    if (json.result.code == 1020){
                        navigation.navigate("Search",{
                            accessToken:json.accessToken,
                            refreshToken:json.refreshToken
                        });
                    }
                })
                .catch((error) => {
                    alert("ERROR:\n"+JSON.stringify(error, null, 2));
                });
    }

    return (
        <View style={styles.container}>
            <TextInput
                style={styles.input}
                onChangeText={onChangeEmail}
                placeholder="Email"
                value={email}
            />
            <TextInput
                style={styles.input}
                onChangeText={onChangePassword}
                value={password}
                placeholder="Password"
            />
            <View style={styles.buttonContainer}>
                <Button
                    onPress = {
                        async () => {
                            submitLogin();
                        }
                    }
                    title="LOG IN"
                    color="#841584"
                />
            </View>
            <View style={styles.buttonContainer}>
                <Button
                    onPress = {() => navigation.navigate("Register")}
                    title="SIGN UP"
                    color="#841584"
                />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
    },
    buttonContainer: {
        margin: 20
    },
    input: {
        height: 40,
        margin: 12,
        borderWidth: 1,
        padding: 10,
    },
});

export default Login;