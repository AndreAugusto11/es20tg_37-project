describe('Show stats', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "Delete from tournaments_users;"')
    cy.exec('psql -d tutordb -c "Delete from users_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from users_created_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments_topics;"')
    cy.exec('psql -d tutordb -c "Delete from users_enrolled_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments;"');
    cy.exec(
      'psql -d tutordb -c "UPDATE users SET private_tournaments_stats = false;"'
    );
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Creates a Tournament and sees the stats', () => {
    cy.createTournaments('5');
    cy.wait(1000);
    cy.contains('Stats').click();
    cy.get('[data-cy="torn"]').click();
    cy.get('[data-cy="totalNumberCreatedTournaments"]')
      .contains(1)
      .should('exist');
  });
  it('Enrolls in a Tournament and sees the stats', () => {
    cy.exec(
      'psql -d tutordb -c "insert into tournaments(end_date,number_of_questions,start_date,status,user_id) values (\'2025-06-05 00:21:00\',1,\'2025-06-05 00:21:00\',\'CREATED\',678);"'
    );
    cy.enrollTournament('Student 678')
    cy.contains('Stats').click();
    cy.get('[data-cy="torn"]').click();
    cy.get('[data-cy="totalNumberEnrolledTournaments"]')
      .contains(1)
      .should('exist');
  });
  it('Changing Tournament Stats from public to private', () => {
      cy.contains('Stats').click()
      cy.wait(1000)
      cy.get('[data-cy="torn"]').click();
      cy.get('[data-cy="privateTournamentsStatsBtn"]').click({force: true})
      cy.wait(1000)
      cy.get('[data-cy="publicTournamentsStatsBtn"]').should('exist')
  })
});
