describe('Create a valide suggestion', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login and creates a suggestion and accept it', () => {

    cy.createQuestionSuggestion('TestAccept','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(30000)
    cy.demoTeacherLogin();
    cy.acceptQuestionSuggestion('TestAccept');
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.showQuestionSuggestion('TestAccept')
  });

  it('login and creates a suggestion and accept it now in show', () => {

    cy.createQuestionSuggestion('TestAcceptShow','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(30000);
    cy.demoTeacherLogin();
    cy.acceptQuestionSuggestionShow('TestAcceptShow');
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.showQuestionSuggestion('TestAcceptShow')
  });

  it('login and creates a suggestion and reject it', () => {

    cy.createQuestionSuggestion('TestAccept','Question','a', 'b', 'c', 'd', 'No');
    cy.contains('Logout').click();
    cy.wait(30000)
    cy.demoTeacherLogin();
    cy.rejectQuestionSuggestion('TestAccept');
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.showQuestionSuggestion('TestAccept')
  });


});
