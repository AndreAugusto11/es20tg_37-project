describe('Tournaments walkthrough', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "Delete from tournaments_users;"')
    cy.exec('psql -d tutordb -c "Delete from users_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from users_created_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments_topics;"')
    cy.exec('psql -d tutordb -c "Delete from users_enrolled_tournaments;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments ;"')
    cy.exec(
      'psql -d tutordb -c "insert into tournaments(id,end_date,number_of_questions,start_date,status,user_id,quiz_id) values (1234,\'2019-05-26 00:00:00\',1,\'2021-05-24 00:00:00\',\'ONGOING\',676,5376);"'
    );
    cy.exec(
      'psql -d tutordb -c "insert into tournaments_topics(tournament_id,topics_id) values (1234,82);"'
    );
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login enroll in tournament and checks it Enrolled Tournaments', () => {
    cy.enrollTournament('1234');
    cy.demoStudentLogin();
  });

  it('login,checks Enrolled Tournaments,solves quiz', () => {
    cy.exec(
      'psql -d tutordb -c "insert into tournaments_users (tournament_id,users_id) values(1234,676);"'
    );
    cy.exec(
      'psql -d tutordb -c "insert into users_tournaments (user_id,tournaments_id) values(676,1234);"'
    );

    cy.answerTournament('1234');
    cy.demoStudentLogin();
  });
});
