# PetPal API

### Starting the server

Run the following command: `npm start`

The server will start at: `http://localhost:3000`

### Supported Endpoints

**`/api`** - entry point to API

`Response`: `Welcome to PetPal API`

**`/api/register`** - enables users to register for app

`Request`: 

    {
        "first_name": "John",
        "last_name": "Doe",
        "email_address": "test@gmail.com",
        "password": "aosfhahnachagcasb",
        "user_type": "Owner"
    }

`Response`: 200 success code or 400 error code

**`/api/login`** - enables users to login

`Request`: 

    {
        "email_address": "test@gmail.com",
        "password": "aosfhahnachagcasb"
    }

`Response`: User profile for the user

