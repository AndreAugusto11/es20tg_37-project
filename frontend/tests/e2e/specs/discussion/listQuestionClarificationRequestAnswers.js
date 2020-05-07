describe('List Question Clarification Request walkthrough', () => {
	beforeEach(() => {
		cy.demoStudentLogin()
	})
  
	afterEach(() => {
	  cy.contains('Logout').click()
	})

	it('student login creates a Clarification Request and teacher login to confirm', () => {
		var content = generateContent(6)

		cy.solveQuiz()
		cy.createClarificationRequest(content)
		cy.contains('Logout').click()
      
		cy.demoTeacherLogin()
		cy.goToDiscussion()
		cy.get('[data-cy="Search"]').type(content)
		cy.contains(content).click()
		cy.get('[data-cy="ButtonToPublic"]').click({force: true})
		cy.contains('Logout').click()
		
		cy.demoStudentLogin()
		cy.contains('QUIZZES').click();
		cy.contains('Solved').click();
		cy.get('.list-row')
		  .first()
		  .click();
		cy.get('[data-cy="seeOtherClarificationsButton"]').click();
		cy.get('[data-cy="Search"]').type(content)
		cy.contains(content)
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