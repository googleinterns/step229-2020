const {getTotalCosts} = require('./script');

test('Test getTotalCosts', () => {
  expect(getTotalCosts("{}")).toBe("{}");
});