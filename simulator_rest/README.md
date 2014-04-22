# Data Driven Rest Service Simulator

Data driven Rest service simulator that will work as stand alone service or for unit testing.
The idea of 'data driven' is that one should never have to change code to satisfy a specific use case.

## Primary Goals

* Purely data driven response.
* Support for stand alone service.
* Support for unit testing.

## TO DO

* Implementation to support data sources in addition to path/file - ex excel, csv,db ...
* Update jetty.xml for performance
* Move this document to javadoc of relevant classes/methods and generate this document from javadoc/template merge - single point of maintenance.
* property to return request headers - maybe with a regex?


## Details, Details, Details ...

### Startup
Service startup works the same way either stand alone or embedded.

* Define Port to use for for service from system property REST\_SERVICE\_PORT. Defaults to 9090.
* Define host ip to use for for service from system property REST\_SERVICE\_HOST. Defaults to localhost.
* Define data path root from system property REST\_DATA\_ROOT. Defaults to ./data.
* Load [Global properties](#propertyFile) (optional) called config.properties located in REST\_DATA\_ROOT path.
* Start service. Currently uses JETTY embedded.

Note: Some of the tasks make be deferred to being done before the first request is processed.

### Request to Response

* construct resource path by extracting path from request url.
* load [Local properties](#propertyFile) called config.properties, if exists, in the data path. These are merged with and override the global properties loaded at startup.   
* construct name of [response resource](#responseFile) by using the following , separated by '\_' in specified order.
  * The request method (GET,POST,DELETE,...)
  * values extracted from resource path (request URI) and specified in request.uri.variables.
  * values of request headers specified in request.headers. 
  * values of request headers specified in request.headers.additional
  * values of request parameters specified in request.parameters
  * values of request parameters specified in request.parameters.additional
* replace unacceptable characters in name of response file with a '-'. Acceptable characters are \[A-Za-z0-9\_\.-\]
* load response file from data path. values of previously loaded config.properties will be overridden.
* set response status from response.status value
* set response headers from response.return.header.X values 
* set response headers from response.header.X values 
* if response.data.value exists and is not empty, then send it as response.
* if response.data.value does not exist and response.data.file and is not empty, then send contents of file as response. 


Any errors /failure will result in a 501 error.




## Implementation Notes

Various options are available to manage structure of your test data and and to manage the length of the resource names.

### Request URI path translation

Lets say you have a resource path that is /users/1/avatar and /users/2/avatar

The response data can be organized in two separate ways

* The resources for each user can be kept in individual directories as specified by the resource path (GET.json)
* By specifying uri patterns (resource.uri.patterns=sample and resource.uri.pattern.sample=/users/{}/avatar) you could end up with two resource files(GET\_1.json and GET\_2.json) in same directory (/users/avatar)  
  
 

### Value Hashing

In the request.headers and request.parameters properties, if the header/parameter name is preceded with a ':' then the value of the header/parameter is hashed using SHA-1 and then used.
For example is you specify :X-Auth-Claims, then the value of header is hashed to a hex encode SHA-1 value.

### Value Replacement

Some time you may want to shorten the values. For example Accept header of value application/json will translate to application_json. 
You could specify propery of request.header.Accept.application/json=json and the value will translate to json.   


## Appendix

<a id="propertyFile"/>
### Property File  

Property files is a java property file. The ordering of values is significant. All keys are optional.

* request.uri.patterns = comma separated list of patterns. Order specified is significant. Applicable to global property file only.
* request.uri.pattern.X = X is (for each) pattern specified above. The value {} is used to extract variables. ex: /users/{}
* request.uri.variables = * or comma separated list of index (starting at 0) of variables extracted from uri path.
* request.headers - comma separated list of request headers to use in constructing the file name. if header is preceded with a ':' then value is hashed using SHA-1.
* request.parameters - comma separated list of request query parameters to use in constructing the file name. if parameter is preceded with a ':' then value is hashed using SHA-1.
* request.header.additional - additional comma separated list of request headers to use in constructing the file name. Same rules as request.headers.
* request.parameters.additional - additional comma separated list of request query parameters to use in constructing the file name.  Same rules as request.parameters.
* request.replace.header.H.V - Where H is header name and V is value. Value of this property will be used as replacement. 
* request.replace.parameter.P.V - Where P is parameter name and V is value. Value of this property will be used as replacement. 
* response.header.X - Header X to set in the response. Empty values will result in header not being set.


<a id="responseFile"/>
### Response File 

Response file is a java property file. The following keys are supported. Values here will overwrite previously loaded property files. 
In addition to [properties](#propertyFile) specified in the property file, you can specify the following properties. 

* response.status -  MANDATORY - value to send. 
* response.data.value  -  Optional - Data to send. 
* response.data.resource - Optional resource - send contents of resource specified. If response.data.value is also specified then this is ignored. Location of file default to directory of response file unless file name starts with '/' and then it is relative to DATA\_ROOT\_DIR 

### Contact

Lloyd Fernandes 
OpenSource@astute.biz
