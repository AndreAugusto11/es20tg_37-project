describe('Reject a suggestion', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click()
  });

  afterEach(() => {
    cy.contains('Logout').click();
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM options WHERE question_id IN (SELECT id FROM questions WHERE title = \'TestReject\');"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM questions WHERE title = \'TestReject\';"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM users_question_suggestion;"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM justifications;"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM question_suggestions;"');
  });

  it('login, creates a suggestion and rejects it through direct button', () => {
    cy.createQuestionSuggestion('TestReject','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(5000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.rejectQuestionSuggestion('TestReject', 'Some justification');
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.showQuestionSuggestion('TestReject')
  });

  it('login, creates a suggestion and rejects it through show menu', () => {
    cy.createQuestionSuggestion('TestReject','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(5000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.rejectQuestionSuggestionShow('TestReject', 'Some justification 2');
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.showQuestionSuggestion('TestReject')
  });

  it('login, accepts rejected suggestion and rejects rejected suggestion and fails both times', () => {
    cy.createQuestionSuggestion('TestReject','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(5000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.rejectQuestionSuggestion('TestReject', 'Some justification Rejected 2');
    cy.acceptQuestionSuggestion('TestReject');
    cy.closeErrorMessage();
    cy.rejectQuestionSuggestion('TestReject', 'Some justification Rejected 2');
    cy.closeErrorMessage();
  });
});
