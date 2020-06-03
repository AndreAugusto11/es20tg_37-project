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

  it('Tournament is created and enrolls in it', () => {
    cy.exec(
      'psql -d tutordb -c "insert into tournaments(end_date,number_of_questions,start_date,status,user_id) values (\'2025-06-05 00:21:00\',1,\'2025-06-05 00:21:00\',\'CREATED\',678);"'
    );
    cy.enrollTournament('Student 678');
  });

  /* it('login,checks Enrolled Tournaments,solves quiz', () => {
    cy.exec(
      'psql -d tutordb -c "insert into tournaments_users (tournament_id,users_id) values(1234,676);"'
    );
    cy.exec(
      'psql -d tutordb -c "insert into users_tournaments (user_id,tournaments_id) values(676,1234);"'
    );

    cy.answerTournament('1234');
    cy.demoStudentLogin();
  }); */
});
