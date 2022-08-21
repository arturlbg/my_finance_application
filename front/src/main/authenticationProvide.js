import React from 'react'

import AuthService from '../app/service/authService'
import jwt from 'jsonwebtoken'

export const AuthContext = React.createContext()
export const AuthConsumer = AuthContext.Consumer;

const AuthProvider = AuthContext.Provider;

class AuthenticationProvide extends React.Component{

    state = {
        userAuthenticated: null,
        isAuthenticated: false,
        isLoading: true
    }

    startSection = (tokenDTO) => {
        const token = tokenDTO.token
        const claims = jwt.decode(token)
        const user = {
            id: claims.userid,
            name: claims.name
        }
        
        AuthService.login(user, token);
        this.setState({ isAuthenticated: true, userAuthenticated: user })
    }

    finishSection = () => {
        AuthService.removeUserAuthenticated();
        this.setState({ isAuthenticated: false, userAuthenticated: null})
    }

    async componentDidMount(){
        const isAuthenticated = AuthService.isUserAuthenticated()
        if(isAuthenticated){
            const user = await AuthService.refreshSection()
            this.setState({
                isAuthenticated: true,
                userAuthenticated: user,
                isLoading: false
            })
        }else{
            this.setState( previousState => {
                return {
                    ...previousState,
                    isLoading: false
                }
            })
        }
    }

    render(){

        if(this.state.isLoading){
            return null;
        }

        const context = {
            userAuthenticated: this.state.userAuthenticated,
            isAuthenticated: this.state.isAuthenticated,
            startSection: this.startSection,
            finishSection: this.finishSection
        }

        return(
            <AuthProvider value={context} >
                {this.props.children}
            </AuthProvider>
        )
    }
}

export default AuthenticationProvide;