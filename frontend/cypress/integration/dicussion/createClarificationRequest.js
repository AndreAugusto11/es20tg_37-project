describe('Create Clarification Request walkthrough', () => {
    beforeEach(() => {
      cy.demoStudentLogin()
    })
  
    afterEach(() => {
      cy.contains('Logout').click()
    })
  
    it('login, solves quiz, creates Clarification Request, and visualizes it', () => {
      cy.solveQuiz()
      cy.createClarificationRequest(generateContent(5))
      cy.seeClarificationRequest()
    });

    it('login, solves quiz and creates Clarification Request, and checks on the list', () => {
        let content = generateContent(5)

        cy.solveQuiz()
        cy.createClarificationRequest(content)
        cy.listClarificationRequest(content)
      });

    it('login, solves quiz and creates empty Clarification Request', () => {
        cy.solveQuiz()
        cy.createClarificationRequest(" ")
        cy.closeErrorMessage()
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