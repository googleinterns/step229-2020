// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

function initBody() {
  document.getElementById('dataButtons').style.display = 'none';
  setCredentialsServlet();
}

function setCredentialsServlet() {
  var credentialsUrl = formatURLs("get-credentials", {"projID":config.projectID, "bucket":config.bucketName, "object":config.objectName});
  fetch(credentialsUrl)
  .then(response => checkPermissions());
}

function checkPermissions() {
  var permissionsUrl = formatURLs("check-permissions", {"projID":config.projectID});
  fetch(permissionsUrl)
  .then(response => response.json())
  .then((permission) => {
  var message = document.getElementById("message-container");
    if (Number.isInteger(permission[1])) {
      var missingPermissions = permission[0];
      missingPermissionList = "";
      for (item of missingPermissions) {
        missingPermissionList += item+"\n";
      }
      var missing = permission[1];
      if (missing == 0) {
        message.innerText = "The permissions are all correctly setup. Nothing more needs doing.";
        document.getElementById('dataButtons').style.display = 'block';
      } else if (missing == 1) {
        message.innerText = "There is "+missing+" permission missing. It is:\n"+missingPermissionList;
      } else {
        message.innerText = "There are "+missing+" permissions missing. These are:\n"+missingPermissionList;
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
