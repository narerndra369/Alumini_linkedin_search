1. Clone the Proect
2. import to IDE
3. Build Maven dependencies
4. Goto Application.properties file Change the values
5. run the application

   Api Endpoints:

   Of course. Here is a well-structured way to present your API endpoints in a GitHub README file using Markdown. This format is clean, easy to read, and standard for API documentation.

You can copy and paste the following text directly into your README.md file.

API Documentation
This document provides details on the available API endpoints for this project.

1. Search for Alumni Profiles
This endpoint allows you to search for alumni profiles based on university, designation, and passout year.

Method: POST

Endpoint: /api/alumni/search

Request Body
The request body should contain the search criteria. The passoutYear is optional.

JSON

{
  "university": "mohan babu university, tirupati",
  "designation": "Software Development",
  "passoutYear": ""
}
Success Response (200 OK)
Returns a list of alumni profiles matching the search query.

JSON

{
	"data": [
		{
			"name": "Bharath kumar Perugu",
			"currentRole": null,
			"university": "mohan babu university, tirupati",
			"location": "Tirupati Urban, Andhra Pradesh, India",
			"linkedinHeadline": "Data Analyst | Power BI Expert | Secretary of ISTE - Student Chapter | Student of Mohan Babu University, Tirupati",
			"passoutYear": null
		},
		{
			"name": "Raja N",
			"currentRole": null,
			"university": "mohan babu university, tirupati",
			"location": "Tirupati Urban, Andhra Pradesh, India",
			"linkedinHeadline": "Mohan Babu University, tirupati",
			"passoutYear": null
		}
		// ... more profiles
	],
	"status": "success"
}
2. Get Employee Records
This endpoint retrieves employee records with options for sorting and grouping.

Method: GET

Endpoint: /api/dataset/employees/query

Query Parameters
Parameter	Type	Description	Example
sortBy	String	The field to sort the records by (e.g., age).	sortBy=age
order	String	The sort order. Can be asc or desc.	order=asc
groupBy	String	The field to group the records by (e.g., department).	groupBy=department

Export to Sheets
Example 1: Sorting Records
Request URL: http://localhost:8080/api/dataset/employees/query?sortBy=age&order=asc

Success Response (200 OK)

JSON

{
    "sortedRecords": [
        {
            "id": 3,
            "name": "RAmu",
            "age": 15,
            "department": "HR"
        },
        {
            "id": 1,
            "name": "John Doe",
            "age": 30,
            "department": "Engineering"
        },
        {
            "id": 2,
            "name": "Narendra",
            "age": 48,
            "department": "Engineering"
        }
    ]
}
Example 2: Grouping Records
Request URL: http://localhost:8080/api/dataset/employees/query?groupBy=department

Success Response (200 OK)

JSON

{
    "groupedRecords": {
        "Engineering": [
            {
                "id": 1,
                "name": "John Doe",
                "age": 30,
                "department": "Engineering"
            },
            {
                "id": 2,
                "name": "Narendra",
                "age": 48,
                "department": "Engineering"
            }
        ],
        "HR": [
            {
                "id": 3,
                "name": "RAmu",
                "age": 15,
                "department": "HR"
            }
        ]
    }
}
