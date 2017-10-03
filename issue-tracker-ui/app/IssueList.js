import React, { Component } from 'react';
import { Link } from 'react-router';
import { Button, Glyphicon, Table, Panel } from 'react-bootstrap';

import IssueAdd from './IssueAdd.js';
import IssueFilter from './IssueFilter.js';

// La version ES2015 d'un composant avec une arrow function,
// pas de return car on ne renvoi qu'une instruction unique.
const IssueRow = (props) => {
  function onDeleteClick() {
    props.deleteIssue(props.issue.id);
  }

  return (
    <tr>
      <td><Link to={`/issues/${props.issue.id}`}>{props.issue.id}</Link></td>
      <td>{props.issue.status}</td>
      <td>{props.issue.owner}</td>
      <td>{props.issue.created.toDateString()}</td>
      <td>{props.issue.effort}</td>
      <td>{props.issue.completionDate ? props.issue.completionDate.toDateString() : ''}</td>
      <td>{props.issue.title}</td>
      <td>
        <Button bsSize="xsmall" onClick={onDeleteClick}><Glyphicon glyph="trash" /></Button>
      </td>
    </tr>
  );
};

IssueRow.propTypes = {
  issue: React.PropTypes.object.isRequired,
  deleteIssue: React.PropTypes.func.isRequired,
};

// Un composant plus classique avec un return,
// indispensable lorsque la fonction comprend plus d'une instruction
function IssueTable(props) {
  const issueRows = props.issues.map(issue => <IssueRow key={issue.id} issue={issue} deleteIssue={props.deleteIssue}/>)
  return (
    <Table bordered condensed hover responsive>
      <thead>
        <tr>
          <th>Id</th>
          <th>Status</th>
          <th>Owner</th>
          <th>Created</th>
          <th>Effort</th>
          <th>Completion Date</th>
          <th>Title</th>
          <th></th>
        </tr>
      </thead>
      <tbody>{issueRows}</tbody>
    </Table>
  );
}

IssueTable.propTypes = {
  issues: React.PropTypes.array.isRequired,
  deleteIssue: React.PropTypes.func.isRequired,
};

export default class IssueList extends Component {
  // On définit le state dans le constructeur
  constructor() {
    super();
    // l'état initial est un tableau d'issues vide
    this.state = { issues: [] };
    this.createIssue = this.createIssue.bind(this);
    this.setFilter = this.setFilter.bind(this);
    this.deleteIssue = this.deleteIssue.bind(this);
  }

  // Méthode appelée lorsque la page est rechargée
  componentDidMount() {
    this.loadData();
  }

  // Méthode appelée lorsqu'une propriété du composant change
  // React Router parse la query string dans l'url et la rend disponible
  // au composant dans une propriété nommée 'location'
  componentDidUpdate(prevProps) {
    const oldQuery = prevProps.location.query;
    const newQuery = this.props.location.query;
    if (oldQuery.status === newQuery.status
        && oldQuery.effort_gte === newQuery.effort_gte
        && oldQuery.effort_lte === newQuery.effort_lte) {
      return;
    }
    this.loadData();
  }

  // Chargement des données
  loadData() {
    // on récupère les 'issues' depuis le backend
    console.log('Loading data from : ' + `/api/issues${this.props.location.search}`);
    fetch(`/api/issues${this.props.location.search}`).then(response =>
      response.json()
    ).then(data => {
      console.log("Nb d'enregistrements:", data._metadata.total_count);
      data.records.forEach(issue => {
        issue.created = new Date(issue.created);
        if (issue.completionDate){
          issue.completionDate = new Date(issue.completionDate);
        }
      });
      // le nouveau state contient les enregistrements récupérés
      this.setState({ issues: data.records });
    }).catch(err => {
      console.log(err);
    });
  }

  // Création et envoi au serveur d'une nouvelle 'issue'
  createIssue(newIssue) {
    fetch('/api/issues', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newIssue),
    }).then(response => {
      if (response.ok) {
        response.json().then(updatedIssue => {
          updatedIssue.created = new Date(updatedIssue.created);
          if (updatedIssue.completionDate) {
            updatedIssue.completionDate = new Date(updatedIssue.completionDate);
          }
          // le state est immuable, on utilise la fonction concat qui retourne
          // une copie de la liste à laquelle on ajoute un élément
          const newIssues = this.state.issues.concat(updatedIssue);
          this.setState({
            issues: newIssues
          });
        });
      } else {
        response.json().then(error => {
          alert("Failed to add issue: " + error.message)
        });
      }
    }).catch(err => {
      alert("Error in sending data to server: " + err.message);
    });
  }

  // la méthode setFilter prend en paramètre un objet query du type : { status: 'Open' }
  // on utilise la méthode push du router pour changer la query string en conservant le
  // pathname
  setFilter(query) {
    this.props.router.push({ pathname: this.props.location.pathname, query });
  }

  deleteIssue(id) {
    fetch(`/api/issues/${id}`, { method: 'DELETE' }).then(response => {
      if (!response.ok){
        alert('Failed to delete issue');
      } else {
        this.loadData();
      }
    });
  }

  render() {
    return (
      <div>
        <Panel collapsible header="Filter">
          <IssueFilter setFilter={this.setFilter} initFilter={this.props.location.query} />
        </Panel>
        <hr />
        <IssueTable issues={this.state.issues} deleteIssue={this.deleteIssue}/>
        <hr />
        <IssueAdd createIssue={this.createIssue}/>
      </div>
    );
  }
}

IssueList.propTypes = {
  location: React.PropTypes.object.isRequired,
  router: React.PropTypes.object,
};
