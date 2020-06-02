// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', () => {
  cy.get('[data-cy="error"]')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('createTournaments', (numQ) => {
  cy.contains('Tournaments').click();
  cy.get('[data-cy="createButton"]').click();
  cy.wait(5000);
  cy.get('[data-cy="numQuest"]').type(numQ);
  cy.get('[data-cy="topicSelect"]').click({force: true});
  cy.contains('Adventure').click();
  cy.get('#startDateInput-input').click({force: true}).type('{rightarrow}{enter}', {force: true});
  cy.get('#endDateInput-input').click({force: true}).type('{rightarrow}{rightarrow}{enter}', {force: true});
  cy.get('[data-cy="save"]').click({force: true});
});

Cypress.Commands.add('cancelTournaments', studentName => {
  cy.contains('Tournaments').click();
  cy.contains(studentName)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="cancelTournament"]')
    .click();
});

Cypress.Commands.add('enrollTournament', id => {
  cy.contains('Tournaments').click();
  cy.contains('All Tournaments').click();
  cy.contains(id)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="enrollTournament"]')
    .click();
  cy.contains('Tournaments').click();
  cy.contains('Enrolled Tournaments').click();
  cy.contains(id);
});

Cypress.Commands.add('answerTournament', id => {
  cy.contains('Tournaments').click();
  cy.contains('Enrolled Tournaments').click();
  cy.contains(id)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="answerTournament"]')
    .click();
  cy.wait(5000);
  for (let i = 0; i < 5; i++) {
    cy.get('.option')
      .first()
      .click();
    cy.get('div.square').click();
  }
  cy.get('.end-quiz').click();
  cy.contains('I\'m sure').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
});

Cypress.Commands.add('goToDiscussion', () => {
  cy.contains('Management').click();
  cy.contains('Discussion').click();
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
});

Cypress.Commands.add('solveQuiz', () => {
  cy.contains('QUIZZES').click();
  cy.contains('Create').click();
  cy.contains('Create quiz').click();
  for (let i = 0; i < 5; i++) {
    cy.get('.option')
      .first()
      .click();
    cy.get('div.square').click();
  }
  cy.get('.end-quiz').click();
  cy.contains('I\'m sure').click();
});

Cypress.Commands.add('createClarificationRequest', content => {
  cy.get('[data-cy="createClarificationButton"]').click();
  cy.get('[data-cy="Content"]').type(content);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('createClarificationRequestAnswer', (content) => {
  cy.get('[data-cy="answerButton"]').click({force: true});
  cy.get('[data-cy="Content"]').type(content, {force: true});
  cy.get('[data-cy="saveButton"]').click({force: true});
});

Cypress.Commands.add('closeClarificationRequest', () => {
  cy.get('[data-cy="closeButton"]').click({force: true});
});

Cypress.Commands.add('seeClarificationRequest', () => {
  cy.get('[data-cy="seeClarificationButton"]').click();
});

Cypress.Commands.add('listClarificationRequest', content => {
  cy.contains('Discussion').click();
  cy.get('[data-cy="Search"]').type(content);
  cy.contains(content);
});

Cypress.Commands.add(
  'createQuestionSuggestion',
  (title, question, op0, op1, op2, op3, flag) => {
    cy.contains('New Suggestion').click({ force: true });
    if (title !== '') {
      cy.get('[data-cy="Title"]').type(title, { force: true });
    }
    if (question !== '') {
      cy.get('[data-cy="Content"]').type(question, { force: true });
    }
    cy.get('[data-cy="Option"]')
      .eq(0)
      .type(op0, { force: true });
    cy.get('[data-cy="Option"]')
      .eq(1)
      .type(op1, { force: true });
    cy.get('[data-cy="Option"]')
      .eq(2)
      .type(op2, { force: true });
    if (op3 !== '') {
      cy.get('[data-cy="Option"]')
        .eq(3)
        .type(op3, { force: true });
    }
    if (flag === 'No') {
      cy.get('[data-cy="Correct"]')
        .eq(3)
        .click({ force: true });
    }
    cy.get('[data-cy="saveButton"]').click({ force: true });
  }
);

Cypress.Commands.add('showQuestionSuggestion', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="showSuggestion"]')
    .click();
  cy.get('[data-cy="closeButton"]').click({ force: true });
});

