describe('Create Clarifiction Request walkthrough', () => {
    beforeEach(() => {
      cy.demoStudentLogin()
    })
  
    afterEach(() => {

    })
  
    it('login, solves quiz, creates Clarification Request, and visualizes it', () => {
      cy.solveQuiz()
      cy.createClarificationRequest("Tenho uma duvida")
      cy.seeClarificationRequest()
    });

    it('login, solves quiz and creates Clarification Request, and checks on the list', () => {
        cy.solveQuiz()
        cy.createClarificationRequest("Tenho uma duvida")
        cy.listClarificationRequest("Tenho uma duvida")
      });

    it('login, solves quiz and creates empty Clarification Request', () => {
        cy.solveQuiz()
        cy.createClarificationRequest(" ")
        cy.closeErrorMessage()
      });
  
  });