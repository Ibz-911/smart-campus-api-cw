# Smart Campus API


Overview
This project is a RESTful API built using JAX-RS for the Smart Campus system. The API manages Rooms, Sensors, and Sensor Readings using in-memory Java data structures such as maps and lists. It supports resource nesting, filtering using query parameters, and custom error handling.

How to Run
1. Open the project in NetBeans
2. Make sure Apache Tomcat is selected as the server
3. Clean and build the project
4. Run the project
5. Use Postman to test the endpoints

Base URL:
http://localhost:8080/smart-campus-api/api/v1/

Sample curl Commands

Get API discovery
curl -X GET http://localhost:8080/smart-campus-api/api/v1/

Create a room
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"ENG-101\",\"name\":\"Engineering Lab\",\"capacity\":35}"

Get all rooms
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms

Create a sensor
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":0.0,\"roomId\":\"ENG-101\"}"

Filter sensors by type
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=Temperature"

Add a sensor reading
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d "{\"value\":24.7}"

Get sensor readings
curl -X GET http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings

## Answers to Coursework Questions

Part 1.1 – JAX-RS Resource Lifecycle
By default, JAX-RS creates a new instance of a resource class for every incoming HTTP request. This is called per-request scope. Because of this, you cannot store shared data in instance variables — each request gets its own fresh object, so anything written in one request is invisible to the next. To safely share data across all requests you must use a static data structure held outside the resource class. In this project a static ConcurrentHashMap is used, which is thread-safe, meaning multiple simultaneous requests can read and write to it without corrupting data or causing race conditions.

Part 1.2 – HATEOAS
HATEOAS (Hypermedia as the Engine of Application State) means API responses include links pointing to related resources and available actions, rather than just raw data. For example, a response for a room might include a link to that room's sensors. This means client developers do not need to memorise or hard-code URLs — they can follow links discovered at runtime, similar to browsing a website. It also makes the API more resilient to change, since clients following links adapt automatically rather than breaking if a URL structure changes.

Part 2.1 – IDs vs Full Objects
Returning only IDs keeps responses small and saves bandwidth, but forces the client to make one additional request per ID to retrieve the actual data, increasing latency and complexity. Returning full objects increases payload size but allows the client to display everything from a single request. For small collections, returning full objects is the better choice. For very large collections, pagination with summary objects is more appropriate.

Part 2.2 – DELETE Idempotency
Yes, DELETE is idempotent in this implementation. Idempotency means sending the same request multiple times produces the same outcome as sending it once. If a client sends a DELETE request for a room that has already been deleted, the server finds no matching room and returns 204 No Content rather than 404. The end state is the same regardless of how many times the request is sent — the room does not exist — which is correct RESTful behaviour for DELETE.

Part 3.1 – @Consumes Mismatch
The @Consumes annotation tells JAX-RS that an endpoint only accepts requests with a Content-Type of application/json. If a client sends data as text/plain or application/xml, the JAX-RS runtime rejects the request before it reaches the resource method and automatically returns a 415 Unsupported Media Type response. The developer does not need to write any manual content-type checking — the framework handles the mismatch entirely at the routing layer.

Part 3.2 – Query Param vs Path Segment for Filtering
A path segment like /sensors/type/CO2 implies that CO2 is a distinct addressable resource in its own right. A query parameter like /sensors?type=CO2 communicates that you are retrieving the sensors collection and optionally narrowing it by a filter. Filtering is a view concern, not a resource identity concern. Query parameters are also optional by design, so one endpoint serves both the full list and filtered subsets. They also combine easily for multiple filters, whereas path segments do not compose cleanly.

Part 4.1 – Sub-Resource Locator Benefits
The sub-resource locator pattern lets a resource class delegate handling of a sub-path to a separate class. Instead of defining every nested endpoint inside one large class, the locator method returns a new instance of a dedicated class that handles everything under that path. This keeps each class focused on one responsibility and prevents any single file from becoming unmanageable. Each sub-resource class can also be tested in isolation, and the nesting structure is expressed clearly through the class hierarchy rather than a long list of path annotations in one file.

Part 5.2 – 422 vs 404
A 404 response means the URL requested does not exist on the server. A 422 Unprocessable Entity means the URL is valid and the request was understood, but the body contains a semantic error — in this case, a reference to a room ID that does not exist. The client did nothing wrong with its URL; the problem is inside the JSON payload. Using 422 communicates precisely what went wrong, whereas returning 404 would be misleading because the sensors endpoint itself does exist.

Part 5.4 – Stack Trace Security Risk
Exposing raw stack traces reveals the names of internal packages and classes, the versions of frameworks and libraries in use, and method names and line numbers. An attacker can use framework version information to look up known vulnerabilities, and the structural detail to identify exactly where certain operations happen in the code. In some cases data values may also be exposed through exception messages. The global exception mapper prevents all of this by catching every unexpected error and returning only a generic, safe message with no internal details.

Part 5.5 – Filters vs Manual Logging
Placing logging inside a JAX-RS filter means it is written once and applied automatically to every request and response, simply because the filter is registered as a provider. Adding logging manually inside each resource method instead would require repeating it across every method in every class, making it easy to miss endpoints and mixing logging concerns into business logic. Filters keep cross-cutting concerns cleanly separated from application logic and ensure consistent coverage without any extra effort per endpoint.
