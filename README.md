# Money Transfer Api

## Requirements
* Java 8
* Maven


## Build and run
Once cloned from git the application can be built with maven

    mvn clean install

To run the api execute:

    mvn exec:java
    
The api will start on `localhost` and will be listening to port `8080`.

## API documentation
The following REST endpoints are exposed by the API

| Http method | Endpoint                                        | Request                                               | Description                                                    |
|-------------|-------------------------------------------------|-------------------------------------------------------|----------------------------------------------------------------|
| GET         | /initialData                                    |                                                       | Create initial data for tests purposes                         |
| POST        | /bankAccount                                    | { "owner": "owner name", "balance": 5000.00 }         | Create a new bank account                                      |
| GET         | /bankAccount                                    |                                                       | Gets a list of all bank accounts                               |
| GET         | /bankAccount/{id}                               |                                                       | Get bank account with id = {id}                                |
| PUT         | /bankAccount                                    | { "id": 1, "owner" : "owner name" }                   | Update bank account's owner name                               |
| DELETE      | /bankAccount/{id}                               |                                                       | Delete bank account with id = {id}                             |
| POST        | /transaction                                    | { "senderId": 3, "receiverId": 1, "amount": 1500.80 } | Create a new transaction between bank accounts                 |
| GET         | /transaction                                    |                                                       | Gets a list of all transactions                                |
| GET         | /transaction/{id}                               |                                                       | Get transaction with id = {id}                                 |

## Details
All endpoint return a custom object called ApiResponse as follows:

    code: HTTP code to return
    accounts: a list of bank accounts to be returned if needed
    transactions: a list of transactions to be returned if needed
    message: a message to be returned if needed (often used for error messages)
    
Bank Account object is as follows:

    id: account identifier
    owner: account's owner name
    balance: account's balance
    
And Transaction object is as follows:

    id: transaction identifier
    senderId: identifier of sender's account
    receiverId: identifier of receiver's account
    amount: amount to be transfered
    date: date of the transaction
    
## Suggestions of future implementations
* Paginate the results for when they get too large
* Put more logs and comments in the code
* Implement some kind of currency exchange, maybe using an external API for the exchange rates like https://exchangeratesapi.io/
* Implement auth
