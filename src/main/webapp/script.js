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
  google.charts.setOnLoadCallback(getFailedJobs);
  google.charts.setOnLoadCallback(predictCostWeek);
  google.charts.setOnLoadCallback(getAveragevCPUCount);
  google.charts.setOnLoadCallback(SSDVsHDDTimeComparison);
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

function getTotalCosts(){
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
  //var data = [["Category", "Data"],["Person 1", 10],["Person 2", 50],["Person 3", 100]];
  drawPieChart(data, "Total Cost of Jobs Per Category", "totalCost-container");
}

function getFailedJobs(){
  //takes each of the jobs and finds the total number of failed jobs within each aggregated group of jobs
  var data = [];
  for (job of jobs) {
    var failed = 0;
    var jobData = [];
    jobData.push(job[0]);
    for (var i = 0; i < job[1].length; i++) {
      //check that it is jobState we need
      if (job[i].jobState == "Failed") {
        failed ++;
      }
    }
    jobData.push(failed);
    data.push(jobData);
  }
  //test data
  //var data = [["Category", "Data"],["Person 1", 10],["Person 2", 50],["Person 3", 100]];
  drawPieChart(data, "Total Number of Failed Jobs Per Category", "failedJobs-container");
}

function predictCostWeek() {
  //find the moving average for 30 days worth of data
  //need to aggregate aggregated data to get groups of jobs run on the same day
  
  for (job of jobs) {
    var dailyAverage = job[1].reduce(function(a, b) {
        return a.jobPrice + b.jobPrice;
    }, 0);
    job[1] = dailyAverage / job[1].length;
  }
  for (var i = 0; i < 3; i++) {
    var prediction = jobs.slice(i, jobs.length).reduce(function(a, b) {
      return a + b;
    }, 0);
    prediction /= (jobs.length - i);
    jobs.push(["Future Day "+i, prediction])
  }
  data = jobs

 /* var data = [
          ['Year', 'Sales', 'Expenses'],
          ['2004',  1000,      400],
          ['2005',  1170,      460],
          ['2006',  660,       1120],
          ['2007',  1030,      540]
        ];
  */
  drawLineGraph(data, "Cost Prediction On Daily Scale", "costPredictionDaily-container");
}

function getAveragevCPUCount() {
  //takes each of the jobs and finds the total cost of each aggregated group of jobs
  var data = [];
  for (job of jobs) {
    var jobData = [];
    jobData.push(job[0]);
    var vCPUCount = job[1].reduce(function(a, b) {
      return a.currentVcpuCount + b.currentVcpuCount;
    }, 0);
    vCPUCount /= job[1].length;
    jobData.push(vCPUCount);
    data.push(jobData);
  }
  //test data
  //var data = [["Category", "Data"],["Person 1", 10],["Person 2", 50],["Person 3", 100]];
  drawPieChart(data, "Average vCPU Usage", "vCPU-container");
}

function SSDVsHDDTimeComparison() {
  var data = [];
  for (job of jobs) {
    var jobData = [];
    var numberJobs = job[1].length;
    jobData.push(job[0]);
    var ssdCount = job[1].reduce(function(a, b) {
      return a.totalDiskTimeSSD + b.totalDiskTimeSSD;
    }, 0);
    var hddCount = job[1].reduce(function(a, b) {
      return a.totalDiskTimeHDD + b.totalDiskTimeHDD;
    }, 0);
    ssdCount /= numberJobs;
    hddCount /= numberJobs;
    jobData.push(ssdCount);
    jobData.push(hddCount);
    data.push(jobData);
  }
  //test data
  /*var data = [
        ['Genre', 'Fantasy & Sci Fi', 'Romance', 'Mystery/Crime', 'General',
         'Western', 'Literature', { role: 'annotation' } ],
        ['2010', 10, 24, 20, 32, 18, 5, ''],
        ['2020', 16, 22, 23, 30, 16, 9, ''],
        ['2030', 28, 19, 29, 30, 12, 13, '']
      ];
  */
  drawColumnChart(data, "Comparison of SSDTime VS HDDTime", "SSDVsHDDTime-container", true);
}

function drawLineGraph(data, title, containerName) {
  var chartData = google.visualization.arrayToDataTable(data);
  var options = {
    title: title,
    curveType: 'function',
    trendlines: { 0: {} } 
  }
  var chart = new google.visualization.LineChart(document.getElementById(containerName));
  chart.draw(chartData, options);
}

function drawPieChart(data, title, containerName) {
  var chartData = google.visualization.arrayToDataTable(data);
  var options = {
    title: title
  };
  var chart = new google.visualization.PieChart(document.getElementById(containerName));
  chart.draw(chartData, options);
}

function drawColumnChart(data, title, containerName, isStacked) {
  var chartData = google.visualization.arrayToDataTable(data);
  var options = {
    title: title,
    isStacked: isStacked
  };
  var chart = new google.visualization.ColumnChart(document.getElementById(containerName));
  chart.draw(chartData, options);
}