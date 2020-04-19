describe('Create Clarifiction Request walkthrough', () => {
    beforeEach(() => {
      cy.demoStudentLogin()
    })
  
    afterEach(() => {

    })
  
    it('login, solves quiz, creates Clarification Request, and visualizes it', () => {
      var randomNumber = Math.floor(Math.random() * 10000);
      var content = "Tenho uma dvida aqui" + randomNumber;

      cy.solveQuiz()
      cy.createClarificationRequest(content)
      cy.seeClarificationRequest()
    });

    it('login, solves quiz and creates Clarification Request, and checks on the list', () => {
        var randomNumber = Math.floor(Math.random() * 10000);
        var content = "Tenho uma dvida aqui" + randomNumber;

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