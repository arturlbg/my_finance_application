import ApiService from '../apiservice'

import ValidationError from '../exception/ValidationError'

export default class ReleaseService extends ApiService {

    constructor(){
        super('/api/releases')
    }

    getMonthList(){
        return  [
            { label: 'Select...', value: '' },
            { label: 'January', value: 1 },
            { label: 'February', value: 2 },
            { label: 'March', value: 3 },
            { label: 'April', value: 4 },
            { label: 'May', value: 5 },
            { label: 'June', value: 6 },
            { label: 'July', value: 7 },
            { label: 'August', value: 8 },
            { label: 'September', value: 9 },
            { label: 'October', value: 10 },
            { label: 'November', value: 11 },
            { label: 'December', value: 12 },
        ]
    }

    getTypeList(){
        return  [
            { label: 'Select...', value: '' },
            { label: 'Expense' , value : 'EXPENSE' },
            { label: 'Revenue' , value : 'REVENUE' }
        ]

    }

    getById(id){
        return this.get(`/${id}`);
    }

    changeStatus(id, status){
        return this.put(`/${id}/update-status`, { status })
    }

    validate(release){
        const erros = [];

        if(!release.year){
            erros.push("Year is required.")
        }

        if(!release.month){
            erros.push("Month is required.")
        }

        if(!release.description){
            erros.push("Description is required.")
        }

        if(!release.value){
            erros.push("Value is required.")
        }

        if(!release.type){
            erros.push("Type is required.")
        }

        if(erros && erros.length > 0){
            throw new ValidationError(erros);
        }
    }

    save(release){
        return this.post('/', release);
    }

    update(release){
        return this.put(`/${release.id}`, release);
    }

    consult(releaseFilter){
        let params = `?year=${releaseFilter.year}`

        if(releaseFilter.month){
            params = `${params}&month=${releaseFilter.month}`
        }

        if(releaseFilter.type){
            params = `${params}&type=${releaseFilter.type}`
        }

        if(releaseFilter.status){
            params = `${params}&status=${releaseFilter.status}`
        }

        if(releaseFilter.user){
            params = `${params}&user=${releaseFilter.user}`
        }

        if(releaseFilter.description){
            params = `${params}&description=${releaseFilter.description}`
        }

        return this.get(params);
    }

    deleteById(id){
        return this.delete(`/${id}`)
    }
}