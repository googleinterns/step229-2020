// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

function initBody() {
    checkPermissions();
}

function checkPermissions() {
  fetch('/check-permissions?projID='+configLogs.projectID)
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
