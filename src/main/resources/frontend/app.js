
	const API_URL = "http://localhost:8080";

	// Load data automatically when page opens
	window.onload = function () {
		loadCustomers();
	};

	// ================= CREATE CUSTOMER =================
	async function createCustomer() {
    // async = function works with promises (non-blocking code)
    // allows using await for HTTP requests
    const customer = {
    	firstName: document.getElementById("firstName").value,
    	lastName: document.getElementById("lastName").value,
    	birthDate: document.getElementById("birthDate").value,
    	gender: document.getElementById("gender").value
    };
    // Reads values from HTML input fields
    // document.getElementById = access DOM element
    // .value = user input text

    // Send POST request to backend
    const res = await fetch(`${API_URL}/customers`, {
    	method: "POST",
    	headers: { "Content-Type": "application/json" },
    	body: JSON.stringify(customer)
    });
    // fetch() sends HTTP request to backend
    // POST = create new resource
    // JSON.stringify converts JS object → JSON string
    // Content-Type tells backend data format

		// Simple error handling
		if (!res.ok) {
			alert("Error creating customer");
			return;
		}
//		in CustomerEndpoints:
//		if (created) {
//        	ctx.status(201).json(customer); = res.ok

		// Refresh table after creation
		loadCustomers();
	}

	// ================= LOAD CUSTOMERS =================
	// is called when webpage reloads & when we add a customer
	async function loadCustomers() {

		const res = await fetch(`${API_URL}/customers`); // get meth. by def.
		const data = await res.json(); // the actual data (all customers) in json format

		const table = document.getElementById("customerTable");
		table.innerHTML = "";

		// Render table rows dynamically, c is each customer
		data.forEach(c => {
			table.innerHTML += `
				<tr>
					<td>${c.firstName}</td>
					<td>${c.lastName}</td>
					<td>${c.birthDate}</td>
					<td>${c.gender}</td>
				</tr>
			`;
		});
		// forEach = loop through array
        // template string `...` allows embedding variables ${}
        // builds HTML dynamically
        data);
        // Sends data to chart function for visualization
        // ensures graph updates when data changes
	}

	// ================= EXPORT JSON =================
	async function exportJson() {
// calling the endpoint
		const res = await fetch(`${API_URL}/export/json`);
		// DAO method loads all customer records from database
        // returns List<Customer> : SELECT * FROM customer
		const data = await res.json();
		// Converts HTTP response JSON into JavaScript object
        // data becomes array of customers

		// Blob = file object stored in browser memory
		const blob = new Blob(
			[JSON.stringify(data, null, 2)],
			// Converts JavaScript object back into JSON text
			// null = no custom replacer
            // 2 = pretty formatting with indentation (Vertiefung, отступ)
			{ type: "application/json" }
		);

		const url = URL.createObjectURL(blob);
		// Creates temporary local URL for blob/file
        // browser can use this URL for downloading

		const a = document.createElement("a");
		// Creates temporary HTML anchor element (<a>)
		a.href = url; // href = download source
		a.download = "customers.json"; // download = target filename
		// Forces file download instead of opening in browser
		a.click(); // Simulates user click automatically
                   // triggers browser download

		URL.revokeObjectURL(url); // Removes temporary URL from memory
                                  // prevents memory leaks
	}



	// ================= EXPORT CSV =================
	async function exportCsv() {

		const res = await fetch(`${API_URL}/export/csv`);
		const data = await res.text();

		const blob = new Blob([data], { type: "text/csv" });

		const url = URL.createObjectURL(blob);

		const a = document.createElement("a");
		a.href = url;
		a.download = "customers.csv";
		a.click();

		URL.revokeObjectURL(url);
	}



	// ================= EXPORT XML =================
	async function exportXml() {

		const res = await fetch(`${API_URL}/export/xml`);
		const data = await res.text();

		const blob = new Blob([data], { type: "application/xml" });

		const url = URL.createObjectURL(blob);

		const a = document.createElement("a");
		a.href = url;
		a.download = "customers.xml";
		a.click();

		URL.revokeObjectURL(url);
	}

//    * renderGenderChart() → converts raw data → statistics
//    * Chart.js → visualizes data
//    * destroy() → prevents memory and duplicates graphics
//    * window.onload → automatic loading data


let genderChartInstance = null;
// stores chart object so we can update it later (important to avoid duplicates)

// ================= CREATE GRAPH =================
function renderGenderChart(customers) {

	// Count customers by gender
	let counts = {
		M: 0,
		W: 0,
		D: 0,
		U: 0
	};

	// Loop through all customers and count genders
	customers.forEach(c => {
		if (counts[c.gender] !== undefined) {
			counts[c.gender]++;
		}
	});

	// If chart already exists → destroy it before creating new one
	if (genderChartInstance) {
		genderChartInstance.destroy();
	}

	// Create chart
	const ctx = document.getElementById("genderChart");

	// creating chart instance based on chart class - linked in html: <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
	// Chart.js exposes a global Chart class through the CDN script
	// CDN (Content Delivery Network) is a distributed network of servers that caches web content
	genderChartInstance = new Chart(ctx, {
		type: "pie", // pie chart = good for distribution

		// Configuration object
		data: {
			labels: ["Male (M)", "Female (W)", "Diverse (D)", "Unknown (U)"],
			datasets: [{
				label: "Customers by Gender",
				data: [counts.M, counts.W, counts.D, counts.U],
			// labels = legend text
            // datasets = numeric values for chart
				// colors for visual clarity
				backgroundColor: ["#36A2EB", "#FF6384", "#FFCE56", "#AAAAAA"]
			}]
		},

		options: {
			responsive: true,
			//maintainAspectRatio: false, // transl.: Seitenverhältnis beibehalten
			plugins: {
				legend: { // Legend = list of categories
					position: "bottom"
				}
			}
		}
//		new Chart(ctx, {
//            type: "pie",
//            data: {...},
//            options: {...}
//        }); = Create pie chart with this data and these settings

	});
}
