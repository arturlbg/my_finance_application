import React from 'react';

import Routes from './routes';
import Navbar from '../components/navbar';
import AuthenticationProvide from './authenticationProvide';

import 'toastr/build/toastr.min';

import 'bootswatch/dist/flatly/bootstrap.css';
import '../custom.css';
import 'toastr/build/toastr.css';

import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';

class App extends React.Component {

  render(){
    return(
      <AuthenticationProvide>
        <Navbar />
        <div className="container">    
            <Routes />
        </div>
      </AuthenticationProvide>
    )
  }
}

export default App