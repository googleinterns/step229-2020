// Copyright 2020 Google LLC

/**
 * @author andreeanica16
 * @author tblanshard
 */

var sdkVisited = false;

function initBody() {
  //document.getElementById('dataButtons').style.display = 'none';
  document.getElementById('projectID').value = config.projectID;
  setCredentialsServlet();
  google.charts.load('current', {'packages':['corechart']});
  google.charts.load('current', {
        'packages':['geochart'],
        'mapsApiKey': config.mapApiKey,
      });
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
        missingPermissionList += item + ', ';
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

        message.innerText = 'There is ' + missing + ' permission missing. It is: ' + missingPermissionList;
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
  const accessObject = {
      projectID : config.projectID, 
  };

  fetch('/jobs', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(accessObject),
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
  var isSDKSelected = (option == 'sdk');
  if (isSDKSelected) {
    document.getElementById('sdkAnalysis').style.display = 'block';
  } else {
    document.getElementById('sdkAnalysis').style.display = 'none';
  }
  var jobs = fetchAggregatedJobsBy(option);
  jobs.then(jobData => {
    if (isSDKSelected && !sdkVisited) {
      getOutdatedSDK(jobData);
      sdkVisited = true;
    }
    google.charts.setOnLoadCallback(
      drawPieChart(getTotalCosts(jobData), 'Total Cost of Jobs Per Category', 'totalCost-container')
    );
    google.charts.setOnLoadCallback(
      drawPieChart(getAverageCosts(jobData), 'Average Cost of Jobs Per Category', 'averageCost-container')
    );
    google.charts.setOnLoadCallback(getDailyView(jobData));
    google.charts.setOnLoadCallback(getFailedJobs(jobData));
    google.charts.setOnLoadCallback(getFailedJobsCost(jobData));
    google.charts.setOnLoadCallback(getAveragevCPUCount(jobData));
    google.charts.setOnLoadCallback(SSDVsHDDTimeComparison(jobData));
    if (option === 'region') {
      transformAgregatedDataforGeoChart(jobData);
      document.getElementById('hiddenLink').hidden = false;
      document.getElementById('regionDiv').style.display = 'inherit';
    } else {
      document.getElementById('hiddenLink').hidden = true;
      document.getElementById('regionDiv').style.display = 'none';
    }

    google.charts.setOnLoadCallback(SSDVsHDDComparison(jobData));
    document.getElementById('container').style.visibility = 'visible';    
  });
}

function getJobsFromProject(projectID) {
  fetch('/jobs?projectID=' + projectID)
  .then(response => response.json())
  .then(jobs => {
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
  console.log(aggregated)
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
  return data;
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
  return data;
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
    container.innerHTML = '<p id="noMoneyMessage">No money has been spent on failed jobs.</p>';
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
  thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() - 30);

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

  for (var i = 1; i < 4; i++) {
    var totalCost = data[1].slice(i, data[1].length).reduce((a, b) => a + b, 0);
    totalCost /= 30;
    data[0].push("Pred " + i);
    data[1].push(totalCost);
  }

  data = transpose(data);
  drawLineGraph(data, 'Cost Prediction On Daily Scale', 'costPrediction-container');  
}

function getWeeklyView(aggregated) {
  //find the moving average for 30 days worth of data
  //need to aggregate aggregated data to get groups of jobs run in the same week

  var today = new Date();
  var thirtyDaysFromNow = new Date(today);
  thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() - 30);

  var firstDayOfWeek = new Date(thirtyDaysFromNow);
  if (firstDayOfWeek.getDay() === 0) {
    firstDayOfWeek.setDate(firstDayOfWeek.getDate() - 6);
  } else {
    firstDayOfWeek.setDate(firstDayOfWeek.getDate() - (firstDayOfWeek.getDay() - 1));
  }
  

  var weekStarts = [];
  weekStarts.push(firstDayOfWeek.toLocaleDateString("en-US"));

  for (var i = 0; i < 4; i++) {
    weekStarts.push(new Date(firstDayOfWeek.setDate(firstDayOfWeek.getDate() + 7)).toLocaleDateString("en-US"));
  }

  var data = [];
  var titles = ['Category', ...weekStarts];
  data.push(titles);

  var dateDict = {};
  for (weekDate in weekStarts) {
    dateDict[weekStarts[weekDate]] = 0;
  }

  for (category in aggregated) {
    var totalCosts = {...dateDict};
    for (jobs in aggregated[category]) {
      var jobDate = new Date(aggregated[category][jobs].startTime);
      var weekStart = new Date(jobDate.getDate() - (jobDate.getDay() - 1));
      totalCosts[weekStart.toLocaleDateString("en-US")] += aggregated[category][jobs].price;
    }
    var totalCostsOrdered = [];
    totalCostsOrdered.push(category);
    for (date in weekStarts) {
      totalCostsOrdered.push(totalCosts[weekStarts[date]]);
    }
    data.push(totalCostsOrdered);
  } 

  var totalCost = data[1].slice(1, data[1].length).reduce((a, b) => a + b, 0);
  totalCost /= 4;
  data[0].push("Pred 1");
  data[1].push(totalCost);

  data = transpose(data);

  drawLineGraph(data, 'Cost Prediction On Weekly Scale', 'costPrediction-container');  
}

function getOutdatedSDK(aggregated) {
  var container = document.getElementById('sdkAnalysis');
  container.innerHTML += '<h3>The following jobs are using outdated SDKs.</h3>';
  for (outdatedJob in aggregated['STALE']) {
    container.innerHTML += '<p>' + JSON.stringify(aggregated['STALE'][outdatedJob].name).replace(/\"/g, "") + '</p>';
    container.innerHTML += '<p class="sdkDetails">' + JSON.stringify(aggregated['STALE'][outdatedJob].sdkName).replace(/\"/g, "") +
      ' (' + JSON.stringify(aggregated['STALE'][outdatedJob].sdk).replace(/\"/g, "") + ')<p>';
  }
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
  return data;
}

function SSDVsHDDComparison(aggregated) {
  var data = [];
  data.push(['Category','Average SSD Usage', 'Average HDD Usage']);
  for (category in aggregated) {
    var jobData = [];
    var ssd = 0;
    var hdd = 0;
    jobData.push(category);
    for (costs in aggregated[category]) {
      if (aggregated[category][costs].currentPDUsage == undefined) {
        hdd += 0;
      } else {
        hdd += aggregated[category][costs].currentPDUsage;
      }
      if (aggregated[category][costs].currentSSDUsage == undefined) {
        ssd += 0;
      } else {
        ssd += aggregated[category][costs].currentSSDUsage;
      }
    }
    ssd /= aggregated[category].length;
    hdd /= aggregated[category].length;

    jobData.push(ssd);
    jobData.push(hdd);
    data.push(jobData);
  }

  drawColumnChart(data, 'Comparison of SSD usage VS HDD usage', 'SSDVsHDD-container', true);
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
    width: '300%',
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
    isStacked: isStacked,
    legend: {
      position: 'bottom'
    },
  };
  var chart = new google.visualization.ColumnChart(document.getElementById(containerName));
  chart.draw(chartData, options);
}


function drawRegionsMap(array, aggregatedData) {
  var data = google.visualization.arrayToDataTable(array);

  var options = {
      displayMode: 'markers',
      'width':1300,
      'height':500,
      colorAxis: {colors: ['pink', 'darkRed']},
      chartArea: {width: '90%'}
  };

  var view = new google.visualization.DataView(data);
  view.setColumns([0, 
      {
          type: 'number',
          label: 'Area',
          calc: function (dt, row) {
              return {
                v: dt.getValue(row, 1),
                f: '' + dt.getValue(row, 2)
                }
          }
      }, 
      {
          type: 'number',
          label: 'Total Jobs:',
          calc: function (dt, row) {
              return {
                v: dt.getValue(row, 1),
                f: '' + dt.getValue(row, 1)
              }
      }}]);

  var chart = new google.visualization.GeoChart(document.getElementById('chartDiv'));

  google.visualization.events.addListener(chart, 'select', function () {
      var selection = chart.getSelection();
      if (selection.length > 0) {
        key = data.getValue(selection[0].row, 2);
        let allJobs = '';

        if (aggregatedData[key] !== undefined) {
          for (let i = 0; i < aggregatedData[key].length; i++) {
            allJobs = allJobs + aggregatedData[key][i].name + '\n';
          }
        }

        const additionalData = document.getElementById('additionalData');
        additionalData.innerText = allJobs; 
      }
    });

  chart.draw(view,  options);
}

function transformAgregatedDataforGeoChart(aggregatedData) {
  fetch('/regionToCity').then(response => response.json()).then(myMap => {
      console.log(aggregatedData);
    let array = [['City',   'NumberOfJobs', 'Area']];
    Object.keys(myMap).forEach((key) => {
      let size = 0;

      if (aggregatedData[key] !== undefined) {
        size = aggregatedData[key].length;
      }
      
      let auxArray = [];
      auxArray.push(myMap[key]);
      auxArray.push(size);
      auxArray.push(key);
      array.push(auxArray);
    });

    google.charts.setOnLoadCallback(drawRegionsMap(array, aggregatedData)); 
  });
}

module.exports = {getTotalCosts, getAverageCosts};