describe('Show stats', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "Delete from tournaments_users;"')
    cy.exec('psql -d tutordb -c "Delete from users_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from users_created_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments_topics;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments ;"')
    cy.demoStudentLogin();
    cy.contains('Tournaments').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('it creates 2 tournaments and see the stats', () => {
    cy.createTournaments('5', 'GitHub', '2025-11-23 10:50', '2025-11-23 10:59')
    cy.createTournaments('6', 'GitHub', '2025-11-23 10:50', '2025-11-23 10:59')
    cy.wait(5000);
    cy.contains('Stats').click();
    cy.get('[data-cy="torn"]').click();
    cy.get('[data-cy="totalNumberCreatedTournaments"]')
      .contains(2)
      .should('exist');
  });
  it('it enrolls in a Tournament and see the  stats', () => {
    cy.exec('psql -d tutordb -c "insert into tournaments(id,end_date,number_of_questions,start_date,status,user_id) values (1234,\'2020-05-26 00:00:00\',1,\'2020-05-24 00:00:00\',\'OPEN\',641);"')
    cy.exec('psql -d tutordb -c "insert into tournaments_topics(tournament_id,topics_id) values (1234,82);"')
    cy.enrollTournament('1234')
    cy.demoStudentLogin();
    cy.contains('Stats').click();
    cy.get('[data-cy="torn"]').click();
    cy.get('[data-cy="totalNumberEnrolledTournaments"]')
      .contains(1)
      .should('exist');
  });
});
