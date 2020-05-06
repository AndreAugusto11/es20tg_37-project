describe('Make Clarification Request to be public walkthrough', () => {
    beforeEach(() => {
      cy.demoStudentLogin()
    })
  
    afterEach(() => {
      cy.contains('Logout').click()
    })
  
    it('student creates Clarification Request, teacher login to make it public, teacher sees if it is presented public', () => {
      var content = generateContent(5)
      cy.solveQuiz()
      cy.createClarificationRequest(content)
      cy.seeClarificationRequest()
      cy.contains('Logout').click()
      
      cy.demoTeacherLogin()
      cy.goToDiscussion()
      cy.get('[data-cy="Search"]').type(content)
      cy.contains(content).click()
      cy.get('[data-cy="ButtonToPublic"]').click()

      cy.goToDiscussion()
      cy.get('[data-cy="Search"]').type(content)
      cy.contains(content).click()
      cy.get('[data-cy="ButtonToPrivate"]').click()
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