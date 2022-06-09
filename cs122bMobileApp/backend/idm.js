import Config from "./config.json";

export async function login(loginRequest) {
    return await fetch('http://10.0.2.2:8083/login', {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email: loginRequest.email,
            password: loginRequest.password
        })
    })
}
export async function user_register(registerRequest) {
    return await fetch('http://10.0.2.2:8083/register', {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email: registerRequest.email,
            password: registerRequest.password
        })
    })
}
