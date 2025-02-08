# PetPal API

### Starting the server

Run the following command: `npm start`

The server will start at: `http://localhost:3000`

### Supported Endpoints

**`/api`** - entry point to API

`Response`: `Welcome to PetPal API`

**`/api/register`** - enables users to register for app

**Note:** firstName, lastName, emailAddress, password, userType are required in the request body for this endpoint to work properly

`Request`: 

    {
        "firstName": "John",
        "lastName": "Doe",
        "emailAddress": "test@gmail.com",
        "password": "aosfhahnachagcasb",
        "userType": "Owner"
    }

`Response`: 200 success code or 400 error code

**`/api/login`** - enables users to login

`Request`: 

    {
        "email_address": "test@gmail.com",
        "password": "aosfhahnachagcasb"
    }

`Response`: User profile for the user

