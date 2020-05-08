describe('Accept a suggestion', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM options WHERE question_id IN (SELECT id FROM questions WHERE title = \'TestAccept\');"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM questions WHERE title = \'TestAccept\';"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM users_question_suggestion;"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM question_suggestions;"');
  });

  it('login, creates a suggestion and accepts it through direct button', () => {
    cy.createQuestionSuggestion('TestAccept','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(5000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.acceptQuestionSuggestion('TestAccept');
    cy.contains('Management').click();
    cy.wait(500);
    cy.contains('Questions').click();
    cy.wait(5000);
    cy.showQuestionFromSuggestion('TestAccept')
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.showQuestionSuggestion('TestAccept');
  });

  it('login, creates a suggestion and accepts it through show menu', () => {
    cy.createQuestionSuggestion('TestAccept','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(5000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.acceptQuestionSuggestionShow('TestAccept');
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.showQuestionSuggestion('TestAccept')
  });
});
