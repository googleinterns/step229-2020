const {getTotalCosts, getAverageCosts, getFailedJobs} = require('./script');

var testAggregatedCostInput = '{ "testData" : [' +
  '{ "price":0.014105724489430654 },' +
  '{ "price":0.014105724489430655 },' +
  '{ "price":0.014105724489430656 }]}';
testAggregatedCostInput = JSON.parse(testAggregatedCostInput);

var testAggregatedFailedInput = '{ "testData" : [' +
  '{ "state":"JOB_STATE_FAILED" },' +
  '{ "state":"JOB_STATE_CANCELLED" },' +
  '{ "state":"JOB_STATE_FAILED" }]}';
testAggregatedFailedInput = JSON.parse(testAggregatedFailedInput);

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