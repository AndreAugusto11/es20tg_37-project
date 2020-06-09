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

  it('Tournament is created and enrolls in it', () => {
    cy.exec(
      'psql -d tutordb -c "insert into tournaments(title,available_date,conclusion_date,number_of_questions,status,user_id,course_execution_id) values (\'TITLE\',\'2025-06-05 00:21:00\', \'2025-06-06 00:21:00\',1,\'ENROLLING\',678,11);"'
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
