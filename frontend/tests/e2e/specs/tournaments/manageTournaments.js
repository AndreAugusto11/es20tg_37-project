describe('Tournaments walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login and checks all tournaments', () => {
    cy.allTournaments()
    cy.demoStudentLogin()
  });

  it('login enroll in tournament and checks it Enrolled Tournaments', () => {
    cy.exec('psql -d tutordb -c "insert into tournaments(id,end_date,number_of_questions,start_date,status,user_id) values (1234,\'2020-05-26 00:00:00\',1,\'2020-05-24 00:00:00\',\'OPEN\',641);"')
    cy.exec('psql -d tutordb -c "insert into tournaments_topics(tournament_id,topics_id) values (1234,82);"')

    cy.enrollTournament('1234')

    cy.exec('psql -d tutordb -c "Delete from tournaments_users where tournament_id = 1234;"')
    cy.exec('psql -d tutordb -c "Delete from users_tournaments where tournaments_id = 1234;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments_topics where tournament_id = 1234;"')
    cy.exec('psql -d tutordb -c "Delete from tournaments where id = 1234;"')
    cy.demoStudentLogin()
  });


});
