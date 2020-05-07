describe('Show stats', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('it creates a suggestion and see the  stats', () => {
    cy.createQuestionSuggestion(
      'TestNormal',
      'Question',
      'a',
      'b',
      'c',
      'd',
      'No'
    );
    cy.contains('Stats').click();
    cy.wait(5000);
  });
  it('it creates a suggestion, this is accpeted and see the  stats', () => {
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
    cy.wait(5000);
  });
});
