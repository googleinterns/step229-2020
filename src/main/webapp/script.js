// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */


function initBody() {
    fetch('/data').then(console.log('Works'));
    checkPermissions();
}

function checkPermissions() {
    fetch('/check-permissions').then(console.log('it works'));
}