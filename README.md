[![Circle CI](https://circleci.com/gh/companieshouse/forms-enablement-api/tree/develop.svg?style=shield&circle-token=9ff96b9c65cc014e6bf6dff7e66fe8ad9aa8315e)](https://circleci.com/gh/companieshouse/forms-enablement-api/tree/develop)

Companies House Forms Enablement API Service
=====================

The tool accepts forms posted from Salesforce, converts them to an agreed format and posts them to Companies House Information Processing System (CHIPS). 

### About this application

This application is written using the [Dropwizard](http://www.dropwizard.io/) Java framework.

### Prerequisites

In order to run the tool locally you'll need the following installed on your machine:

- [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)

### Getting Started

Run the following from the command line to download the repository and change into the directory:

```
git clone git@github.com:companieshouse/forms-enablement-api.git

cd forms-enablement-api
```


### Running the development server

To run the server type the following command in your terminal

```bash
./run.sh
```

or on windows

```
mvn -DembeddedMongoProxyHost=wsproxy.internal.ch clean package
java -jar target/formsapiservice-1.0-SNAPSHOT.jar server configuration.yml
```
Then you can now visit [http://localhost:8080/](http://localhost:8080/) in your browser

Note on the maven build
------------------------
For the tests to run the system property *embeddedMongoProxyHost* needs to be set to *wsproxy.internal.ch* for the MongoDB 
related test to be able to down load MongoDB packages via HTTP. 

### Code style

We are using the [Google Java Style](https://google.github.io/styleguide/javaguide.html) enforced by the Maven
[checkstyle](https://maven.apache.org/plugins/maven-checkstyle-plugin/) plugin

Configuration
-------------


 Variable                 | Default                 |Description
 -------------------------|-------------------------|--------------
 SALESFORCE_API_KEY       |                         |API key to access SalesForce APIs
 SALESFORCE_AUTH_URL      |                         |URL for SalesForce authorisation
 SALESFORCE_AUTH_USERNAME |                         |SalesForce user name
 SALESFORCE_AUTH_PASSWORD |                         |SalesForce password
 SALESFORCE_AUTH_ID       |                         |SalesForce authentication id 
 SALESFORCE_AUTH_SECRET   |                         |SalesForce authentication secret
 SALESFORCE_AUTH_TYPE     |                         |
 SALESFORCE_CLIENT_URL    |                         |URL for SalesForce
 CHIPS_API_KEY            |                         |API key to access CHIPS
 CHIPS_API_URL            |                         |URL of CHIPS API
 CHIPS_BARCODE_SERVICE_URL|                         |URL of Barcode Service
 CHIPS_PRESENTER_AUTH_URL |                         |URL of Presenter Service
 
 Manual Tests
 ------------
 
 The following curl command will test the facility to retrieve a barcode via the service. Replace <authorisation> with
 the  word CHIPS and the value of CHIPS_API_KEY separated by a colon which has been Base64 encoded. Replace <serviceuri> withe 
 the ip 
 address
 and port the service is listening on.
 ```
 curl -X -H post "Authorization: Basic <authorisation>" http://<serviceuri>/barcode
 ```
 For example:
 
 ```
 curl -X -H post "Authorization: Basic Q0hJUFM6RFVNTVk=" http://172.19.70.120:8080/barcode
 ```
 
 

  