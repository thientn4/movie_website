import React, { Component } from 'react';
import { Button, TextInput, StyleSheet, View } from 'react-native';
import {user_register} from "../backend/idm"

const Register = ( {navigation} ) => {
    const [email, onChangeEmail] = React.useState(null);
    const [password, onChangePassword] = React.useState(null);
    const submitRegister = () => {
        const payLoad = {
            email: email,
            password: password.split('')
        }
        user_register(payLoad)
            .then((response) => response.json() )
                .then((json) => {
                    alert(json.result.message);
                    if (json.result.code == 1010){
                        navigation.goBack(null);
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
                            submitRegister();
                        }
                    }
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

export default Register;