describe('Close Clarification Request walkthrough', () => {
  beforeEach(() => {

  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('student login creates a Clarification Request and closes it', () => {
    var content = generateContent(6)

    cy.demoStudentLogin()
    cy.solveQuiz()
    cy.createClarificationRequest(content)
    cy.wait(1000)
    cy.listClarificationRequest(content)
    cy.contains(content).click()
    cy.contains('OPEN').should('exist')
    cy.closeClarificationRequest()
    cy.contains('CLOSED').should('exist')
    cy.get('[data-cy="answerButtonDisabled"]').should('exist');
    cy.get('[data-cy="closeButtonDisabled"]').should('exist');
  });

  it('student login creates a Clarification Request, teacher answers and then student closes it', () => {
    var contentReq = generateContent(6)
    var contentRes = generateContent(6)

    cy.demoStudentLogin()
    cy.solveQuiz()
    cy.createClarificationRequest(contentReq)
    cy.contains('Logout').click()

    cy.demoTeacherLogin()
    cy.goToDiscussion()
    cy.get('[data-cy="Search"]').type(contentReq)
    cy.contains(contentReq).click()
    cy.createClarificationRequestAnswer(contentRes)
    cy.contains('ANSWERED').should('exist')
    cy.contains('Logout').click()

    cy.demoStudentLogin()
    cy.listClarificationRequest(contentReq)
    cy.contains(contentReq).click()
    cy.contains('ANSWERED').should('exist')
    cy.closeClarificationRequest()
    cy.contains('CLOSED').should('exist')
    cy.get('[data-cy="answerButtonDisabled"]').should('exist');
    cy.get('[data-cy="closeButtonDisabled"]').should('exist');
  });

});

function generateContent(length) {
  let result           = '';
  let characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let charactersLength = characters.length;
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}