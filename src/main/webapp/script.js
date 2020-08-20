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
  fetch('/get-credentials?projID='+config.projectID+'&bucket='+config.bucketName+"&object="+config.objectName)
  .then(response => console.log("works"));
}

function checkPermissions() {
  fetch('/check-permissions?projID='+config.projectID)
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

function updateJobDatabase() {
  fetch('/jobs', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(accessDataflowAPI),
  });
  
}
