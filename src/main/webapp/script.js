// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

let projectID;

function initBody() {
    fetch('/data').then(console.log('Works'));
    projectID = "bt-dataflow-sql-demo";
    checkPermissions();
}

function checkPermissions() {
    fetch('/check-permissions?projID='+projectID, {method:"GET"})
    .then(response => response.json())
    .then((accounts) => {
      console.log(accounts);  
    });
}