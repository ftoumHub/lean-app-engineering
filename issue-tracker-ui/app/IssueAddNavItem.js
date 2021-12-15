import React from 'react';
import { withRouter } from 'react-router';
import PropTypes from 'prop-types';
import { NavItem, Glyphicon, Modal, Form, FormGroup, FormControl, FormSelect, ControlLabel, Button, ButtonToolbar } from 'react-bootstrap';
import Toast from './Toast.js';

class IssueAddNavItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showing: false,
            toastVisible: false, toastMessage: '', toastType: 'success',
            error: false
        };
        this.showModal = this.showModal.bind(this);
        this.hideModal = this.hideModal.bind(this);
        this.submit = this.submit.bind(this);
        this.displayError = this.displayError.bind(this);
        this.hideError = this.hideError.bind(this);
        this.showError = this.showError.bind(this);
        this.dismissToast = this.dismissToast.bind(this);
    }

    showModal() {
        this.setState({ showing: true });
    }

    hideModal() {
        this.setState({ showing: false });
    }

    showError(message) {
        this.setState({
            toastVisible: true, toastMessage: message,
            toastType: 'danger'
        });
    }

    dismissToast() {
        this.setState({ toastVisible: false });
    }

    displayError() {
        this.setState({ error: true });
    }

    hideError() {
        this.setState({ error: false });
    }

    submit(e) {
        e.preventDefault();
        const form = document.forms.issueAdd;
        const newIssue = {
            owner: form.owner.value, title: form.title.value,
            status: 'NEW', created: new Date(),
        };
        if (form.title.value !== "") {
            this.hideModal();
            fetch('/api/issues', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newIssue),
            }).then(response => {
                if (response.ok) {
                    window.location.reload(false);
                } else {
                    response.json().then(error => {
                        this.showError(`Failed to add issue: ${error.message}`);
                    });
                }
            }).catch(err => {
                this.showError(`Error in sending data to server: ${err.message}`);
            });
        }
        else {
            this.displayError();
        }
    }

    render() {
        let button;
        if (this.state.error) {
            button = <ControlLabel style={{color:"red"}}>The field title is missing</ControlLabel>;
        }
        else {
            button = null;
        }
        return (
            <NavItem onClick={this.showModal}><Glyphicon glyph="plus" /> Create Issue
                <Modal keyboard show={this.state.showing} onHide={this.hideModal}>
                    <Modal.Header closeButton>
                        <Modal.Title>Create Issue</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form name="issueAdd">
                            <FormGroup>
                                <ControlLabel>Title</ControlLabel>
                                <FormControl name="title" autoFocus required />
                                {button}
                            </FormGroup>
                            <FormGroup>
                                <ControlLabel>Owner</ControlLabel>
                                <FormControl name="owner" />
                            </FormGroup>
                        </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <ButtonToolbar>
                            <Button type="button" bsStyle="primary" onClick={this.submit}>Submit</Button>
                            <Button bsStyle="link" onClick={this.hideModal}>Cancel</Button>
                        </ButtonToolbar>
                    </Modal.Footer>
                </Modal>
            </NavItem >
        );
    }
}
IssueAddNavItem.propTypes = {
    router: PropTypes.object,
};
export default withRouter(IssueAddNavItem);