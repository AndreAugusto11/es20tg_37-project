describe('Tournament Cancel', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "update quizzes set tournament_id = null;"')
    cy.exec('psql -d tutordb -c "update topic_conjunctions set tournament_id = null;"')
    cy.exec('psql -d tutordb -c "delete from tournaments_enrolled_users;"')
    cy.exec('psql -d tutordb -c "delete from tournaments;"')
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Creating tournament and cancelling it', () => {
    cy.createTournaments('4');
    cy.cancelTournaments('Demo Student');
    cy.contains('CANCELLED').should('exist');
    cy.get('[data-cy="disabledCancelTournament"]').should('exist');
  });
});
