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
Cypress.Commands.add('demoAdminLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="adminButton"]').click()
    cy.contains('Administration').click()
    cy.contains('Manage Courses').click()
})

Cypress.Commands.add('demoTeacherLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="teacherDemoButton"]').click()
})

Cypress.Commands.add('goToDiscussion', () => {
    cy.contains('Management').click()
    cy.contains('Discussion').click()
})

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="studentDemoButton"]').click()
})

Cypress.Commands.add('solveQuiz', () => {
    cy.contains('QUIZZES').click()
    cy.contains('Create').click()
    cy.contains('Create quiz').click()
    for (let i = 0; i < 5; i++) {
        cy.get('.option').first().click()
        cy.get('div.square').click()
    }
    cy.get('.end-quiz').click()
    cy.contains('I\'m sure').click()
})

Cypress.Commands.add('createClarificationRequest', (content) => {
    cy.get('[data-cy="createClarificationButton"]').click()
    cy.get('[data-cy="Content"]').type(content)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('createClarificationRequestAnswer', (content) => {
    cy.get('[data-cy="answerButton"]').click()
    cy.get('[data-cy="Content"]').type(content)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('seeClarificationRequest', () => {
    cy.get('[data-cy="seeClarificationButton"]').click()
})

Cypress.Commands.add('listClarificationRequest', (content) => {
    cy.contains('Discussion').click()
    cy.get('[data-cy="Search"]').type(content)
    cy.contains(content)
})

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="Name"]').type(name)
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
    cy.contains('Error')
        .parent()
        .find('button')
        .click()
})

Cypress.Commands.add('deleteCourseExecution', (acronym) => {
    cy.contains(acronym)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="deleteCourse"]')
        .click()
})

Cypress.Commands.add('createFromCourseExecution', (name, acronym, academicTerm) => {
    cy.contains(name)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="createFromCourse"]')
        .click()
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

