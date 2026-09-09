# Planning

**"Make the back-end API work with the external API"**

1. Decide exact specification for the back-end API:

   * What do the end-points `/questions` and `/checkanswers` really do?
     * What parameters?
     * How many questions are returned?
     * Is the category specified?
     * How are external API tokens handled?
   * What kind of internal state does the back-end keep track of?
   * Can we ensure that GET and POST behave as expected?
     * GET should not have any side effects
     * POST can have side effects

2. Create end point to contact external API

   * How will the front-end interact with the back-end?

     * What function does the front-end use to access the back-end?

     * How is the JSON return from the back-end read by the front-end?

   * What data to send to the external API and in what format?
   * What format can we expect from data retrieved from the external API?
   * How to decode the received data?
     * Do we even need to decode the data?
     * What data structures are needed to perform the back-end API spec?

3. Implement rate limit for external API access

   * How should the rate limit be implemented?
     * Does the back-end API wait to respond? Is this ever done in industry?
     * Does the front-end repeatedly send requests to the back-end API if it needs to wait?
     * Can we make the front-end wait a certain amount based on back-end API output?
       * like: "GET /questions/?amount=10" returns '{"success":"no", "wait":"15s"}'

4. Optimise external API requests (for example, create a pool of questions to answer requests with)

## Overall goals:

* [x] Create a Spring Boot project
* [x] Create a Vue.js project
* [x] Connect front-end to back-end
* [x] Fix build setup and test on other computers

* [ ] Make the back-end API work with the external API
* [ ] Create and implement API end points
* [ ] Integrate base API functionality with the front-end
* [ ] Make the front-end look good
* [ ] Document the entire stack
* [ ] Write tests for the entire stack (prefer back-end)
* [ ] Ensure test environment works
* [ ] Host the result
* [ ] Setup Continuous Integration / Continuous Delivery with GitHub Actions

