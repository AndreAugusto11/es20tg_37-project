describe('Show stats', () => {
  beforeEach(() => {
    cy.exec(
      'psql -d tutordb -c "DELETE FROM options o WHERE question_id IN ( SELECT id FROM questions q WHERE q.type = \'SUGGESTION\');"'
    );
    cy.exec(
      'psql -d tutordb -c "DELETE FROM questions q WHERE q.type = \'SUGGESTION\';"'
    );
    cy.exec('psql -d tutordb -c "DELETE FROM users_question_suggestion;"');
    cy.exec('psql -d tutordb -c "DELETE FROM question_Suggestions;"');
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
      .contains(1)
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
      .contains(1)
      .should('exist');
  });
});
