import React from 'react'
import currencyFormatter from 'currency-formatter'

export default props => {

    const rows = props.releases.map( release => {
        return (
            <tr key={release.id}>
                <td>{release.description}</td>
                <td>{ currencyFormatter.format(release.value, { locale: 'pt-BR'}) }</td>
                <td>{release.type}</td>
                <td>{release.month}</td>
                <td>{release.status}</td>
                <td>
                    <button className="btn btn-success" title="Active"
                            disabled={ release.status !== 'PENDING' }
                            onClick={e => props.changeStatus(release, 'ACTIVATED')} 
                            type="button">
                            <i className="pi pi-check"></i>
                    </button>
                    <button className="btn btn-warning"  title="Cancel"
                            disabled={ release.status !== 'PENDING' }
                            onClick={e => props.changeStatus(release, 'CANCELED')} 
                            type="button">
                            <i className="pi pi-eject"></i>
                    </button>
                    <button type="button"   title="Edit"
                            className="btn btn-primary"
                            onClick={e => props.editAction(release.id)}>
                            <i className="pi pi-pencil"></i>
                    </button>
                    <button type="button"  title="Delete"
                            className="btn btn-danger" 
                            onClick={ e => props.deleteAction(release)}>
                            <i className="pi pi-trash"></i>
                    </button>
                </td>
            </tr>
        )
    } )

    return (
        <table className="table table-hover">
            <thead>
                <tr>
                    <th scope="col">Description</th>
                    <th scope="col">Value</th>
                    <th scope="col">Type</th>
                    <th scope="col">Month</th>
                    <th scope="col">Situation</th>
                    <th scope="col">Actions</th>
                </tr>
            </thead>
            <tbody>
                {rows}
            </tbody>
        </table>
    )
}

