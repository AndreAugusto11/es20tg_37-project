describe('Update a suggestion', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login, creates a suggestion and when this is rejected updates this one', () => {
    cy.createQuestionSuggestion(
      'TestNormal',
      'Question',
      'a',
      'b',
      'c',
      'd',
      'No'
    );
    cy.wait(10000);
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.rejectQuestionSuggestion('TestNormal', 'This is bad');
    cy.wait(1000);
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.updateRejectedQuestionSuggestion(
      'TestNormal',
      'Update',
      'QuestionUpdate',
      'e',
      'f',
      'h',
      'i'
    );
    cy.wait(10000);
    cy.showQuestionSuggestion('Update');
  });

  it('login, creates a suggestion and updates this one', () => {
    cy.createQuestionSuggestion(
      'TestNormal',
      'Question',
      'a',
      'b',
      'c',
      'd',
      'No'
    );
    cy.wait(10000);
    cy.updateRejectedQuestionSuggestion(
      'TestNormal',
      'Update',
      'QuestionUpdate',
      'e',
      'f',
      'h',
      'i'
    );
    cy.wait(1000);
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
  });
});
