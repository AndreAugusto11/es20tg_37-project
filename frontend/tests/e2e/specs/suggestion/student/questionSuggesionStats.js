describe('Show stats', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
    cy.exec(
      'psql -d tutordb -c ' +
        '"DELETE FROM options o WHERE question_id IN ( SELECT id FROM questions WHERE title = \'TestStats\');"'
    );
    cy.exec(
      'psql -d tutordb -c ' +
        '"DELETE FROM questions WHERE title = \'TestStats\';"'
    );
    cy.exec('psql -d tutordb -c ' + '"DELETE FROM users_question_suggestion;"');
    cy.exec('psql -d tutordb -c ' + '"DELETE FROM question_Suggestions;"');
  });

  it('it creates a suggestion and see the stats', () => {
    cy.createQuestionSuggestion(
      'TestStats',
      'Question',
      'a',
      'b',
      'c',
      'd',
      'No'
    );
    cy.wait(10000);
    cy.contains('Stats').click();
    cy.get('[data-cy="sugge"]').click();
    cy.get('[data-cy="totalNumberSuggestions"]')
      .contains(1)
      .should('exist');
  });

  it('it creates a suggestion, this is accepted and see the  stats', () => {
    cy.createQuestionSuggestion(
      'TestStats',
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
    cy.acceptQuestionSuggestion('TestStats');
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Stats').click();
    cy.get('[data-cy="sugge"]').click();
    cy.get('[data-cy="totalNumberSuggestionsAvailable"]')
      .contains(1)
      .should('exist');
  });

  it('And switch suggestions from private to public and then to public again', () => {
    cy.contains('Stats').click();
    cy.get('[data-cy="sugge"]').click();
    cy.get('[data-cy="privateSuggestionStatsBtn"]').click();
    cy.get('[data-cy="publicSuggestionStatsBtn"]').click();
  });
});
