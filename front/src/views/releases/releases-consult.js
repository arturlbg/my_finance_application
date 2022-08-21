import React from 'react'
import { withRouter } from 'react-router-dom'

import LocalStorageService from '../../app/service/localstorageService'
import ReleaseService from '../../app/service/releaseService'
import Card from '../../components/card'
import FormGroup from '../../components/form-group'
import SelectMenu from '../../components/selectMenu'
import ReleasesTable from './releasesTable'

import * as messages from '../../components/toastr'

import { Button } from 'primereact/button'
import { Dialog } from 'primereact/dialog'

class ReleasesConsult extends React.Component {

    state = {
        year: '',
        month: '',
        type: '',
        description: '',
        showConfirmDialog: false,
        releaseDelete: {},
        releases : []
    }

    constructor(){
        super();
        this.service = new ReleaseService();
    }

    search = () => {
        if(!this.state.year){
            messages.errorMessage('Year field is required.');
            return false;
        }

        const userLogged = LocalStorageService.getItem('_user_logged');

        const releaseFilter = {
            year: this.state.year,
            month: this.state.month,
            type: this.state.type,
            description: this.state.description,
            user: userLogged.id
        }

        this.service
            .consult(releaseFilter)
            .then( response => {
                const list = response.data;
                
                if(list.length < 1){
                    messages.warningMessage("No result found.");
                }
                this.setState({ releases: list })
            }).catch( error => {
                console.log(error)
            })
    }

    edit = (id) => {
        this.props.history.push(`/releases-register/${id}`)
    }

    openVerification = (release) => {
        this.setState({ showConfirmDialog : true, releaseDelete: release  })
    }

    cancelDelete = () => {
        this.setState({ showConfirmDialog : false, releaseDelete: {}  })
    }

    delete = () => {
        this.setState( { showConfirmDialog: false } )
        this.service
            .deleteById(this.state.releaseDelete.id)
            .then(response => {
                const releases = this.state.releases;
                const index = releases.indexOf(this.state.releaseDelete)
                releases.splice(index, 1);
                this.setState( { releases: releases } )
                messages.successMessage('Release successfully deleted!')
            }).catch(error => {
                messages.errorMessage('There was an error deleting the release.')
            })
    }

    launchRegisterForm = () => {
        this.props.history.push('/releases-register')
    }

    changeStatus = (release, status) => {
        this.service
            .changeStatus(release.id, status)
            .then( response => {
                const releases = this.state.releases;
                const index = releases.indexOf(release);
                if(index !== -1){
                    release['status'] = status;
                    releases[index] = release;
                    this.setState({release});
                }
                messages.successMessage("Status successfully changed!")
            })
    }

    render(){
        const months = this.service.getMonthList();
        const types = this.service.getTypeList();

        const confirmDialogFooter = (
            <div>
                <Button label="Confirm" icon="pi pi-check" onClick={this.delete} />
                <Button label="Cancel" icon="pi pi-eject" onClick={this.cancelDelete} 
                        className="p-button-secondary" />
            </div>
        );

        return (
            <Card title="Consult Releases">
                <div className="row">
                    <div className="col-md-6">
                        <div className="bs-component">
                            <FormGroup htmlFor="inputyear" label="Year: *">
                                <input type="text" 
                                       className="form-control" 
                                       id="inputyear" 
                                       value={this.state.year}
                                       onChange={e => this.setState({year: e.target.value})}
                                       placeholder="Type the year" />
                            </FormGroup>

                            <FormGroup htmlFor="inputmonth" label="Month: ">
                                <SelectMenu id="inputmonth" 
                                            value={this.state.month}
                                            onChange={e => this.setState({ month: e.target.value })}
                                            className="form-control" 
                                            list={months} />
                            </FormGroup>

                            <FormGroup htmlFor="inputDesc" label="Description: ">
                                <input type="text" 
                                       className="form-control" 
                                       id="inputDesc" 
                                       value={this.state.description}
                                       onChange={e => this.setState({description: e.target.value})}
                                       placeholder="Type the description" />
                            </FormGroup>

                            <FormGroup htmlFor="inputtype" label="Type: ">
                                <SelectMenu id="inputtype" 
                                            value={this.state.type}
                                            onChange={e => this.setState({ type: e.target.value })}
                                            className="form-control" 
                                            list={types} />
                            </FormGroup>

                            <button onClick={this.search} 
                                    type="button" 
                                    className="btn btn-success">
                                    <i className="pi pi-search"></i> Search
                            </button>
                            <button onClick={this.launchRegisterForm} 
                                    type="button" 
                                    className="btn btn-danger">
                                    <i className="pi pi-plus"></i> Register
                            </button>

                        </div>
                        
                    </div>
                </div>   
                <br/>
                <div className="row">
                    <div className="col-md-12">
                        <div className="bs-component">
                            <ReleasesTable releases={this.state.releases} 
                                              deleteAction={this.openVerification}
                                              editAction={this.edit}
                                              changeStatus={this.changeStatus} />
                        </div>
                    </div>  
                </div> 
                <div>
                    <Dialog header="Confirmation" 
                            visible={this.state.showConfirmDialog} 
                            style={{width: '50vw'}}
                            footer={confirmDialogFooter} 
                            modal={false} 
                            onHide={() => this.setState({showConfirmDialog: false})}>
                        Confirm the release delete?
                    </Dialog>
                </div>           
            </Card>

        )
    }
}

export default withRouter(ReleasesConsult);