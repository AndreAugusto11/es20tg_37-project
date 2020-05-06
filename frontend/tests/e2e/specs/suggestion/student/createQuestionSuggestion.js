describe('Create a suggestion', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.contains('Suggestions').click()
  });

  afterEach(() => {
    cy.contains('Logout').click()
  });

  it('login and creates a suggestion', () => {
    cy.createQuestionSuggestion('TestNormal','Question','a', 'b', 'c', 'd', 'No');
  });

  it('login and creates a Suggestion without title', () => {
    cy.createQuestionSuggestion('','Question','a', 'b', 'c', 'd', 'No');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click()
  });

  it('login and creates a suggestions without question', () => {
    cy.createQuestionSuggestion('TestNoQuestion','','a', 'b', 'c', 'd', 'No');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click()
  });

  it('login and creates a suggestions without one option', () => {
    cy.createQuestionSuggestion('TestNoOption','Question','a', 'b', 'c', '', 'No');
    cy.wait(5000)
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click()
  });

  it('login and creates a suggestions without one option correct', () => {
    cy.createQuestionSuggestion('TestNoCorrect','Question','a', 'b', 'c', 'd', 'Yes');
    cy.wait(5000)
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click()
  });

  it('Show created question', () => {
    cy.showQuestionSuggestion('TestNormal')
  })
});