Cypress.Commands.add('showQuestionFromSuggestion', (title) =>{
  cy.contains(title)
    .parent()
    .parent()
    .find('[data-cy="questionShowButton"]')
    .click({ force: true });
  cy.wait(500);
  cy.get('[data-cy="questionCloseButton"]').click({ force: true });
});

Cypress.Commands.add('acceptQuestionSuggestion', title => {
  cy.contains(title)
    .parent()
    .children()
    .should('have.length', 5)
    .find('[data-cy="acceptButton"]')
    .click({ force: true });
});

Cypress.Commands.add('acceptQuestionSuggestionShow', title => {
  cy.contains(title)
    .parent()
    .children()
    .should('have.length', 5)
    .find('[data-cy="showButton"]')
    .click({ force: true });
  cy.get('[data-cy="acceptQuestion"]').click({ force: true });
});

Cypress.Commands.add('rejectQuestionSuggestion', (title, text) => {
  cy.contains(title)
    .parent()
    .children()
    .should('have.length', 5)
    .find('[data-cy="rejectButton"]')
    .click({ force: true });
  cy.get('[data-cy="justification_text"]').type(text);
  cy.get('[data-cy="saveJustification"]').click({ force: true });
});

Cypress.Commands.add('rejectQuestionSuggestionShow', (title, text) => {
  cy.contains(title)
    .parent()
    .children()
    .should('have.length', 5)
    .find('[data-cy="showButton"]')
    .click({ force: true });
  cy.get('[data-cy="rejectQuestion"]').click({ force: true });
  cy.get('[data-cy="justification_text"]').type(text);
  cy.get('[data-cy="saveJustification"]').click({ force: true });
});

Cypress.Commands.add(
  'updateRejectedQuestionSuggestion',
  (title, newTitle, question, op0, op1, op2, op3) => {
    cy.contains(title)
      .parent()
      .children()
      .should('have.length', 5)
      .find('[data-cy="updateRejectedQuestion"]')
      .click({ force: true });
    if (newTitle !== '') {
      cy.get('[data-cy="Title"]')
        .clear({ force: true })
        .type(newTitle, { force: true });
    }
    if (question !== '') {
      cy.get('[data-cy="Content"]')
        .clear({ force: true })
        .type(question, { force: true });
    }
    cy.get('[data-cy="Option"]')
      .eq(0)
      .clear({ force: true })
      .type(op0, { force: true });
    cy.get('[data-cy="Option"]')
      .eq(1)
      .clear({ force: true })
      .type(op1, { force: true });
    cy.get('[data-cy="Option"]')
      .eq(2)
      .clear({ force: true })
      .type(op2, { force: true });
    if (op3 !== '') {
      cy.get('[data-cy="Option"]')
        .eq(3)
        .clear({ force: true })
        .type(op3, { force: true });
    }
    cy.get('[data-cy="saveButton"]')
      .click({ force: true });
  }
);

Cypress.Commands.add('editQuestionAcceptedQuestion',
  (title, newTitle, content, op0, op1, op2, op3) =>{
  cy.contains(title)
    .parent()
    .parent()
    .find('[data-cy="editQuestion"]')
    .click({ force: true });
  if (newTitle !== '') {
    cy.get('[data-cy="questionTitle"]')
      .clear({ force: true })
      .type(newTitle, { force: true });
  } else {
    cy.get('[data-cy="questionTitle"]')
      .clear({ force: true });
  }
  cy.get('[data-cy="questionContent"]')
    .clear({ force: true })
    .type(content, { force: true });
  cy.get('[data-cy="questionOp"]')
    .eq(0)
    .clear({ force: true })
    .type(op0, { force: true });
  cy.get('[data-cy="questionOp"]')
    .eq(1)
    .clear({ force: true })
    .type(op1, { force: true });
  cy.get('[data-cy="questionOp"]')
    .eq(2)
    .clear({ force: true })
    .type(op2, { force: true });
  cy.get('[data-cy="questionOp"]')
    .eq(3)
    .clear({ force: true })
    .type(op3, { force: true });
  cy.get('[data-cy="questionSaveButton"]')
    .click({ force: true });
});
