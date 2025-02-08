# PetPal API

### Starting the server

Run the following command: `npm start`

The server will start at: `http://localhost:3000`

### Supported Endpoints

**`/api`** - entry point to API

`Response`: `Welcome to PetPal API`

**`POST /api/register`** - enables users to register for app

`Request`: 

    {
        "first_name": "John",
        "last_name": "Doe",
        "email_address": "test@gmail.com",
        "password": "aosfhahnachagcasb",
        "user_type": "Owner"
    }

`Response`: 200 success code or 400 error code

**`GET /api/login`** - enables users to login

`Request`: 

    {
        "email_address": "test@gmail.com",
        "password": "aosfhahnachagcasb"
    }

`Response`: User profile for the user

**`GET /tasks/:userId`** - gets a list of tasks for a given user

`Request`: `/tasks/:akhsjfbkaaacjn`

`Response`: List of all tasks for the given user

**`POST /tasks/:userId`** - creates a new task for a given user

`Request`: 

    {
        "description": "this is a test",
        "status": "CLOSED"
    }

`Response`: Created task for the user

