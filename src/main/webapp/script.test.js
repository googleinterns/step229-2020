const {getTotalCosts, getAverageCosts, getFailedJobs, getFailedJobsCost,
  getAveragevCPUCount, SSDVsHDDTimeComparison} = require('./script');

var testAggregatedCostInput = '{ "testData" : [' +
  '{ "price":0.014105724489430654 },' +
  '{ "price":0.014105724489430655 },' +
  '{ "price":0.014105724489430656 }]}';
testAggregatedCostInput = JSON.parse(testAggregatedCostInput);

var testAggregatedFailedInput = '{ "testData" : [' +
  '{ "state":"JOB_STATE_FAILED", "price":0.014105724489430654 },' +
  '{ "state":"JOB_STATE_CANCELLED", "price":0.014105724489430655 },' +
  '{ "state":"JOB_STATE_FAILED", "price":0.014105724489430656 }]}';
testAggregatedFailedInput = JSON.parse(testAggregatedFailedInput);

var testAveragevCPUCountInput = '{ "testData" : [' +
  '{ "currentVcpuCount":2 },' +
  '{ "currentVcpuCount":4 },' +
  '{ "currentVcpuCount":1 }]}';
testAveragevCPUCountInput = JSON.parse(testAveragevCPUCountInput);

var testAverageSSDVsHDDInput = '{ "testData" : [' +
  '{ "totalDiskTimeSSD":2, "totalDiskTimeHDD": 1},' +
  '{ "totalDiskTimeSSD":4, "totalDiskTimeHDD": 3},' +
  '{ "totalDiskTimeSSD":1, "totalDiskTimeHDD": 2}]}';
testAverageSSDVsHDDInput = JSON.parse(testAverageSSDVsHDDInput);

test('Test getTotalCosts', () => {
  const result = [["Category", "Total Cost"],["testData", 0.04231717346829197]]
  expect(getTotalCosts(testAggregatedCostInput)).toStrictEqual(result);
});

test('Test getAverageCosts', () => {
  const result = [["Category", "Average Cost"],["testData", 0.014105724489430656]]
  expect(getAverageCosts(testAggregatedCostInput)).toStrictEqual(result);
});

test('Test getFailedJobs', () => {
  const result = [["Category", "Total Count"],["testData", 2]]
  expect(getFailedJobs(testAggregatedFailedInput)).toStrictEqual(result);
});

test('Test getFailedJobsCost', () => {
  const result = [["Category", "Total Cost"],["testData", 0.02821144897886131]]
  expect(getFailedJobsCost(testAggregatedFailedInput)).toStrictEqual(result);
});

test('Test getFailedJobsCost', () => {
  const result = [["Category", "Total Cost"],["testData", 0.02821144897886131]]
  expect(getFailedJobsCost(testAggregatedFailedInput)).toStrictEqual(result);
});

test('Test getAveragevCPUCount', () => {
  const result = [['Category','Average Count'],["testData", 2.3333333333333335]]
  expect(getAveragevCPUCount(testAveragevCPUCountInput)).toStrictEqual(result);
});

test('Test SSDVsHDDTimeComparison', () => {
  const result = [['Category','Average SSD Time', 'Average HDD Time'],["testData", ((7/3)/3600), 2/3600]]
  expect(SSDVsHDDTimeComparison(testAverageSSDVsHDDInput)).toStrictEqual(result);
});