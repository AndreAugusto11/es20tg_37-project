describe('Manage question suggestion walkthrough', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM options WHERE question_id IN (SELECT id FROM questions WHERE title = \'TestAccept\');"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM questions WHERE title = \'TestAccept\';"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM justifications;"');
    cy.exec('psql -d tutordb -c "' +
      'DELETE FROM question_suggestions;"');
  });

  afterEach(() => {
    cy.logout();
  });

  it('Creates a suggestion, gets accepted and admin removes it', () => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.createQuestionSuggestion('TestAccept','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(2000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.acceptQuestionSuggestionShow('TestAccept');
    cy.contains('Logout').click();
    cy.demoAdminLogin();
    cy.get('[data-cy="administrationMenuButton"]').click();
    cy.get('[data-cy="manageSuggestionsMenuButton"]').click();
    cy.removeQuestionSuggestion('TestAccept');
  });

  it('Creates a suggestion, gets rejected and admin removes it', () => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.createQuestionSuggestion('TestAccept','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(2000);
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Suggestions').click();
    cy.rejectQuestionSuggestion('TestAccept', 'Some justification');
    cy.contains('Logout').click();
    cy.demoAdminLogin();
    cy.get('[data-cy="administrationMenuButton"]').click();
    cy.get('[data-cy="manageSuggestionsMenuButton"]').click();
    cy.removeQuestionSuggestion('TestAccept');
  });

  it('Creates a suggestion pending and admin tries to remove it', () => {
    cy.demoStudentLogin();
    cy.contains('Suggestions').click();
    cy.createQuestionSuggestion('TestAccept','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(2000);
    cy.demoAdminLogin();
    cy.get('[data-cy="administrationMenuButton"]').click();
    cy.get('[data-cy="manageSuggestionsMenuButton"]').click();
    cy.get('[data-cy="disabledRemoveButton"]').should('exist');
  });
});
