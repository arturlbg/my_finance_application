import ApiService from '../apiservice'

import ValidationError from '../exception/ValidationError'

class UserService extends ApiService {

    constructor() {
        super('/api/users')
    }

    authentic(credenciais) {
        return this.post('/authentic', credenciais)
    }

    getBalanceByUser(id) {
        return this.get(`/${id}/balance`);
    }

    save(user) {
        return this.post('', user);
    }

    validate(user) {
        const errors = []

        if (!user.name) {
            errors.push('The Name field is required.')
        }

        if (!user.email) {
            errors.push('The Email field is required.')
        } else if (!user.email.match(/^[a-z0-9.]+@[a-z0-9]+\.[a-z]/)) {
            errors.push('Enter a valid email.')
        }

        if (!user.password || !user.passwordRepetition) {
            errors.push('Enter the password twice.')
        } else if (user.password !== user.passwordRepetition) {
            errors.push('Passwords do not match.')
        }

        if (errors && errors.length > 0) {
            throw new ValidationError(errors);
        }
    }

}

export default UserService;