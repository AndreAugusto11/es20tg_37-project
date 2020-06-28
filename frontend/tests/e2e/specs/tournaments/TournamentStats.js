describe('Show stats', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "update quizzes set tournament_id = null;"')
    cy.exec('psql -d tutordb -c "update topic_conjunctions set tournament_id = null;"')
    cy.exec('psql -d tutordb -c "delete from tournaments_enrolled_users;"')
    cy.exec('psql -d tutordb -c "delete from tournaments;"')
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
      'psql -d tutordb -c "insert into tournaments(title,available_date,conclusion_date,number_of_questions,status,user_id,course_execution_id) values (\'TITLE\',\'2025-06-05 00:21:00\', \'2025-06-06 00:21:00\',1,\'ENROLLING\',678,11);"'
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
