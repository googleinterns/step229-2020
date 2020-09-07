// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

function initBody() {
  //document.getElementById('dataButtons').style.display = 'none';
  document.getElementById('projectID').value = accessDataflowAPI.projectID;
  setCredentialsServlet();
  google.charts.load('current', {'packages':['corechart']});
}

function setCredentialsServlet() {
  var credentialsUrl = formatURLs('get-credentials', {'projID':config.projectID, 'bucket':config.bucketName, 'object':config.objectName});
  fetch(credentialsUrl)
  .then(response => checkPermissions());
}

function checkPermissions() {
  var permissionsUrl = formatURLs('check-permissions', {'projID':config.projectID});
  fetch(permissionsUrl)
  .then(response => response.json())
  .then((permission) => {
  var message = document.getElementById('permissionDialog');
    if (Number.isInteger(permission[1])) {
      var missingPermissions = permission[0];
      missingPermissionList = '';
      for (item of missingPermissions) {
        missingPermissionList += item+', ';
      }
      var missing = permission[1];
      if (missing == 0) {
        const button = document.getElementById('allPermisionsCorrect');
        button.hidden = false;

        message.innerText = 'The permissions are all correctly setup. Nothing more needs doing.';
        //document.getElementById('dataButtons').style.display = 'block';
      } else if (missing == 1) {
        const button = document.getElementById('showMissingPermision');
        button.hidden = false;

        message.innerText = 'There is '+missing+' permission missing. It is: '+missingPermissionList;
      } else {
        const button = document.getElementById('showMissingPermision');
        button.hidden = false;

        message.innerText = 'There are ' + missing + ' permissions missing. These are: ' + missingPermissionList;
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

/**
 * Sends a GET request to the AggregationDataServlet to fetch the jobs 
 * aggregated by the option checked by the user
 */

function fetchAggregatedJobsBy(option) {
  var aggregationUrl = formatURLs('get-aggregated-data', {'projectID': config.projectID, 'option': option});
  return fetch(aggregationUrl)
  .then(response => response.json())
  .then(jobs => {
    return jobs;
  });
}

function setUpGraphs() {
  var option = document.querySelector('input[name = option]:checked').value;
  var jobs = fetchAggregatedJobsBy(option);
  jobs.then(jobData => {
    google.charts.setOnLoadCallback(getTotalCosts(jobData));
    google.charts.setOnLoadCallback(getAverageCosts(jobData));
    google.charts.setOnLoadCallback(getDailyView(jobData));
    google.charts.setOnLoadCallback(getFailedJobs(jobData));
    google.charts.setOnLoadCallback(getFailedJobsCost(jobData));
    google.charts.setOnLoadCallback(getAveragevCPUCount(jobData));
    google.charts.setOnLoadCallback(SSDVsHDDTimeComparison(jobData));
    document.getElementById('container').style.visibility = 'visible';    
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
  jobSDK.innerText = job.sdk + ' ' + job.sdkSupportStatus + ' ' +
                         job.sdkName;
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

function getTotalCosts(aggregated){
  //takes each of the jobs and finds the total cost of each aggregated group of jobs
  var data = [];
  data.push(['Category','Total Cost']);
  for (category in aggregated) {
    var totalCost = 0;
    var jobData = [];
    jobData.push(category);
    for (costs in aggregated[category]) {
      totalCost += aggregated[category][costs].price;
    }
    jobData.push(totalCost);
    data.push(jobData);
  }
  drawPieChart(data, 'Total Cost of Jobs Per Category', 'totalCost-container');
}

function getAverageCosts(aggregated) {
  //takes each of the jobs and finds the total cost of each aggregated group of jobs
  var data = [];
  data.push(['Category','Average Cost']);
  for (category in aggregated) {
    var totalCost = 0;
    var jobData = [];
    jobData.push(category);
    for (costs in aggregated[category]) {
      totalCost += aggregated[category][costs].price;
    }
    totalCost /= aggregated[category].length;
    jobData.push(totalCost);
    data.push(jobData);
  }
  drawPieChart(data, 'Average Cost of Jobs Per Category', 'averageCost-container');  
}

function getFailedJobs(aggregated){
  //takes each of the jobs and finds the total number of failed jobs within each aggregated group of jobs
  var data = [];
  data.push(['Category','Total Count']);
  for (category in aggregated) {
    var count = 0;
    var jobData = [];
    jobData.push(category);
    for (costs in aggregated[category]) {
      console.log(aggregated[category][costs]);
      if (aggregated[category][costs].state == 'JOB_STATE_FAILED') {
        count ++;
      }
    }
    jobData.push(count);
    data.push(jobData);
  }
  if (data[1][1] == 0 && data.length == 2) {
    var container = document.getElementById('failedJobsCost-container');
    container.innerText = "There are no failed jobs.";
  } else {
    drawPieChart(data, 'Total Number of Failed Jobs Per Category', 'failedJobs-container');
  }
}

function getFailedJobsCost(aggregated) {
  //takes each of the failed jobs within each aggregated group and finds the total cost for each group
  var data = [];
  data.push(['Category','Total Cost']);
  var isAllZero = true;
  for (category in aggregated) {
    var count = 0;
    var failedCost = 0;
    var jobData = [];
    jobData.push(category);
    for (costs in aggregated[category]) {
      if (aggregated[category][costs].state == 'JOB_STATE_FAILED') {
        count ++;
        failedCost += aggregated[category][costs].price;
      }
    }
    failedCost /= count;
    jobData.push(failedCost);
    data.push(jobData);
    isAllZero = isAllZero && ((failedCost == 0) || isNaN(failedCost));
  }
  if (isAllZero) {
    var container = document.getElementById('failedJobsCost-container');
    container.innerText = "No money has been spent on failed jobs.";
  } else {
    drawPieChart(data, 'Total Cost of Failed Jobs Per Category', 'failedJobsCost-container');
  }
}

function dailyViewHandler() {
  var option = document.querySelector('input[name = option]:checked').value;
  var jobs = fetchAggregatedJobsBy(option);
  jobs.then(jobData => {
    google.setOnLoadCallback(getDailyView(jobData));
  });
}

function weeklyViewHandler() {
  var option = document.querySelector('input[name = option]:checked').value;
  var jobs = fetchAggregatedJobsBy(option);
  jobs.then(jobData => {
    google.setOnLoadCallback(getWeeklyView(jobData));
  });
}

function getDatesBetweenDates(endDate, startDate) {
  let dates = [];
  var theDate = new Date(startDate);
  while (theDate < endDate) {
    dates = [...dates, new Date(theDate).toLocaleDateString("en-US")];
    theDate.setDate(theDate.getDate() + 1);
  }
  return dates;
}

function transpose(array) {
  return Object.keys(array[0]).map(function(column) {
      return array.map(function(row) { return row[column]; });
  });
}

function getDailyView(aggregated) {
  //find the moving average for 30 days worth of data
  //need to aggregate aggregated data to get groups of jobs run on the same day


  var today = new Date();
  var thirtyDaysFromNow = new Date(today);
  thirtyDaysFromNow.setDate( thirtyDaysFromNow.getDate() - 30);

  var dateList = getDatesBetweenDates(today, thirtyDaysFromNow);

  var dateDict = {};
  for (date in dateList) {
    dateDict[dateList[date]] = 0;
  }
  
  var data = [];
  var titles = ['Category', ...dateList];
  data.push(titles);
  
  for (category in aggregated) {
    var totalCosts = {...dateDict};
    for (jobs in aggregated[category]) {
      totalCosts[new Date(aggregated[category][jobs].startTime).toLocaleDateString("en-US")] += aggregated[category][jobs].price;
    }
    var totalCostsOrdered = [];
    totalCostsOrdered.push(category);
    for (date in dateList) {
      totalCostsOrdered.push(totalCosts[dateList[date]]);
    }
    data.push(totalCostsOrdered);
  }

  data = transpose(data);


  /*
  for (category in aggregated) {
    for (dates in aggregated[category]) {
      console.log(new Date(aggregated[category][dates].startTime));
    }
  }*/
  
  /*
  var data = [];
  //set up the headers section
  data.push(['Date']);
  for (category in aggregated) {
    data[0].push(category);
    var totalCost = 0;
    var jobData = [];
    jobData.push(category);
    for (costs in aggregated[category]) {
      totalCost += aggregated[category][costs].price;
    }
    totalCost /= aggregated[category].length;
    jobData.push(totalCost);
    data.push(jobData);
  }
  */
  /*for (job of jobs) {
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
    jobs.push(['Future Day '+i, prediction])
  }
  data = jobs;
  data.unshift(['Category', 'Average Cost']);
  */
  //test data
  /*var data = [
          ['Year', 'Sales', 'Expenses'],
          ['2004',  1000,      400],
          ['2005',  1170,      460],
          ['2006',  660,       1120],
          ['2007',  1030,      540]
        ];
  */
  drawLineGraph(data, 'Cost Prediction On Daily Scale', 'costPrediction-container');  
}

function getWeeklyView() {
  //find the moving average for 30 days worth of data
  //need to aggregate aggregated data to get groups of jobs run on the same day
  
  /*for (job of jobs) {
    var dailyAverage = job[1].reduce(function(a, b) {
        return a.jobPrice + b.jobPrice;
    }, 0);
    job[1] = dailyAverage / job[1].length;
  }
  for (var i = 0; i < 1; i++) {
    var prediction = jobs.slice(i, jobs.length).reduce(function(a, b) {
      return a + b;
    }, 0);
    prediction /= (jobs.length - i);
    jobs.push(['Future Week '+i, prediction])
  }
  data = jobs;
  data.unshift(['Category', 'Average Cost']);
*/
  var data = [
          ['Year', 'Sales', 'Expenses'],
          ['2004',  1000,      400],
          ['2005',  1170,      460],
          ['2006',  660,       1120],
          ['2007',  1030,      540]
        ];
 
  drawLineGraph(data, 'Cost Prediction On Weekly Scale', 'costPrediction-container');  
}

function getAveragevCPUCount(aggregated) {
  var data = [];
  data.push(['Category','Average Count']);
  for (category in aggregated) {
    var totalCount = 0;
    var jobData = [];
    jobData.push(category);
    for (costs in aggregated[category]) {
      if (aggregated[category][costs].currentVcpuCount == undefined) {
        totalCount += 0;
      } else {
        totalCount += aggregated[category][costs].currentVcpuCount;
      }
    }
    totalCount /= aggregated[category].length;
    jobData.push(totalCount);
    data.push(jobData);
  }
  drawPieChart(data, 'Average vCPU Usage', 'vCPU-container');
}

function SSDVsHDDTimeComparison(aggregated) {
  /*var data = [];
  data.push(['Category','Average SSD Time', 'Average HDD Time']);
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
  }*/
  //test data
  /* data = [
        ['Genre', 'Fantasy & Sci Fi', 'Romance', 'Mystery/Crime', 'General',
         'Western', 'Literature', { role: 'annotation' } ],
        ['2010', 10, 24, 20, 32, 18, 5, ''],
        ['2020', 16, 22, 23, 30, 16, 9, ''],
        ['2030', 28, 19, 29, 30, 12, 13, '']
      ];
  */
  var data = [];
  data.push(['Category','Average SSD Time', 'Average HDD Time']);
  for (category in aggregated) {
    var jobData = [];
    var ssdTime = 0;
    var hddTime = 0;
    jobData.push(category);
    for (costs in aggregated[category]) {
      if (aggregated[category][costs].totalDiskTimeHDD == undefined) {
        hddTime += 0;
      } else {
        hddTime += aggregated[category][costs].totalDiskTimeHDD;
      }
      if (aggregated[category][costs].totalDiskTimeSSD == undefined) {
        ssdTime += 0;
      } else {
        ssdTime += aggregated[category][costs].totalDiskTimeSSD;
      }
    }
    ssdTime /= aggregated[category].length;
    hddTime /= aggregated[category].length;
    ssdTime /= 3600;
    hddTime /= 3600;
    jobData.push(ssdTime);
    jobData.push(hddTime);
    data.push(jobData);
  }

  drawColumnChart(data, 'Comparison of SSDTime VS HDDTime', 'SSDVsHDDTime-container', true);
}

function drawLineGraph(data, title, containerName) {
  var chartData = google.visualization.arrayToDataTable(data);
  var options = {
    chartArea: {
      // leave room for y-axis labels
      width: '80%'
    },
    legend: {
      position: 'top'
    },
    //height: 250,
    width: '100%',
    title: title,
    curveType: 'function',
    overflow: 'hidden',
    vAxis: {
      minValue:0,
      viewWindow: {
        min: 0
      }
    }
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