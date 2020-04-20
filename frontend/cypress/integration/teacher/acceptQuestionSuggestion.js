describe('Accept a suggestion', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login, creates a suggestion and accepts it through direct button', () => {

    cy.createQuestionSuggestion('TestAccept','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(10000)
    cy.demoTeacherLogin();
    cy.acceptQuestionSuggestion('TestAccept');
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.showQuestionSuggestion('TestAccept')
  });

  it('login, creates a suggestion and accepts it through show menu', () => {

    cy.createQuestionSuggestion('TestAcceptShow','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(10000);
    cy.demoTeacherLogin();
    cy.acceptQuestionSuggestionShow('TestAcceptShow');
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.showQuestionSuggestion('TestAcceptShow')
  });

  it('login, accepts accepted suggestion and rejects accepted suggestion and fails both times', () => {

    cy.demoTeacherLogin();
    cy.acceptQuestionSuggestion('TestAccept');
    cy.closeErrorMessage();
    cy.rejectQuestionSuggestion('TestAccept', 'Some justification Rejected');
    cy.closeErrorMessage();
  });
});
