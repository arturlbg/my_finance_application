import toastr from 'toastr'

toastr.options = {
    "closeButton": true,
    "debug": false,
    "newestOnTop": false,
    "progressBar": true,
    "positionClass": "toast-top-right",
    "preventDuplicates": false,
    "onclick": null,
    "showDuration": "300",
    "hideDuration": "1000",
    "timeOut": "5000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
  }

export function showMessage(title, message, type){
    toastr[type](message, title)
}

export function errorMessage(message){
    showMessage('Erro', message, 'error')
}

export function successMessage(message){
    showMessage('Success', message, 'success')
}

export function warningMessage(message){
    showMessage('Warning', message, 'warning')
}