describe('Tournaments walkthrough', () => {
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

  it('login and create a tournament', () => {
    cy.createTournaments('4');
  });
});
