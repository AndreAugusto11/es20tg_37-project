describe('Show students stats', () => {
  beforeEach(() => {
    cy.exec('psql -d tutordb -c "UPDATE users SET private_clarification_stats=false, private_suggestion_stats=false, private_tournaments_stats=false WHERE id=676;"')
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Show student stats after making Clarifications and Suggestions stats private', () => {
    cy.contains('Stats').click();
    cy.wait(1000)
    cy.get('[data-cy="clarificationsTab"]').click();
    cy.get('[data-cy="privateClarificationStatsBtn"]').click({force: true})
    cy.get('[data-cy="suggestionsTab"]').click();
    cy.get('[data-cy="privateSuggestionStatsBtn"]').click({force: true})
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Course Stats').click();
    cy.get('[data-cy="otherStats"]').click();
    cy.get('[data-cy="Search"]').type('demo');
    cy.get('[data-cy="iconPrivateClarification"]');
    cy.get('[data-cy="iconPrivateSuggestion"]');
    cy.get('[data-cy="iconPrivateTournament"]').should('not.exist');

  });

  it('Show student stats after making Clarifications and Tournaments stats private', () => {
    cy.contains('Stats').click();
    cy.wait(1000)
    cy.get('[data-cy="clarificationsTab"]').click();
    cy.get('[data-cy="privateClarificationStatsBtn"]').click({force: true})
    cy.get('[data-cy="tournamentsTab"]').click();
    cy.get('[data-cy="privateTournamentsStatsBtn"]').click({force: true})
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Course Stats').click();
    cy.get('[data-cy="otherStats"]').click();
    cy.get('[data-cy="Search"]').type('demo');
    cy.get('[data-cy="iconPrivateClarification"]');
    cy.get('[data-cy="iconPrivateTournament"]');
    cy.get('[data-cy="iconPrivateSuggestion"]').should('not.exist');
  });
});
