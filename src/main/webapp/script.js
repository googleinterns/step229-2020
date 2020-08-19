// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

let arePermissionsCorrect;

function initBody() {
    checkPermissions();
}

function checkPermissions() {
    fetch('/check-permissions?projID='+config.projectID+'&saID='+config.serviceAccount)
    .then(response => response.json())
    .then((permission) => {
      arePermissionsCorrect = permission;
      var message = document.getElementById("message-container");
      if (arePermissionsCorrect) {
        message.innerText = "The permissions are all correctly setup. Nothing more needs doing.";
      } else {
        message.innerText = "There are some permissions missing.";
      }      
    });
}
