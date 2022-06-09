import React,{useState} from "react";
import {useUser} from "../hook/User";
import {useForm} from "react-hook-form";
import {cartRetrieve} from "../backend/billing";
import {cartUpdate} from "../backend/billing";
import {cartDelete} from "../backend/billing";
import {Link, useNavigate} from "react-router-dom";

const Cart=()=>{
    const{
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();
    const navigate = useNavigate()
    const {register, getValues, handleSubmit} = useForm();
    const [items,setItems]=useState([]);
    const [total,setTotal]=useState(0);
    React.useEffect(()=>{
        cartRetrieve(accessToken).then(
            response => {
                if(response.data.result.code==3040) {
                    setItems(response.data.items)
                    setTotal(response.data.total)
                }
            }
        )
    },[])
    const updateCart=(quantity,movieId)=>{
        if(quantity==0){
            cartDelete(accessToken,movieId).then(
                response=>{
                    alert(JSON.stringify(response.data.result.message, null, 2));
                    cartRetrieve(accessToken).then(
                        response => {
                            if(response.data.result.code===3040) {
                                setItems(response.data.items)
                                setTotal(response.data.total)
                            }else{
                                setItems([])
                                setTotal(0)
                            }
                        }
                    ).catch(error => alert(error.response.data))
                }
            ).catch(error => alert(error.response.data))
        }else{
            cartUpdate(accessToken,movieId,quantity).then(
                response=>{
                    alert(JSON.stringify(response.data.result.message, null, 2));
                    cartRetrieve(accessToken).then(
                        response => {
                            if(response.data.result.code===3040) {
                                setItems(response.data.items)
                                setTotal(response.data.total)
                            }else{
                                setItems([])
                                setTotal(0)
                            }
                        }
                    ).catch(error => alert(error.response.data))
                }
            ).catch(error => alert(error.response.data))
        }
    }
    const cartTagStyle={
        display:"flex",
        flexDirection: "column",
        justifyContent:"space-between",
        width:"400px",
        borderBottom:"solid white"
    }
    const itemStyle={
        display:"flex",
        flexDirection: "row",
        justifyContent:"space-between",
        width:"400px",
        borderBottom:"solid white"
    }
    const posterStyle={
        width:"150px",
        height:"225px"
    }
    const itemDetailsStyle={
        width:"200px",
        display:"flex",
        flexDirection: "column",
        justifyContent:"space-between",
    }
    const itemDetailStyle={
        display:"flex",
        flexDirection: "row",
        justifyContent:"space-between",
        fontSize:"20px",
        width:"140px"
    }
    const buttonsStyle={
        display:"flex",
        flexDirection: "row",
        justifyContent:"space-between",
        fontSize:"20px",
        width:"170px"
    }
    const buttonStyle= {
        background: "#DB8C16",
        border: "none",
        height: "30px",
        width: "80px",
        fontSize: "18px",
        color: "white",
        borderRadius: "5px",
    }
    const totalStyle={
        display:"flex",
        flexDirection: "row",
        justifyContent:"space-around"
    }
    const checkoutStyle={
        background: "#DB8C16",
        border: "none",
        height: "30px",
        width: "150px",
        fontSize: "18px",
        color: "white",
        borderRadius: "5px",
    }

    if(accessToken==null)return(
        <div>
            <h1>Please login first</h1>
        </div>
    )

    return (
        <div>
            <div style={cartTagStyle}>
                <div style={totalStyle}>
                    <h1>CART</h1>
                </div>
                <br></br>
            </div>
            <br></br>
            {items.map((item) => (
                <div>
                    <div style={itemStyle}>
                        <div>
                            <img style={posterStyle}
                                 src={"https://image.tmdb.org/t/p/original/"+item.posterPath}
                            ></img>
                            <br></br>
                            <br></br>
                        </div>
                        <div style={itemDetailsStyle}>
                            <h2>{item.movieTitle}</h2>
                            <div style={itemDetailStyle}>
                                <div>price:</div>
                                <div>${Math.round(item.unitPrice*item.quantity*100)/100}</div>
                            </div>
                            <div style={itemDetailStyle}>
                                <div>quantity:</div>
                                <select style={{width:"50px"}} {...register("quantity"+item.movieId)}>
                                    {[...Array(11).keys()].map((quantity) => (
                                        quantity===item.quantity?
                                            (<option value={quantity} selected>{quantity}</option>)
                                        :
                                            (<option value={quantity}>{quantity}</option>)
                                    ))}
                                </select>
                            </div>
                            <div style={buttonsStyle}>
                                <button onClick={handleSubmit(()=>{updateCart(getValues("quantity"+item.movieId),item.movieId)})} style={buttonStyle}>Update</button>
                                <button onClick={handleSubmit(()=>{updateCart(0,item.movieId)})} style={buttonStyle}>Remove</button>
                            </div>
                            <br></br>
                        </div>
                    </div>
                    <br></br>
                </div>
            ))}
            <div style={totalStyle}>
                <h1>Total: ${total}</h1>
            </div>
            <br></br>
            <div style={totalStyle}>
                <button onClick={()=>{if(items.length!=0)navigate("../checkout")}} style={checkoutStyle}>Checkout</button>
            </div>
            <br></br>
            <div style={totalStyle}>
                <Link to={"/history"} style={{color:"orange"}}>
                    Order history
                </Link>
            </div>
        </div>
    )
}
export default Cart;