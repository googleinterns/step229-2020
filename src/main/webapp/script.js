// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

function initBody() {
    fetch('/data').then(console.log('Works'));
    //checkPermissions();
}

function checkPermissions() {
    fetch('/check-permissions?projID='+configLogs.projectID+'&saID='+configLogs.serviceAccount)
    .then(response => response.json())
    .then((accounts) => {
      console.log(accounts);  
    });
}

function updateProjectDatabase() {
  fetch('/jobs', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(accessDataflowAPI),
  }); 
}

function getJobsFromProject(projectID) {
  fetch('/jobs?projectID=' + projectID)
  .then(response => response.json())
  .then(jobs => console.log(jobs))
}
