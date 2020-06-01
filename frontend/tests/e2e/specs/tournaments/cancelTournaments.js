describe('Tournament Cancel', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "Delete from tournaments_users;"')
    cy.exec('psql -d tutordb -c "Delete from users_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from users_created_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments_topics;"')
    cy.exec('psql -d tutordb -c "Delete from users_enrolled_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments ;"')
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Creating tournament and cancelling it', () => {
    cy.createTournaments('5', 'GitHub', '2025-11-23 10:50', '2025-11-23 10:59');
    cy.cancelTournaments("Demo Student");
    cy.contains('CANCELLED').should('exist');
  });
});
