describe('Update a suggestion', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
    cy.exec('psql -d tutordb -c ' +
      '"DELETE FROM options o WHERE question_id IN ( SELECT id FROM questions WHERE title = \'TestUpdate\');"');
    cy.exec('psql -d tutordb -c ' +
      '"DELETE FROM questions WHERE title = \'TestUpdate\';"');
    cy.exec('psql -d tutordb -c ' +
      '"DELETE FROM options o WHERE question_id IN ( SELECT id FROM questions WHERE title = \'TestUpdateNew\');"');
    cy.exec('psql -d tutordb -c ' +
      '"DELETE FROM questions WHERE title = \'TestUpdateNew\';"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM justifications;"');
    cy.exec('psql -d tutordb -c ' +
      '"DELETE FROM users_question_suggestion;"');
    cy.exec('psql -d tutordb -c ' +
      '"DELETE FROM question_Suggestions;"');
  });

  it('login, creates a suggestion and when this is rejected updates this one', () => {
    cy.createQuestionSuggestion(
      'TestUpdate',
      'Question',
      'a',
      'b',
      'c',
      'd',
      'No'
    );
    cy.wait(5000);
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.rejectQuestionSuggestion('TestUpdate', 'This is bad');
    cy.wait(1000);
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.updateRejectedQuestionSuggestion(
      'TestUpdate',
      'TestUpdateNew',
      'QuestionUpdate',
      'e',
      'f',
      'h',
      'i'
    );
    cy.wait(5000);
    cy.showQuestionSuggestion('Update');
  });

  it('login, creates a suggestion and updates this one', () => {
    cy.createQuestionSuggestion(
      'TestUpdate',
      'Question',
      'a',
      'b',
      'c',
      'd',
      'No'
    );
    cy.wait(5000);
    cy.updateRejectedQuestionSuggestion(
      'TestUpdate',
      'TestUpdateNew',
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
