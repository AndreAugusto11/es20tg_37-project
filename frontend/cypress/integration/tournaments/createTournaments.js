describe('Tournaments walkthrough', () => {
    beforeEach(() => {
        cy.demoStudentLogin()
    })

    afterEach(() => {
        cy.contains('Logout').click()
    })

    it('login and checks all tournaments', () => {
        cy.createTournaments('5', 'GitHub', '2025-11-23 10:50', '2025-11-23 10:59')
        cy.exec('psql -d tutordb -c "Delete from tournaments_users;"')
        cy.exec('psql -d tutordb -c "Delete from users_tournaments;"')
        cy.exec('psql -d tutordb -c "Delete from users_created_tournaments;"')
        cy.exec('psql -d tutordb -c "Delete from tournaments_topics;"')
        cy.exec('psql -d tutordb -c "Delete from tournaments ;"')
    });
});
