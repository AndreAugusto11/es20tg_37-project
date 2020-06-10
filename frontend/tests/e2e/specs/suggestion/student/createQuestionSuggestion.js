describe('Create a suggestion', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.contains('Suggestions').click()
  });

  afterEach(() => {
    cy.contains('Logout').click();
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM options WHERE question_id IN (SELECT id FROM questions WHERE title = \'TestCreate\');"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM questions WHERE title = \'TestCreate\';"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM question_suggestions;"');
  });

  it('login and creates a suggestion', () => {
    cy.createQuestionSuggestion('TestCreate','Question','a', 'b', 'c', 'd', 'No');
  });

  it('login and creates a Suggestion without title', () => {
    cy.createQuestionSuggestion('','Question','a', 'b', 'c', 'd', 'No');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click()
  });

  it('login and creates a suggestions without question', () => {
    cy.createQuestionSuggestion('TestCreate','','a', 'b', 'c', 'd', 'No');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click()
  });

  it('login and creates a suggestions without one option', () => {
    cy.createQuestionSuggestion('TestCreate','Question','a', 'b', 'c', '', 'No');
    cy.wait(5000)
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click()
  });

  it('login and creates a suggestions without one option correct', () => {
    cy.createQuestionSuggestion('TestCreate','Question','a', 'b', 'c', 'd', 'Yes');
    cy.wait(5000)
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click()
  });

  it('Show created question', () => {
    cy.createQuestionSuggestion('TestCreate','Question','a', 'b', 'c', 'd', 'No');
    cy.wait(5000)
    cy.showQuestionSuggestion('TestCreate')
  })
});
