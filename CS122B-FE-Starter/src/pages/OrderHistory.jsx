import React, {useEffect, useState} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {orderList} from "backend/billing";

const OrderHistory=()=>{
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();
    const [orders,setOrders]=useState([])
    useEffect(() => {
        orderList(accessToken).then(response => {
                if (response.data.sales !== undefined)
                    setOrders(response.data.sales);
            }
        )
    },[])
    const tableStyle={
        width: "300px",
        fontSize:"17px",
        tableLayout:"fixed",
        borderCollapse:"collapse"
    }
    const colStyle={
        textAlign:"center",
        fontSize:"17px",
        borderBottom: "grey 2px solid",
        height:"25px",
        width:"50%",
        padding:"3px"
    }
    const titleStyle={
        textAlign:"center",
        fontSize:"17px",
        height:"25px",
        width:"50%",
        padding:"3px",
        backgroundColor:"#DB8C16"
    }
    return (
        <div>
            <h1 style={{width:"300px",textAlign:"center"}}>Recent orders</h1>
            <br/>
            <table style={tableStyle}>
                <tbody>
                <tr>
                    <th style={titleStyle}>Date</th>
                    <th style={titleStyle}>Total</th>
                </tr>
                {orders.map((order) => (
                    <tr>
                        <td style={colStyle}>{order.orderDate.substring(0,10)}</td>
                        <td style={colStyle}>${order.total}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}
export default OrderHistory