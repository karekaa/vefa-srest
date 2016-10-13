# Overview of REST functionality

Below are brief documentation of all REST resources and their supported actions.

## General information

- All REST functions needs basic authentication
- Make sure your HTTP client library always send Authorization header (force preemptive auth)
- Make sure your HTTPS client supports new certificates (high security, no-SSL, TLS v1.2+ etc).

## Method overview

```
verb    uri       produces                 description
        [optional 2nd line with information about input parameters that it consumes]
```

Start of a resource providing statistics over the inbox, outbox and messages
```
GET   /statistics   APPLICATION_XML         Retrieves statistics overview of the number of inbox, outbox and messages
```

Represents the "inbox" resource, which allows clients to GET MesssageMetaData messages
```
GET   /inbox                                APPLICATION_XML      Retrieves the unread messages from the inbox
GET   /inbox/{message_no}/                  APPLICATION_XML      Retreives the message header for the supplied message number
GET   /inbox/{message_no}/xml-document      APPLICATION_XML      Retrieves the PEPPOL XML Document in XML format (without the header stuff)
POST  /inbox/{message_no}/read              APPLICATION_XML      Marks a specific message as read
GET   /inbox/count                          TEXT_PLAIN           Returns number of messages in inbox
```

Represents the "outbox" resource, which allows clients to POST outboundMesssageMetaData messages destined for a recipient in the
```
GET  /outbox                               APPLICATION_XML         Retrieves the queued messages in the outbox
GET  /outbox /{message_no}/                APPLICATION_XML         Retreives the message header for the supplied message number.
GET  /outbox /{message_no}/xml-document    APPLICATION_XML         Retrieves the PEPPOL XML Document in XML format, without the header stuff
POST /outbox                               APPLICATION_XML         Sends a new message to specified receiver
      MULTIPART_FORM_DATA
```

Represents the "messages" resource, which allows clients to GET MessageMetaData from both inbox and outbox
```
GET   /messages                                         APPLICATION_XML        Retrieves messages from /inbox and /outbox
GET   /messages/{message_no}/                           APPLICATION_XML        Retreives the message header for the supplied message number
GET   /messages/{message_no}/xml-document               APPLICATION_XML        Retrieves the PEPPOL XML Document in XML format, without the header stuff.
GET   /messages/{message_no}/xml-document-decorated     APPLICATION_XML        Retrieves the PEPPOL XML Document in XML format, with added stylesheet (intended for web viewing on our site).
GET   /messages/count                                   TEXT_PLAIN             Returns number of messages in inbox
GET   /messages/{message_no}/rem                        APPLICATION_XML        Returns REM evidence, if available (TODO)
```

Represents the "directory" resource, which allows clients to check whether participant is registered in peppol network.
```
GET   /directory/{participantId}/{localName}  APPLICATION_XML   Retrieves acceptable document types for given participant and localName
GET   /directory/{participantId}/             HTTP CODE         Checks whether participant is registered in SMP
      Returns HTTP_OK if exists in some SMP - HTTP_NC (no content) if not registered
```

Exposing resources allowing to send notification emails when something goes wrong in ringo client
```
POST  /notify/batchUploadError      APPLICATION_XML
      @FormParam("commandLine") String commandLine, @FormParam("errorMessage") APPLICATION_FORM_URLENCODED
POST  /notify/downloadError         APPLICATION_XML
      @FormParam("commandLine") String commandLine, @FormParam("errorMessage") APPLICATION_FORM_URLENCODED
```

Represents the "admin" resource, which allows to look up various statuses
```
GET   /admin/status             APPLICATION_XML    Retrieves messages without account_id (messages we do not know who belongs to)
GET   /admin/statistics         APPLICATION_XML    Retrieve Statistics
GET   /admin/sendMonthlyReport  TEXT_PLAIN         Send monthly report (for previous month)
      @QueryParam("year") Integer year, @QueryParam("month") Integer month, @QueryParam("email") String email
```

Register a new user (customer account) in the system.
```
POST  /register     APPLICATION_JSON        Returns RegistrationData in JSON format as TEXT_PLAIN
```

### Registration

This is an example showing how to register a new account. I.e entries will be created in the database tables `customer` and `account`:

 1. Create a file in JSON syntax holding the data to be registered:
    ``` 
    {
      "name" : "Difi",
      "address1" : "Business Address 1",
      "address2" : "Business Address 2",
      "zip" : "0494",
      "city" : "Oslo",
      "country" : "Norway",
      "contactPerson" : "The Boss",
      "email" : "boss@business.com",
      "phone" : "111222333",
      "username" : "newbusiness",
      "password" : "topsecret123",
      "orgNo" : "991825827",
      "registerSmp" : false
    }
    ```
    Note! The organisation number must be valid and previously unknown in the system
 
 1. Execute the registration by performing an http POST using any tool you like. Here is how you would do it using `curl`, given the data being present in 
    a file named `/tmp/register.json`:
    ```
    curl -i -H "Content-Type: text/plain" -H "Accept: application/json" -X POST \ 
        --data @/tmp/register.json http://localhost:8080/vefa-srest/register/
    ```
 
 1. Upon successful completion you should see something like this:
    ```
    HTTP/1.1 200 
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Wed, 12 Oct 2016 15:16:06 GMT
    ```