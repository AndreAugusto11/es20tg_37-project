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

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.get('[data-cy="error"]')
    .parent()
    .find('button')
    .click()
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

Cypress.Commands.add('allTournaments', () => {
  cy.contains('Tournaments').click()
  cy.contains('All Tournaments').click()
});

Cypress.Commands.add('createTournaments', (numQ, topicName, start, finish) => {
  cy.contains('Tournaments').click()
  cy.contains('Create Tournaments').click()
  cy.get('[data-cy="createButton"]').click()
  cy.wait(5000)
  cy.get('[data-cy="numQuest"]').type(numQ)
  cy.get('[data-cy="topicSearch"]').click()
  cy.get('[data-cy="topicSearch"]').type(topicName + '\n').type('{esc}')
  cy.get('[data-cy="start"]').type(start)
  cy.get('[data-cy="end"]').type(finish)
  cy.get('[data-cy="save"]').click();
});

Cypress.Commands.add('enrollTournament', (id) => {
  cy.contains('Tournaments').click()
  cy.contains('All Tournaments').click()
  cy.contains(id)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="enrollTournament"]')
    .click()
  cy.contains('Tournaments').click()
  cy.contains('Enrolled Tournaments').click()
  cy.contains(id)
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/')
  cy.get('[data-cy="teacherDemoButton"]').click()
});

Cypress.Commands.add('goToDiscussion', () => {
  cy.contains('Management').click();
  cy.contains('Discussion').click()
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="studentDemoButton"]').click()
});

Cypress.Commands.add('solveQuiz', () => {
  cy.contains('QUIZZES').click();
  cy.contains('Create').click();
  cy.contains('Create quiz').click();
  for (let i = 0; i < 5; i++) {
    cy.get('.option').first().click();
    cy.get('div.square').click()
  }
  cy.get('.end-quiz').click();
  cy.contains('I\'m sure').click()
});

Cypress.Commands.add('createClarificationRequest', (content) => {
  cy.get('[data-cy="createClarificationButton"]').click();
  cy.get('[data-cy="Content"]').type(content);
  cy.get('[data-cy="saveButton"]').click()
});

Cypress.Commands.add('createClarificationRequestAnswer', (content) => {
  cy.get('[data-cy="answerButton"]').click();
  cy.get('[data-cy="Content"]').type(content);
  cy.get('[data-cy="saveButton"]').click()
});

Cypress.Commands.add('seeClarificationRequest', () => {
  cy.get('[data-cy="seeClarificationButton"]').click()
});

Cypress.Commands.add('listClarificationRequest', (content) => {
  cy.contains('Discussion').click();
  cy.get('[data-cy="Search"]').type(content);
  cy.contains(content)
});

Cypress.Commands.add('createQuestionSuggestion', (title, question, op0, op1, op2, op3, flag)=>{
  cy.contains('New Suggestion').click();
  if(title !== ''){cy.get('[data-cy="Title"]').type(title,{force: true})}
  if(question !== ''){cy.get('[data-cy="Content"]').type(question,{force: true})}
  cy.get('[data-cy="Option"]').eq(0).type(op0,{force: true});
  cy.get('[data-cy="Option"]').eq(1).type(op1,{force: true});
  cy.get('[data-cy="Option"]').eq(2).type(op2,{force: true});
  if(op3 !== ''){cy.get('[data-cy="Option"]').eq(3).type(op3,{force: true})}
  if(flag === 'No'){cy.get('[data-cy="Correct"]').eq(3).click({force: true})}
  cy.get('[data-cy="saveButton"]').click()
});

Cypress.Commands.add('showQuestionSuggestion', (title) =>{
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="showSuggestion"]')
    .click();
  cy.get('[data-cy="closeButton"]').click()
});

Cypress.Commands.add('acceptQuestionSuggestion', (title) =>{
  cy.contains(title)
    .parent()
    .children()
    .should('have.length', 5)
    .find('[data-cy="acceptButton"]')
    .click({force: true});
});

Cypress.Commands.add('acceptQuestionSuggestionShow', (title) =>{
  cy.contains(title)
    .parent()
    .children()
    .should('have.length', 5)
    .find('[data-cy="showButton"]')
    .click({force: true});
  cy.get('[data-cy="acceptQuestion"]').click();
});

Cypress.Commands.add('rejectQuestionSuggestion', (title, text) =>{
  cy.contains(title)
    .parent()
    .children()
    .should('have.length', 5)
    .find('[data-cy="rejectButton"]')
    .click({force: true});
  cy.get('[data-cy="justification_text"]').type(text);
  cy.get('[data-cy="saveJustification"]').click();
});

Cypress.Commands.add('rejectQuestionSuggestionShow', (title, text) =>{
  cy.contains(title)
    .parent()
    .children()
    .should('have.length', 5)
    .find('[data-cy="showButton"]')
    .click({force: true});
  cy.get('[data-cy="rejectQuestion"]').click();
  cy.get('[data-cy="justification_text"]').type(text);
  cy.get('[data-cy="saveJustification"]').click();
});
