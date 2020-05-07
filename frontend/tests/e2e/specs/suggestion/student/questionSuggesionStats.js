describe('Show stats', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('it creates a suggestion and see the stats', () => {
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
    cy.contains('Stats').click();
    cy.get('[data-cy="totalNumberSuggestions"]')
      .contains(/^[1-9][0-9]*$/)
      .should('exist');
  });
  it('it creates a suggestion, this is accepted and see the  stats', () => {
    cy.createQuestionSuggestion(
      'TestNormal',
      'Question',
      'a',
      'b',
      'c',
      'd',
      'No'
    );
    cy.wait(5000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.acceptQuestionSuggestion('TestNormal');
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Stats').click();
    cy.get('[data-cy="totalNumberSuggestionsAvailable"]')
      .contains(/^[1-9][0-9]*$/)
      .should('exist');
  });
});
