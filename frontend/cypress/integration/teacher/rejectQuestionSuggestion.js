describe('Reject a suggestion', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login, creates a suggestion and rejects it through direct button', () => {

    cy.createQuestionSuggestion('TestReject','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(10000)
    cy.demoTeacherLogin();
    cy.rejectQuestionSuggestion('TestReject', 'Some justification');
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.showQuestionSuggestion('TestReject')
  });

  it('login, creates a suggestion and rejects it through show menu', () => {

    cy.createQuestionSuggestion('TestRejectShow','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(10000)
    cy.demoTeacherLogin();
    cy.rejectQuestionSuggestionShow('TestRejectShow', 'Some justification 2');
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.showQuestionSuggestion('TestReject')
  });

  it('login, accepts rejected suggestion and rejects rejected suggestion and fails both times', () => {

    cy.demoTeacherLogin();
    cy.acceptQuestionSuggestion('TestReject');
    cy.closeErrorMessage();
    cy.rejectQuestionSuggestion('TestReject', 'Some justification Rejected 2');
    cy.closeErrorMessage();
  });
});
