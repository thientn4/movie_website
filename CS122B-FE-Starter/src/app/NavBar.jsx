import React from "react";
import {NavLink} from "react-router-dom";
import styled from "styled-components";
import {LoginButton} from "../pages/Login";

const StyledNav = styled.nav`
  display: flex;

  height: 50px;
  padding: 5px;
  margin: 0;
  min-width: 1040px;

  background-color: #DB8C16;
  justify-content: space-between;
`;

const StyledNavLink = styled(NavLink)`
  padding: 10px;
  font-size: 25px;
  color: white;
  text-decoration: none;
  width:5vw;
`;

/**
 * To be able to navigate around the website we have these NavLink's (Notice
 * that they are "styled" NavLink's that are now named StyledNavLink)
 * <br>
 * Whenever you add a NavLink here make sure to add a corresponding Route in
 * the Content Component
 * <br>
 * You can add as many Link as you would like here to allow for better navigation
 * <br>
 * Below we have two Links:
 * <li>Home - A link that will change the url of the page to "/"
 * <li>Login - A link that will change the url of the page to "/login"
 */
const NavBar = () => {
    const loginStyle= {
        padding:"10px"
    }

    return (
        <StyledNav>
            <StyledNavLink style={{padding: "4px",paddingLeft:"10px"}} to="/">
                <h2><i>FabFlix</i></h2>
            </StyledNavLink>
            <div style={loginStyle}>
                <LoginButton></LoginButton>
                <StyledNavLink to="/search">
                    Search
                </StyledNavLink>
                <StyledNavLink to="/cart">
                    Cart
                </StyledNavLink>
            </div>
        </StyledNav>
    );
}
export default NavBar;
