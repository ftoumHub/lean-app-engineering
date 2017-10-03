import React from 'react';
import { Link } from 'react-router';

import NumInput from './NumInput.js';
import DateInput from './DateInput.js';

export default class IssueEdit extends React.Component {

  constructor() {
    super();
    this.state = {
      issue: {
        _id: '',
        title: '',
        status: '',
        owner: '',
        effort: null,
        completionDate: null,
        created: null
      },
      invalidFields: {},
    };
    this.onChange = this.onChange.bind(this);
    this.onValidityChange = this.onValidityChange.bind(this);
    this.onSubmit = this.onSubmit.bind(this);
  }

  componentDidMount() {
    this.loadData();
  }

  componentDidUpdate(prevProps) {
    if (prevProps.params.id !== this.props.params.id) {
      this.loadData();
    }
  }

  onSubmit(event) {
    console.log('==> Soumission de la mise à jour.');

    event.preventDefault();
    if (Object.keys(this.state.invalidFields).length !== 0) {
      return;
    }
    fetch(`/api/issues/${this.props.params.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(this.state.issue),
    }).then(response => {
      if (response.ok) {
        response.json().then(updatedIssue => {
          updatedIssue.created = new Date(updatedIssue.created);
          if (updatedIssue.completionDate) {
            updatedIssue.completionDate = new Date(updatedIssue.
                completionDate);
          }
          this.setState({ issue: updatedIssue });
          alert('Updated issue successfully.');
        });
      } else {
        response.json().then(error => {
          alert(`Failed to update issue: ${error.message}`);
        });
      }
    }).catch(err => {
      alert(`Error in sending data to server: ${err.message}`);
    });
  }

  onChange(event, convertedValue) {
  	console.log('==> onChange dans IssueEdit');
    const issue = Object.assign({}, this.state.issue);
    // On utilise le nom de chaque input comme une clé dans l'objet state
    // afin d'affecter la nouvelle valeur de la propriété modifiée.
    // Cette technique permet de combiner tous onChange de chaque input
    // dans une seule méthode.
    //issue[event.target.name] = event.target.value;
    const value = (convertedValue !== undefined) ? convertedValue : event.target.value;
    issue[event.target.name] = value;
    this.setState({ issue });
  }

  onValidityChange(event, valid) {
    const invalidFields = Object.assign({}, this.state.invalidFields);
    if (!valid) {
      invalidFields[event.target.name] = true;
    } else {
      delete invalidFields[event.target.name];
    }
    this.setState({ invalidFields });
  }

  loadData() {
  	console.log('Loading data from : ' + `/api/issues/${this.props.params.id}`);

    fetch(`/api/issues/${this.props.params.id}`).then(response => {
      if (response.ok) {
      	// On convertit chaque attribut en string ce qui est requis par le html.
        response.json().then(issue => {
          issue.created = new Date(issue.created);
          issue.completionDate = issue.completionDate != null ?
            new Date(issue.completionDate) : null;
          //issue.effort = issue.effort != null ? issue.effort.toString() : '';
          // On créé un nouveau state
          this.setState({ issue });
        });
      } else {
        response.json().then(error => {
          alert(`Failed to fetch issue: ${error.message}`);
        });
      }
    }).catch(err => {
      alert(`Error in fetching data from server: ${err.message}`);
    });
  }

  render() {
    const issue = this.state.issue;
    const validationMessage = Object.keys(this.state.invalidFields).length === 0 ? null
      : (<div className="error">Please correct invalid fields before submitting.</div>);
    return (
      <div>
        <form onSubmit={this.onSubmit}>
          Created: {issue.created ? issue.created.toDateString() : ''}
          <br />
          Status: <select name="status" value={issue.status} onChange={this.onChange}>
            <option value="NEW">New</option>
            <option value="OPEN">Open</option>
            <option value="ASSIGNED">Assigned</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="DONE">Done</option>
          </select>
          <br />
          Owner: <input name="owner" value={issue.owner} onChange={this.onChange} />
          <br />
          Effort: <NumInput size={5} name="effort" value={issue.effort} onChange={this.onChange} />
          <br />
          Completion Date: <DateInput
            name="completionDate" value={issue.completionDate} onChange={this.onChange}
            onValidityChange={this.onValidityChange}
          />
          <br />
          Title: <input name="title" size={50} value={issue.title} onChange={this.onChange} />
          <br />
          {validationMessage}
          <button type="submit">Submit</button>
          <Link to="/issues">Back to issue list</Link>
        </form>
      </div>
    );
  }
}

IssueEdit.propTypes = {
  params: React.PropTypes.object.isRequired,
};
