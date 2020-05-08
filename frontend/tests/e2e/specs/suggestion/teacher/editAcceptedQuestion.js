describe('Edit a question', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM options WHERE question_id IN (SELECT id FROM questions WHERE title = \'TestEdit\');"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM questions WHERE title = \'TestEdit\';"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM users_question_suggestion;"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM question_suggestions;"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM options WHERE question_id IN (SELECT id FROM questions WHERE title = \'TestEditNew\');"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM questions WHERE title = \'TestEditNew\';"');
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
    cy.showQuestionSuggestion('TestEdit')
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
    cy.editQuestionAcceptedQuestion(
      'TestEdit', '', 'ContentNew', 'Option1New',
      'Option2New', 'Option3New', 'Option4New', 'No');
    cy.wait(1000);
    cy.closeErrorMessage();
    cy.get('[data-cy="questionCancelButton"]').click();
  });
});
