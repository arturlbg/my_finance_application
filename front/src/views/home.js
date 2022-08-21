import React from 'react'

import UserService from '../app/service/userService'
import { AuthContext } from '../main/authenticationProvide'

class Home extends React.Component{

    state = {
        balance: 0
    }

    constructor(){
        super()
        this.UserService = new UserService();
    }

    componentDidMount(){
        const userLogged = this.context.userAuthenticated;

        this.UserService
            .getBalanceByUser(userLogged.id)
            .then( response => {
                this.setState({ balance: response.data})
            }).catch(error => {
                console.error(error.response)
            });
    }

    render(){
        return (
            <div className="jumbotron">
                <h1 className="display-3">Welcome!</h1>
                <p className="lead">This is your finance system.</p>
                <p className="lead">Your balance for the current month is R$ {this.state.balance} </p>
                <hr className="my-4" />
                <p>And this is your administrative area, use one of the menus or buttons below to navigate the system.</p>
                <p className="lead">
                    <a className="btn btn-primary btn-lg" 
                    href="/user-register" 
                    role="button"><i className="pi pi-users"></i>  
                     User Register
                    </a>
                    <a className="btn btn-danger btn-lg" 
                    href="/releases-register" 
                    role="button"><i className="pi pi-money-bill"></i>  
                     Release Register
                    </a>
                </p>
            </div>
        )
    }
}

Home.contextType = AuthContext;

export default Home