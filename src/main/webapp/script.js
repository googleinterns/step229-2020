// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

let projectID;
let serviceAccountID;

function initBody() {
    fetch('/data').then(console.log('Works'));
    projectID = "bt-dataflow-sql-demo";
    serviceAccountID = "dataflow-service-analyser-sa@bt-dataflow-sql-demo.iam.gserviceaccount.com";
    checkPermissions();
}

function checkPermissions() {
    fetch('/check-permissions?projID='+projectID+'&saID='+serviceAccountID, {method:"GET"})
    .then(response => response.json())
    .then((accounts) => {
      console.log(accounts);  
    });
}