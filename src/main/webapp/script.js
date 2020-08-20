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
    fetch('/check-permissions?projID='+config.projectID+'&saID='+config.serviceAccount)
    .then(response => response.json())
    .then((accounts) => {
      console.log(accounts);  
    });
}

function updateJobDatabase() {
  fetch('/jobs', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(accessDataflowAPI),
  });
  
}
