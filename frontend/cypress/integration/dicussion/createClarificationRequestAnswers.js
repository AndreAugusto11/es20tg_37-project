describe('Create Clarification Request Answer walkthrough', () => {
	beforeEach(() => {

	})
  
	afterEach(() => {
	  cy.contains('Logout').click()
	})

	it('student login creates a Clarification Request and teacher login to confirm', () => {
		var content = generateContent(6)

		cy.demoStudentLogin()
		cy.solveQuiz()
		cy.createClarificationRequest(content)
		cy.contains('Logout').click()
		cy.demoTeacherLogin()
		cy.goToDiscussion()
		cy.get('[data-cy="Search"]').type(content)
		cy.contains(content)
	});

	it('login tries to create a Clarification Answer to a closed Request', () => {
		var contentReq = generateContent(6);
		var contentRes = generateContent(6);

		cy.demoStudentLogin()
		cy.solveQuiz()
		cy.createClarificationRequest(contentReq)
		cy.contains('Logout').click()
		cy.demoTeacherLogin()
		cy.goToDiscussion()
		cy.get('[data-cy="Search"]').type(contentReq)
		cy.contains(contentReq).click()
		cy.createClarificationRequestAnswer(contentRes)
		cy.contains('Logout').click()

		cy.demoTeacherLogin()
		cy.goToDiscussion()
		cy.contains(contentReq).click()
		cy.get('[data-cy="answerButton"]').should('not.exist')
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