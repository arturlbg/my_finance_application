import React from 'react'

import Login from '../views/login'
import Home from '../views/home'
import UserRegister from '../views/userRegister'
import ReleasesConsult from '../views/releases/releases-consult'
import ReleasesRegister from '../views/releases/releases-register'
import LandingPage from '../views/landingPage'
import { AuthConsumer } from '../main/authenticationProvide'

import { Route, Switch, BrowserRouter, Redirect } from 'react-router-dom'

function AuthenticatedRoute( { component: Component, isUserAuthenticated, ...props } ){
    return (
        <Route exact {...props} render={ (componentProps) => {
            if(isUserAuthenticated){
                return (
                    <Component {...componentProps} />
                )
            }else{
                return(
                    <Redirect to={ {pathname : '/login', state : { from: componentProps.location } } } />
                )
            }
        }}  />
    )
}

function Routes(props){
    return (
        <BrowserRouter>
            <Switch>
                <Route exact path="/" component={LandingPage} />
                <Route exact path="/login" component={Login} />
                <Route exact path="/user-register" component={UserRegister} />
                
                <AuthenticatedRoute isUserAuthenticated={props.isUserAuthenticated} path="/home" component={Home} />
                <AuthenticatedRoute isUserAuthenticated={props.isUserAuthenticated} path="/releases-consult" component={ReleasesConsult} />
                <AuthenticatedRoute isUserAuthenticated={props.isUserAuthenticated} path="/releases-register/:id?" component={ReleasesRegister} />
            </Switch>
        </BrowserRouter>
    )
}

export default () => (
    <AuthConsumer>
        { (context) => (<Routes isUserAuthenticated={context.isAuthenticated} />) }
    </AuthConsumer>
)