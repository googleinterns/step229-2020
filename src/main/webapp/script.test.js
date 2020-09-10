const {getTotalCosts, getAverageCosts, getFailedJobs, getFailedJobsCost} = require('./script');

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