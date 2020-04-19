describe('Create Clarifiction Request Answer walkthrough', () => {
	beforeEach(() => {

	})
  
	afterEach(() => {
	  cy.contains('Logout').click()
	})

	it('login tries to create a Clarification Answer to a closed Request', () => {
		cy.demoTeacherLogin()

		cy.contains('CLOSED').click()

		cy.get('[data-cy="answerButton"]').should('not.exist')
	});

	it('login creates a Clarification Request Answer', () => {
		cy.demoTeacherLogin()

		cy.contains('OPEN').click()

		cy.createClarificationRequestAnswer('This is the answer to the request')
	});

	it('student login creates a Clarification Request and teacher login to confirm', () => {
		cy.demoStudentLogin()
		cy.solveQuiz()
		cy.createClarificationRequest("Tenho uma duvida aqui")
		cy.contains('Logout').click()
		cy.demoTeacherLogin()
		cy.get('[data-cy="Search"]').type("Tenho uma duvida aqui")
		cy.contains("Tenho uma duvida aqui")
	});

  });
  