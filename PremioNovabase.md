# ES20 Novabase submission, Group 37

## Additional Features

### Ddp ([PR #185](https://github.com/tecnico-softeng/es20tg_37-project/pull/185), [PR #204](https://github.com/tecnico-softeng/es20tg_37-project/pull/204))

- #### F12: Students can add images to the Clarification Requests and its Answers

- #### F13: Teachers can add images to the Clarification Request Answers

- #### F14: Teachers can see students' clarifications public stats

### Ppa ([PR #199](https://github.com/tecnico-softeng/es20tg_37-project/pull/199), [PR #204](https://github.com/tecnico-softeng/es20tg_37-project/pull/204))

- #### F9: Delete accepted/rejected suggestion

- #### F10: Attach image to suggestion

- #### F11: Attach image to justification

- #### F12: List all suggestions

- #### F13: Teachers can see students' suggestions public stats

- #### Frontend UI Refactored

### Tdp ([PR #206](https://github.com/tecnico-softeng/es20tg_37-project/pull/206), [PR #204](https://github.com/tecnico-softeng/es20tg_37-project/pull/204))

- #### Domain, Web Services, Views and Tests Refactored

- #### F5: Dynamically Generate Quiz

- #### F11: A student can view its quiz results of a tournament

- #### F12: A student can view the overall results of the tournament and his placement

- #### F13: Teachers can see students' tournaments public stats

## Continuous Integration (CI)

For each pull request/push to develop or master, this sequence is triggered through a Github Actions Workflow:

Commit -> Build -> Run Spock and Cypress Tests

## Continuous Deployment (CD)

http://quizzes-tutor.westeurope.cloudapp.azure.com/

We build images of the backend and frontend (dockerfile), than with docker compose we build a container with those images and build the db.

- Automatic: AzureDevops Pipeline -> AzureVM with Storage Account

- Manual: SSH to VM

## People Involved

  - André Augusto, 90704, AndreAugusto11
  - Lucas Vicente, 90744, WARSKELETON
  - Miguel Levezinho, 90756, mLeveIST
