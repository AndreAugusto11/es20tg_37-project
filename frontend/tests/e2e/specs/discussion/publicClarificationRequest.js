describe('Make Clarification Request to be public walkthrough', () => {
    beforeEach(() => {
      cy.demoStudentLogin()
    })
  
    afterEach(() => {
      cy.contains('Logout').click()
    })
  
    it('student creates Clarification Request, teacher login to make it public, student login to confirm it is public', () => {
      cy.solveQuiz()
      cy.createClarificationRequest(generateContent(5))
      cy.seeClarificationRequest()
      cy.contains('[data-cy="iconPrivate"]')
      cy.contains('Logout').click()
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