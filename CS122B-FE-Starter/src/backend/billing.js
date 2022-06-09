import Config from "backend/config.json";
import Axios from "axios";

export async function cartInsert(movieId,quantity,accessToken) {
    const options = {
        method: "POST",
        baseURL: Config.billingUrl,
        url: Config.billing.cart_insert,
        data: {
            movieId: movieId,
            quantity: quantity
        },
        headers:{
            Authorization: "Bearer "+accessToken,
        }
    }

    return Axios.request(options);
}
export async function cartRetrieve(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.billingUrl,
        url: Config.billing.cart_retrieve,
        headers:{
            Authorization: "Bearer "+accessToken,
        }
    }

    return Axios.request(options);
}

export async function cartUpdate(accessToken, movieId, quantity) {
    const options = {
        method: "POST",
        baseURL: Config.billingUrl,
        url: Config.billing.cart_update,
        data:{
            movieId: movieId,
            quantity: quantity
        },
        headers:{
            Authorization: "Bearer "+accessToken,
        }
    }

    return Axios.request(options);
}

export async function cartDelete(accessToken,movieId) {
    const options = {
        method: "DELETE",
        baseURL: Config.billingUrl,
        url: Config.billing.cart_delete+movieId,
        headers:{
            Authorization: "Bearer "+accessToken,
        }
    }

    return Axios.request(options);
}
export async function orderPayment(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.billingUrl,
        url: Config.billing.order_payment,
        headers:{
            Authorization: "Bearer "+accessToken,
        }
    }

    return Axios.request(options);
}
export async function orderComplete(accessToken,paymentIntentId) {
    const options = {
        method: "POST",
        baseURL: Config.billingUrl,
        url: Config.billing.order_complete,
        data: {
            paymentIntentId: paymentIntentId
        },
        headers:{
            Authorization: "Bearer "+accessToken,
        }
    }

    return Axios.request(options);
}
export async function orderList(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.billingUrl,
        url: Config.billing.order_list,
        headers:{
            Authorization: "Bearer "+accessToken,
        }
    }

    return Axios.request(options);
}