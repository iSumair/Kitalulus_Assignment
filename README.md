# Kitalulus_Assignment
A private repo for the assignment evaluation

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://github.com/iSumair/Kitalulus_Assignment)

## Features
This is a spring boot application that provides two end points:

- http://localhost:8080/authenticate [Used to authenticate user and return a JWT token]
- http://localhost:8080/fetchCountryDetails/{country} [Takes country name as a param and returns country information like name, population, currencies and currency conversion rate to IDR]

## Sample Requests and Responses

- Following are the sample request/responses. 
- For now, username and password are hardcoded for authentication.

```sh
URL:
http://localhost:8080/authenticate

Request:
{
    "username" : "foo",
    "password" : "foo"
}

Response:
{
    "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb28iLCJleHAiOjE2NDQxNzY0ODQsImlhdCI6MTY0NDE0MDQ4NH0.XrjInX17-AqMnxo1u6WN3q6ul2kfLqcHcX-rROIIwBs"
}
```

```sh
URL:
http://localhost:8080/fetchCountryDetails/pakistan

Response:
{
    "name": {
        "common": "Pakistan",
        "official": "Islamic Republic of Pakistan"
    },
    "currencies": {
        "PKR": {
            "name": "Pakistani rupee",
            "symbol": "₨",
            "toIDRRate": 82.5016848381866
        }
    },
    "population": 220892331
}
```
