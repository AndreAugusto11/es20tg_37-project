describe('Tournaments walkthrough', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "update quizzes set tournament_id = null;"')
    cy.exec('psql -d tutordb -c "update topic_conjunctions set tournament_id = null;"')
    cy.exec('psql -d tutordb -c "delete from topics_topic_conjunctions where topic_conjunctions_id = 9999;"')
    cy.exec('psql -d tutordb -c "delete from topic_conjunctions where id = 9999;"')
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

  it('Tournament is created, enrolls in it and answers quiz', () => {
    cy.exec(
      `psql -d tutordb -c "insert into tournaments(id,title,available_date,conclusion_date,number_of_questions,status,user_id,course_execution_id) values (1,\'TITLE\',${getCurrentDateTime(30)}, ${getCurrentDateTime(60)},5,\'ENROLLING\',678,11);"`
    );
    cy.exec(
      'psql -d tutordb -c "insert into topic_conjunctions(id,tournament_id) values (9999,1);"'
    );
    cy.exec(
      'psql -d tutordb -c "insert into topics_topic_conjunctions(topics_id,topic_conjunctions_id) values (102,9999);"'
    );
    cy.exec(
      'psql -d tutordb -c "insert into topics_topic_conjunctions(topics_id,topic_conjunctions_id) values (88,9999);"'
    );
    cy.exec(
      'psql -d tutordb -c "insert into tournaments_enrolled_users(enrolled_tournaments_id,enrolled_users_id) values (1,678);"'
    );
    cy.enrollTournament('Student 678');
    cy.wait(30000);
    cy.contains('Discussion').click({ force: true });
    cy.contains('Tournaments').click({ force: true });
    cy.solveTournamentQuiz('Student 678');
    cy.wait(1000);
    cy.contains('Tournaments').click({ force: true });
    cy.get('[data-cy="openTournamentResults"]').should('exist');
    cy.get('[data-cy="openTournamentQuiz"]').should('exist');
    cy.get('[data-cy="openTournamentQuiz"]').click({ force: true });
  });
});

function getCurrentDateTime(seconds) {
  let date = new Date().toJSON().slice(0, 10);
  let time = new Date();
  time.setSeconds(time.getSeconds() + seconds);
  let timeS = time.toJSON().slice(11, 19);
  return '\'' + date + ' ' + timeS + '\'';
}
