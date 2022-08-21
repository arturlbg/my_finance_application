import React from 'react'

import NavbarItem from './navbarItem'
import { AuthConsumer } from '../main/authenticationProvide'

function Navbar(props){
    return (
        <div className="navbar navbar-expand-lg fixed-top navbar-dark bg-primary">
        <div className="container">
          <a href="/home" className="navbar-brand">My finances</a>
          <button className="navbar-toggler" type="button" 
                  data-toggle="collapse" data-target="#navbarResponsive" 
                  aria-controls="navbarResponsive" aria-expanded="false" 
                  aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className="collapse navbar-collapse" id="navbarResponsive">
            <ul className="navbar-nav">
                <NavbarItem render={props.isUserAuthenticated} href="/home" label="Home" />
                <NavbarItem render={props.isUserAuthenticated} href="/user-register" label="Users" />
                <NavbarItem render={props.isUserAuthenticated} href="/releases-consult" label="Releases" />
                <NavbarItem render={props.isUserAuthenticated} onClick={props.logout} href="/login" label="Log out" />
            </ul>
            </div>
        </div>
      </div>
    )
}

export default () => (
  <AuthConsumer>
    {(context) => (
        <Navbar isUserAuthenticated={context.isAuthenticated} logout={context.finishSection} />
    )}
  </AuthConsumer>
)