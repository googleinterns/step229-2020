// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

/*
//Commented as requires discussion with Andreea
document.getElementById('theform').onsubmit = function() { 
  var method = document.getElementById('aggregationMethod').value;
  var aggregationUrl = formatURLs("get-aggregated-data", {"method": method});
  fetch(aggregationUrl)
  .then(response => response.json())
  .then((jobs) => {
    getTotalCosts(jobs);
  });
  return false;
};*/

function initBody() {
  document.getElementById('dataButtons').style.display = 'none';
  setCredentialsServlet();
  google.charts.load('current', {'packages':['corechart']});
  google.charts.setOnLoadCallback(getTotalCosts);
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
  .then(jobs => {
      console.log(jobs);
      const table = document.getElementById('jobs');
      for (let i = 0; i < jobs.length; i++) {
          addJobToTable(jobs[i], table);
      }
      table.hidden = false;
  });
}

function addJobToTable(job, table) {
  const tableRow = document.createElement('tr');
    
  const jobName = document.createElement('td');
  jobName.innerText = job.name;
  tableRow.appendChild(jobName);

  const jobId = document.createElement('td');
  jobId.innerText = job.id;
  tableRow.appendChild(jobId);

  const jobType = document.createElement('td');
  jobType.innerText = job.type;
  tableRow.appendChild(jobType);

  const jobState = document.createElement('td');
  jobState.innerText = job.state;
  tableRow.appendChild(jobState);

  const jobStateTime = document.createElement('td');
  jobStateTime.innerText = job.stateTime;
  tableRow.appendChild(jobStateTime);

  const jobSDK = document.createElement('td');
  jobSDK.innerText = job.sdk + ' ' + job.sdkSupportStatus;
  tableRow.appendChild(jobSDK);

  const jobRegion = document.createElement('td');
  jobRegion.innerText = job.region;
  tableRow.appendChild(jobRegion);

  const jobWorkers = document.createElement('td');
  jobWorkers.innerText = job.currentWorkers;
  tableRow.appendChild(jobWorkers);

  const jobStartTime = document.createElement('td');
  jobStartTime.innerText = job.startTime;
  tableRow.appendChild(jobStartTime);

  const jobCPU = document.createElement('td');
  if (job.totalVCPUTime != undefined) {
    jobCPU.innerText = job.totalVCPUTime;
  }
  tableRow.appendChild(jobCPU);

  const jobMem = document.createElement('td');
  if (job.totalMemoryTime != undefined) {
    jobMem.innerText = job.totalMemoryTime;
  }
  tableRow.appendChild(jobMem);

  const jobHDD = document.createElement('td');
  if (job.totalDiskTimeHDD != undefined) {
    jobHDD.innerText = job.totalDiskTimeHDD;
  }
  tableRow.appendChild(jobHDD);

  const jobSSD = document.createElement('td');
  if (job.totalDiskTimeSSD != undefined)  {
    jobSSD.innerText = job.totalDiskTimeSSD;
  }
  tableRow.appendChild(jobSSD);

  const jobCount = document.createElement('td');
  if (job.currentVcpuCount != undefined) {
    jobCount.innerText = job.currentVcpuCount;
  }
  tableRow.appendChild(jobCount);

  const jobStream = document.createElement('td');
  if (job.enableStreamingEngine) {
    jobStream.innerText = job.totalStreamingData;
  } else {
    jobStream.innerText = '';
  }
  tableRow.appendChild(jobStream);

  const jobPrice = document.createElement('td');
  if (job.price != undefined) {
    jobPrice.innerText = job.price;
  }
  tableRow.appendChild(jobPrice);

  table.appendChild(tableRow);
}

function formatURLs(url, parameters) {
  var encodedParameters = new URLSearchParams(parameters);
  return `/${url}?${encodedParameters.toString()}`;
}

function getTotalCosts(jobs){
  //takes each of the jobs and finds the total cost of each aggregated group of jobs
  var data = [];
  for (job of jobs) {
    var jobData = [];
    jobData.push(job[0]);
    var totalCost = job[1].reduce(function(a, b) {
      return a.jobPrice + b.jobPrice;
    }, 0);
    jobData.push(totalCost);
    data.push(jobData);
  }
  //test data
  //var data = google.visualization.arrayToDataTable([["Category", "Data"],["Person 1", 10],["Person 2", 50],["Person 3", 100]]);
  var chartData = google.visualization.arrayToDataTable(data);
  var options = {
    title: "Total Cost of Jobs Per Category"
  };
  var chart = new google.visualization.PieChart(document.getElementById('totalCost'));
  chart.draw(data, options);
}
