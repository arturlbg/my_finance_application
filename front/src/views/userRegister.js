import React from 'react'

import { withRouter } from 'react-router-dom'
import Card from '../components/card'
import FormGroup from '../components/form-group'

import UserService from '../app/service/userService'
import { successMessage, errorMessage } from '../components/toastr'

class UserRegister extends React.Component{

    state = {
        name : '',
        email: '', 
        password: '',
        passwordRepetition : ''
    }

    constructor(){
        super();
        this.service = new UserService();
    }

    register = () => {

        const {name, email, password, passwordRepetition } = this.state        
        const user = {name,  email, password, passwordRepetition }

        try{
            this.service.validate(user);
        }catch(error){
            const msgs = error.messages;
            msgs.forEach(msg => errorMessage(msg));
            return false;
        }

        this.service.save(user)
            .then( response => {
                successMessage('User registered successfully! Login to enter in platform.')
                this.props.history.push('/login')
            }).catch(error => {
                errorMessage(error.response.data)
            })
    }

    cancel = () => {
        this.props.history.push('/login')
    }

    render(){
        return (
            <Card title="User Register">
                <div className="row">
                    <div className="col-lg-12">
                        <div className="bs-component">
                            <FormGroup label="Name: *" htmlFor="inputname">
                                <input type="text" 
                                       id="inputname" 
                                       className="form-control"
                                       name="name"
                                       onChange={e => this.setState({name: e.target.value})} />
                            </FormGroup>
                            <FormGroup label="Email: *" htmlFor="inputEmail">
                                <input type="email" 
                                       id="inputEmail"
                                       className="form-control"
                                       name="email"
                                       onChange={e => this.setState({email: e.target.value})} />
                            </FormGroup>
                            <FormGroup label="Password: *" htmlFor="inputpassword">
                                <input type="password" 
                                       id="inputpassword"
                                       className="form-control"
                                       name="password"
                                       onChange={e => this.setState({password: e.target.value})} />
                            </FormGroup>
                            <FormGroup label="Repeat the password: *" htmlFor="inputRepeatpassword">
                                <input type="password" 
                                       id="inputRepeatpassword"
                                       className="form-control"
                                       name="password"
                                       onChange={e => this.setState({passwordRepetition: e.target.value})} />
                            </FormGroup>
                            <button onClick={this.register} type="button" className="btn btn-success">
                                <i className="pi pi-save"></i> Save
                            </button>
                            <button onClick={this.cancel} type="button" className="btn btn-danger">
                                <i className="pi pi-times"></i> Cancel
                            </button>
                        </div>
                    </div>
                </div>
            </Card>
        )
    }
}

export default withRouter(UserRegister)