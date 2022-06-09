import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { NavigationContainer } from '@react-navigation/native';

import Login from './pages/Login';
import Register from './pages/Register';
import Search from './pages/Search';
import MovieDetails from './pages/MovieDetails';

//10.0.2.2 for mobile FE
//127.0.0.1 for BE

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

const Stack=createNativeStackNavigator();
const MyStack = () => {
  return (
      <NavigationContainer>
        <Stack.Navigator>
          <Stack.Screen
              name="Login"
              component={Login}
              options={{ title: 'Login' }}
          />
          <Stack.Screen
              name="Register"
              component={Register}
              options={{ title: 'Register' }}
          />
          <Stack.Screen
              name="Search"
              component={Search}
              options={{ title: 'Search' }}
          />
          <Stack.Screen
              name="MovieDetails"
              component={MovieDetails}
              options={{ title: 'Movie Details' }}
          />
        </Stack.Navigator>
      </NavigationContainer>
  );
};

export default MyStack;
