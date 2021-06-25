var synthetics = require('Synthetics');
const log = require('SyntheticsLogger');

const recordedScript = async function () {
  let page = await synthetics.getPage();

  const navigationPromise = page.waitForNavigation()

  await synthetics.executeStep('Go to home page', async function() {
    await page.goto(process.env.TARGET_URL, {waitUntil: 'domcontentloaded', timeout: 30000})
  })

  await page.setViewport({ width: 1853, height: 949 })

  await navigationPromise

  await synthetics.executeStep('Click "Login" button', async function() {
    await page.waitForSelector('.container > .section > .container > div > .btn')
    await page.click('.container > .section > .container > div > .btn')
  })

  await navigationPromise


  await synthetics.executeStep('Type username', async function() {
    await page.type('div:nth-child(2) > div > div > .cognito-asf #signInFormUsername', process.env.USER_NAME)
  })

  await synthetics.executeStep('Type password', async function() {
    await page.type('div:nth-child(2) > div > div > .cognito-asf #signInFormPassword', process.env.PASSWORD)
  })

  await synthetics.executeStep('Submit login form', async function() {
    await page.waitForSelector('div:nth-child(2) > div > div > .cognito-asf > .btn')
    await page.click('div:nth-child(2) > div > div > .cognito-asf > .btn')
  })

  await navigationPromise

  await synthetics.executeStep('Click "Create Todo" button', async function() {
    await page.waitForSelector('.row > .col-sm-6:nth-child(2) > .card > .card-body > .btn')
    await page.click('.row > .col-sm-6:nth-child(2) > .card > .card-body > .btn')
  })

  await navigationPromise

  await synthetics.executeStep('Type todo title', async function() {
    await page.type('.row #title', "Test")
  })

  await synthetics.executeStep('Type todo description', async function() {
    await page.type('.row #description', "Test")
  })

  await synthetics.executeStep('Type todo priority', async function() {
    await page.type('.row #priority', "HIGH")
  })

  await synthetics.executeStep('Type todo due date', async function() {
    await page.type('.row #dueDate', "01022100")
  })

  await synthetics.executeStep('Submit todo form', async function() {
    await page.waitForSelector('.section > .row > .col-md-6 > #todo-form > .btn')
    await page.click('.section > .row > .col-md-6 > #todo-form > .btn')
  })

  await navigationPromise

  await synthetics.executeStep('Check if todo exists on dashboard', async function() {
      // Puppeteer API: https://github.com/puppeteer/puppeteer/blob/v1.14.0/docs/api.md#pageselector)
      let tableRow = await page.waitForSelector('.section > .container > .table > tbody > tr')
      if(!tableRow){
          synthetics.addExecutionError('Cannot find created todo on Dashboard!');
      }
  })

  await synthetics.executeStep('Click "Delete" button', async function() {
    await page.waitForSelector('tbody > tr > td > .btn-danger > .far')
    await page.click('tbody > tr > td > .btn-danger > .far')
  })

  await synthetics.executeStep('Click "Confirm Delete" button', async function() {
    await page.waitForSelector('#confirm-delete #confirm-deletion')
    await page.click('#confirm-delete #confirm-deletion')
  })


  await navigationPromise


  await synthetics.executeStep('Click "Logout" button', async function() {
    await page.waitForSelector('#navbarSupportedContent > .navbar-nav > .nav-item > form > .btn')
    await page.click('#navbarSupportedContent > .navbar-nav > .nav-item > form > .btn')
  })

  await navigationPromise

};
exports.handler = async () => {
    return await recordedScript();
};
