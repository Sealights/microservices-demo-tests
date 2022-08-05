const axios = require("axios");
const chai = require("chai");
const { expect } = require("chai");

const chaiAsPromised = require('chai-as-promised');
const puppeteer = require("puppeteer");
chai.use(chaiAsPromised);

const endpoint = process.env.TEST_ENDPOINT || `http://aa5174ca5746b43a39c0ddb2b2d1da06-1798784314.us-east-2.elb.amazonaws.com`

const end1 = `${endpoint}/`;
const end2 = `${endpoint}/cart`;
const end3 = `${endpoint}/cart/checkout`;
const end4 = `${endpoint}/product/1YMWWN1N4O111122222`;
const end5 = `${endpoint}/product/66VCHSJNUP`;
const end6 = `${endpoint}/incorrectroute`;
const end7 = `${endpoint}/product/`;

const wait = (time = 1000) => {
	return new Promise(res => setTimeout(res, time))
}

const products = [
	'0PUK6V6EV0',
	'1YMWWN1N4O',
	'2ZYFJ3GM2N',
	'66VCHSJNUP',
	'6E92ZMYYFZ',
	'9SIQT8TOJO',
	'L9ECAV7KIM',
	'LS4PSXUNUM',
	'OLJCESPC7Z'
]

describe("Test online boutique", () => {
	beforeEach(async () => {
		await wait(0)
	})

	it("Request of main page should return HTML", async () => {
		const { status, data } = await axios.get(end1);

		expect(status).to.equal(200);
		expect(data).to.contain("DOCTYPE");
	});

	it("Get product page", async () => {
		const { status, data } = await axios.get(end7 + products[8]);

		expect(status).to.equal(200);
		expect(data).to.contain("DOCTYPE");
	});

	it("It should create order", async () => {
		await axios.get(end7 + products[8]);
		const { status, data }  = await axios
			.post(end3, {
				payload: {
					email: "someone@example.com",
					street_address: "1600 Amphitheatre Parkway",
					zip_code: "94043",
					city: "Mountain View",
					state: "CA",
					country: "United States",
					credit_card_number: "4432-8015-6152-0454",
					credit_card_expiration_month: "1",
					credit_card_expiration_year: "2039",
					credit_card_cvv: "672",
				},
			})

		expect(status).to.equal(200);
		expect(data).to.contain("DOCTYPE");
	});

	it("Should not find product", async () => {
		const { response } = await axios.get(end4).catch((err) => err);

		expect(response.status).to.equal(500);
		expect(response.statusText).to.equal("Internal Server Error");
	});

	it("Request of wrong page should return 404", async () => {
		const { response } = await axios
			.get(end6)
			.catch((err) => err);

		expect(response.status).to.equal(404);
		expect(response.data).to.equal("404 page not found\n");
		expect(response.statusText).to.equal("Not Found");
	});

	it("It should create order using browser", async () => {
		const browser = await puppeteer.launch({
			args: ['--no-sandbox'],
			headless: true,
			ignoreHTTPSErrors: true,
		});


		const page = await browser.newPage()
		await page.setCacheEnabled(false);
		await page.goto(end5, {
			waitUntil: 'networkidle0',
		})

		const productCardSelector1 = 'body > main > div.container-fluid > div > div.col-12.col-lg-8 > div.row.hot-products-row.px-xl-6 > div:nth-child(2) > a'
		const productCardSelector2 = 'body > main > div.container-fluid > div > div.col-12.col-lg-8 > div.row.hot-products-row.px-xl-6 > div:nth-child(6) > a'
		const productCardSelector3 = 'body > main > div.container-fluid > div > div.col-12.col-lg-8 > div.row.hot-products-row.px-xl-6 > div:nth-child(7) > a'

		const errorSelector = 'body > main > div > div > p:nth-child(2)'

		const addProductSelector = 'body > main > div.h-product.container > div > div.product-info.col-md-5 > div > form > button'
		const placeOrderSelector = 'body > main > section > div > div.col-lg-5.offset-lg-1.col-xl-4 > form > div.form-row.justify-content-center > div > button'
		const continueShoppingSelector = 'body > main > section.container.order-complete-section > div:nth-child(5) > div > a'

		try {
			await page.click(addProductSelector)
			await page.waitForSelector(placeOrderSelector)
			await page.click(placeOrderSelector)
			await page.waitForSelector(continueShoppingSelector)
			await page.click(continueShoppingSelector)
		} catch (e) {
		} finally {
			await browser.close()
		}
	});
});
