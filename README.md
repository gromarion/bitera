# Bitera example app

Sends 0.01 Ethereum to a given address, specified by the user.

### Installation

> mvn clean install

### Running from IntelliJ

Application.java has the main() function to run the project and start listening for requests at http://localhost:8080

### Endpoints

**URL**: /transaction/v1/send

**Method**: POST

**Body**:

```javascript
{
  "address": "0xAD87c0E80Ab5E13F15757d5139cc6c6fcb823Be3"
}
```

**Response**:

```javascript
{
    "from": "0xE09BF1d2c3E4aBC3B906a16aaD203597Bf472F24",
    "to": "0xAD87c0E80Ab5E13F15757d5139cc6c6fcb823Be3",
    "transactionId": "0x4a196a42d1b568ea03d3f45b92bfa482f5fb196b44f809b7fbd634bf84eb003b",
    "value": 0.01,
    "etherScanAddress": "https://ropsten.etherscan.io/address/0xAD87c0E80Ab5E13F15757d5139cc6c6fcb823Be3"
}
```

