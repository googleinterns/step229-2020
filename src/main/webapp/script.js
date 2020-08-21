// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

function initBody() {
  setCredentialsServlet();
  checkPermissions();
}

function setCredentialsServlet() {
  var credentialsUrl = formatURLs("get-credentials", {"projID":configLogs.projectID, "bucket":configLogs.bucketName, "object":configLogs.objectName});
  fetch(credentialsUrl)
  .then(response => console.log("works"));
}

function checkPermissions() {
  var permissionsUrl = formatURLs("check-permissions", {"projID":configLogs.projectID});
  fetch(permissionsUrl)
  .then(response => response.json())
  .then((permission) => {
  var message = document.getElementById("message-container");
    if (Number.isInteger(permission[1])) {
      var missingPermissions = permission[0];
      var missing = permission[1];
      if (missing == 0) {
        message.innerText = "The permissions are all correctly setup. Nothing more needs doing.";
      } else if (missing == 1) {
        message.innerText = "There is "+missing+" permission missing. It is:";
      } else {
        message.innerText = "There are "+missing+" permissions missing. These are:";
      }
    } else {
      message.innerText = permission;
    }
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

function formatURLs(url, parameters) {
  var encodedParameters = new URLSearchParams(parameters);
  return `/${url}?${encodedParameters.toString()}`;
}
