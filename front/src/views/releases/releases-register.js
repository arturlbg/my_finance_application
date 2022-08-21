import React from 'react'

import Card from '../../components/card'
import FormGroup from '../../components/form-group'
import SelectMenu from '../../components/selectMenu'

import { withRouter } from 'react-router-dom'
import * as messages from '../../components/toastr'

import ReleaseService from '../../app/service/releaseService'
import LocalStorageService from '../../app/service/localstorageService'

class ReleasesRegister extends React.Component {

    state = {
        id: null,
        description: '',
        value: '',
        month: '',
        year: '',
        type: '',
        status: '',
        user: null,
        updating: false
    }

    constructor(){
        super();
        this.service = new ReleaseService();
    }

    componentDidMount(){
        const params = this.props.match.params
       
        if(params.id){
            this.service
                .getById(params.id)
                .then(response => {
                    this.setState( {...response.data, updating: true} )
                })
                .catch(errors => {
                    messages.errorMessage(errors.response.data)
                })
        }
    }

    submit = () => {
        const userLogged = LocalStorageService.getItem('_user_logged')

        const { description, value, month, year, type } = this.state;
        const release = { description, value, month, year, type, user: userLogged.id };

        try{
            this.service.validate(release)
        }catch(error){
            const messages = error.messages;
            messages.forEach(msg => messages.errorMessage(msg));
            return false;
        }     

        this.service
            .save(release)
            .then(response => {
                this.props.history.push('/releases-consult')
                messages.successMessage('Release successfully registered!')
            }).catch(error => {
                messages.errorMessage(error.response.data)
            })
    }

    update = () => {
        const { description, value, month, year, type, status, user, id } = this.state;

        const release = { description, value, month, year, type, user, status, id };
        
        this.service
            .update(release)
            .then(response => {
                this.props.history.push('/releases-consult')
                messages.successMessage('Release successfully changed!')
            }).catch(error => {
                messages.errorMessage(error.response.data)
            })
    }

    handleChange = (event) => {
        const value = event.target.value;
        const name = event.target.name;

        this.setState({ [name] : value })
    }

    render(){
        const types = this.service.getTypeList();
        const months = this.service.getMonthList();

        return (
            <Card title={ this.state.updating ? 'Release update'  : 'Release register' }>
                <div className="row">
                    <div className="col-md-12">
                        <FormGroup id="inputdescription" label="Description: *" >
                            <input id="inputdescription" type="text" 
                                   className="form-control" 
                                   name="description"
                                   value={this.state.description}
                                   onChange={this.handleChange}  />
                        </FormGroup>
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6">
                        <FormGroup id="inputyear" label="Year: *">
                            <input id="inputyear" 
                                   type="text"
                                   name="year"
                                   value={this.state.year}
                                   onChange={this.handleChange} 
                                   className="form-control" />
                        </FormGroup>
                    </div>
                    <div className="col-md-6">
                        <FormGroup id="inputmonth" label="Mês: *">
                            <SelectMenu id="inputmonth" 
                                        value={this.state.month}
                                        onChange={this.handleChange}
                                        list={months} 
                                        name="month"
                                        className="form-control" />
                        </FormGroup>
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-4">
                         <FormGroup id="inputvalue" label="Value: *">
                            <input id="inputvalue" 
                                   type="text"
                                   name="value"
                                   value={this.state.value}
                                   onChange={this.handleChange} 
                                   className="form-control" />
                        </FormGroup>
                    </div>

                    <div className="col-md-4">
                         <FormGroup id="inputtype" label="type: *">
                            <SelectMenu id="inputtype" 
                                        list={types} 
                                        name="type"
                                        value={this.state.type}
                                        onChange={this.handleChange}
                                        className="form-control" />
                        </FormGroup>
                    </div>

                    <div className="col-md-4">
                         <FormGroup id="inputStatus" label="Status: ">
                            <input type="text" 
                                   className="form-control" 
                                   name="status"
                                   value={this.state.status}
                                   disabled />
                        </FormGroup>
                    </div>

                   
                </div>
                <div className="row">
                     <div className="col-md-6" >
                        { this.state.updating ? 
                            (
                                <button onClick={this.update} 
                                        className="btn btn-success">
                                        <i className="pi pi-refresh"></i> Update
                                </button>
                            ) : (
                                <button onClick={this.submit} 
                                        className="btn btn-success">
                                        <i className="pi pi-save"></i> Save
                                </button>
                            )
                        }
                        <button onClick={e => this.props.history.push('/releases-consult')} 
                                className="btn btn-danger">
                                <i className="pi pi-timonth"></i>Cancel
                        </button>
                    </div>
                </div>
            </Card>
        )
    }
}

export default withRouter(ReleasesRegister);