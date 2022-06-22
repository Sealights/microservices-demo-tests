const axios = require("axios");
const chai = require("chai");
const { expect } = require("chai");

const chaiAsPromised = require('chai-as-promised');
chai.use(chaiAsPromised);

const endpoint = process.env.TEST_ENDPOINT || `http://boutique.dev.sealights.co:8080`

const end1 = `${endpoint}/`;
const end2 = `${endpoint}/cart`;
const end3 = `${endpoint}/cart/checkout`;
const end4 = `${endpoint}/product/1YMWWN1N4O111122222`;
const end5 = `${endpoint}/product/66VCHSJNUP`;
const end6 = `${endpoint}/incorrectroute`;

describe("Test online boutique", () => {
	afterEach(async () => {
		// needs to add just for specific case of tests working.
		// could be deleted in case of it's useless
		await new Promise(res => setTimeout(res, 20 * 1000))
	})

	it("Request of main page should return HTML", async () => {
		const { status, data } = await axios.get(end1);

		expect(status).to.equal(200);
		expect(data).to.contain("DOCTYPE");
	});

	it("It should create order", async () => {
		const awaitingResponse = axios
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

		await expect(awaitingResponse).to.be.rejected
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
});
