import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router';
import IssueDelItem from './IssueDelItem';


export default class IssueRow extends Component {
  constructor() {
    super();
    this.state = {
      showId: false
    };
    this.displayId = this.displayId.bind(this);
    this.hideId = this.hideId.bind(this);
  }

  displayId() {
    this.setState({ showId: true })
  }

  hideId() {
    this.setState({ showId: false })
  }

  render() {
    // On récupère l'attribut issue dans les props histoire de factoriser
    const { issue } = this.props;
    let id;
    if (this.state.showId) {
      id = <td onClick={this.hideId}><Link to={`/issues/${issue.id}`}>{issue.id}</Link></td>;
    }
    else {
      id = <td onClick={this.displayId}><Link to={`/issues/${issue.id}`}>{issue.id.substring(0, 4)}</Link></td>
    }
    
    return (
      <tr>
        {id}
        <td>{issue.status}</td>
        <td>{issue.owner}</td>
        <td>{issue.created.toDateString()}</td>
        <td>{issue.effort}</td>
        <td>{issue.completionDate ? issue.completionDate.toDateString() : ''}</td>
        <td>{issue.title}</td>
        <td><IssueDelItem issue={issue.id} /></td>
      </tr>
    );
  }
}

IssueRow.propTypes = {
  issue: PropTypes.object.isRequired
};
