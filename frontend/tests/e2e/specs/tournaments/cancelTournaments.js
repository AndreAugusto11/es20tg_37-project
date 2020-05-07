describe('Tournament Cancel', () => {
  beforeEach(() => {
    cy.precreateTournament();
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
    cy.deletePrecreatedTournament();
  });

  it('Cancelling a precreated Tournament', () => {
    cy.cancelTournaments(23);
  });

  it('Cancelling a previously cancelled Tournament and failing', () => {
    cy.contains('Tournaments').click();
    cy.contains('Create Tournaments').click();
    cy.contains(23)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="cancelTournament"]')
      .click();
    cy.closeErrorMessage();
  });
});
