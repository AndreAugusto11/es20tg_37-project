describe('Tournament Cancel', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Cancelling a precreated Tournament', () => {
    cy.precreateTournament();
    cy.cancelTournaments(23);
    cy.deletePrecreatedTournament();
  });
});
