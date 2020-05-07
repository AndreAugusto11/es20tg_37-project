describe('Edit a question', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login and create suggestion that is accepted and created question edited', () => {

    cy.createQuestionSuggestion(
      'TestEdit', 'Content', 'Option1',
      'Option2', 'Option3', 'Option4', 'No');
    cy.contains('Logout').click();
    cy.wait(1000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.acceptQuestionSuggestion('TestEdit');
    cy.wait(500);
    cy.contains('Management').click();
    cy.wait(500);
    cy.contains('Questions').click();
    cy.wait(5000);
    cy.editQuestionAcceptedQuestion(
      'TestEdit', 'TestEditNew', 'ContentNew', 'Option1New',
      'Option2New', 'Option3New', 'Option4New', 'No')
    cy.wait(1000);
    cy.contains('TestEditNew').should('exist')
    cy.wait(500);
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.wait(500);
    cy.showQuestionSuggestion('TestEdit');
  });

  it('login and create suggestion that is accepted and created question edited wrongly', () => {

    cy.createQuestionSuggestion(
      'TestEdit', 'Content', 'Option1',
      'Option2', 'Option3', 'Option4', 'No');
    cy.contains('Logout').click();
    cy.wait(1000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.acceptQuestionSuggestion('TestEdit');
    cy.wait(500);
    cy.contains('Management').click();
    cy.wait(500);
    cy.contains('Questions').click();
    cy.wait(5000);
    cy.editQuestionAcceptedQuestion(
      'TestEdit', '', 'ContentNew', 'Option1New',
      'Option2New', 'Option3New', 'Option4New', 'No')
    cy.wait(1000);
    cy.closeErrorMessage();
    cy.get('[data-cy="questionCancelButton"]').click()
  });
});
