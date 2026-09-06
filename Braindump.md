# Brain Dump

Write down what is making you worry, so you may feel relaxed while focusing on other things.

## Quick Dump

* The [Spring Boot docs](https://docs.spring.io/spring-boot/index.html) are a great resource for the back-end details
* The [Spring.io](https://spring.io/guides) website has very good practical guides for Spring Boot
* The [Geek for Geeks](https://www.geeksforgeeks.org/advance-java/spring-boot/) website has a lot of in-depth tutorials for Spring Boot
* The [Spring Initializr](https://start.spring.io/) seems to be the main resource for starting Spring Boot projects
* When accessing an endpoint, adding "`?[data1]=[value1]&[data2]=[value2]`" to the end of the URL allows passing of parameters. Keep in mind that the default URL seems to only send "GET" instructions. Other instructions can be tested and made with:
  * In **Edge**: right-click **>** Inspect **>** Network **;** reload page **;** right-click on top header **>** Edit and Resend
  * in **cmd** with the `curl` command, e.g.: `curl localhost:8080/hello -G -d name=%20iba`
    * Here `-d` needs to be specified before every parameter like so: `-d a=1 -d b=2`
    * Also: `-G` specifies a GET request; specifying data without `-G` specifies a POST request
* **REST**ful refers to software architecture which stands for “**Re**presentational **S**tate **T**ransfer”
  * **GET**: Used to *retrieve* data from the server. It should not have side effects on the server. (e.g., getting a webpage, fetching a list of products).
  * **POST**: Used to *send* data to the server, typically to create a new resource. (e.g., submitting a form, adding a new user).
  * **PUT**: Used to *update* an existing resource, or create it if it doesn't exist. The request body usually contains the complete, updated resource.
  * **DELETE**: Used to *remove* a specified resource from the server.
* [HTML URL Encoding Reference](https://www.w3schools.com/tags//ref_urlencode.asp)
* [Open Trivia DB](https://opentdb.com/api_config.php) API has a limit of 1 access per 5 seconds per IP, although up to 50 questions can be retrieved per call
  * Note that the [Token API](https://opentdb.com/api_token.php) does not have that limit
* [Rate Limiting](https://www.geeksforgeeks.org/advance-java/implementing-rate-limiting-in-a-spring-boot-application/) can be implemented through several strategies:
  * **Fixed Window:** Limits requests within a fixed time interval (e.g., 50 requests per minute).
  * **Sliding Window:** Provides smoother control by tracking requests over a moving time window.
  * **Token Bucket:** Allows bursts of requests based on available tokens that refill over time.
  * **Leaky Bucket:** Controls request flow by processing them at a steady rate and rejecting overflow.
* This [Stack Overflow](https://stackoverflow.com/questions/76331109/how-to-rate-limit-my-calls-to-an-external-api) page suggests I can use a Rate Limiter also for limiting my own application's access to an external API with its own Rate Limiter. This seems to be done through additional libraries.
* [Vue](https://vuejs.org) seems to be a good choice for modern JavaScript framework. It can also work with TypeScript.
  * It claims to be easy to learn
  * It combines `css`, `html`, and `js` into a single `.vue` file which also has less boilerplate overall
  * The [tutorial](https://vuejs.org/guide/introduction.html) recommends I am familiar with [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Language_overview), [HTML](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Structuring_content), and [CSS](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Styling_basics) first.