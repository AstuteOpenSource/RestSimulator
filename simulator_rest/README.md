# Data Driven Rest Service Simulator

Data driven Rest service simulator that will work as stand alone service or for unit testing.
The idea of 'data driven' is that one should never have to change code to satisfy a specific use case.

## Primary Goals

* Purely data driven response.
* Support for stand alone service.
* Support for unit testing.

## TO DO

Implementation to support data sources in addition to path/file - ex excel, csv,db ...
Update jetty.xml for performance


## Details, Details, Details ...

### Startup
Service startup works the same way either stand alone or embedded.

* Define Port to use for for service from system property REST\_SERVICE\_PORT. Defaults to 9090.
* Define host ip to use for for service from system property REST\_SERVICE\_HOST. Defaults to 0.0.0.0.
* Define data path root from system property REST\_DATA\_ROOT. Defaults to ./data.
* Load [Global properties](#propertyFile) (optional) called config.properties located in REST\_DATA\_ROOT path.
* Start service. Currently uses JETTY embedded.

Note: Some of the tasks make be deferred to being done before the first request is processed.

### Request to Response

* construct data path by extracting path from request url.
* load config.properties, if exists, in the data path. These are merged with and override the global properties loaded at startup.   
* construct name of [response file](#responseFile) by using the following , separated by '\_' in specified order.
  * The request method (GET,POST,DELETE,...)
  * values of request headers specified in request.headers. 
  * values of request headers specified in request.headers.additional
  * values of request parameters specified in request.parameters
  * values of request parameters specified in request.parameters.additional
  * a '.'
  * if exists then append value of header Content-Type. If not specified then set as 'text/plain'.
  * if exists then append value of header Accept.
* replace unacceptable characters in name of response file with a '-'. Acceptable characters are \[A-Za-z0-9\_\.-\]
* load response file from data path. values of previously loaded config.properties will be overridden.
* set response status from response.status value
* set response headers from response.header.X values 
* if response.data.value exists and is not empty, then send it as response.
* if response.data.value does not exist and  response.data.file and is not empty, then send contents of file as response. 


Any errors /failure will result in a 501 error.

## Appendix

<a id="propertyFile"/>
### Property File  

Property files is a java property file. The ordering of values is significant. All keys are optional.

* request.headers - comma separated list of request headers to use in constructing the file name.
* request.parameters - comma separated list of request query parameters to use in constructing the file name.
* request.header.additional - additional comma separated list of request headers to use in constructing the file name. 
* request.parameters.additional - additional comma separated list of request query parameters to use in constructing the file name. 
* response.header.X - Header X to set in the response. Empty values will result in header not being set.



<a id="responseFile"/>
### Response File 

Response file is a java property file. The following keys are supported. Values here will overwrite previously loaded property files.

* response.header.X - Optional header X to set in the response. Empty values will result in header not being set.
* response.status -  MANDATORY - value to send. 
* response.data.value  -  Optional - Data to send. 
* response.data.resource - Optional resource - send contents of resource specified. If response.data.value is also specified then this is ignored. Location of file default to directory of response file unless file name starts with '/' and then it is relative to DATA\_ROOT\_DIR 

