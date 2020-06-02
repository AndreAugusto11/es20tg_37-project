describe('Tournaments walkthrough', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "Delete from tournaments_users;"')
    cy.exec('psql -d tutordb -c "Delete from users_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from users_created_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments_topics;"')
    cy.exec('psql -d tutordb -c "Delete from users_enrolled_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments;"')
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login and create a tournament', () => {
    cy.createTournaments('5');
  });
});
