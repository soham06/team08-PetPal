# PetPal API

### Starting the server

Run the following command: `npm start`

The server will start at: `http://localhost:3000`

### Supported Endpoints

**`/api`** - entry point to API

`Response`: `Welcome to PetPal API`

**`POST /api/register`** - enables users to register for app

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
        "status": "OPEN"
    }

`Response`: Created task for the user

**`PATCH /tasks/:taskId`** - updates an existing task for a given user

`Request`: 

    {
        "description": "this is a patch test",
        "status": "CLOSED"
    }

`Response`: Updated task for the user

**`DELETE /tasks/:taskId`** - deletes an existing task for a given user

`Request`: No request body needed

`Response`: "Successfully deleted task" if the task is deleted

**`GET /pets/:userId`** - gets a list of pets for a given user

`Request`: `/pets/:akhsjfbkaaacjn`

`Response`: List of all pets for the given user

**`POST /pets/:userId`** - creates a new pet for a given user

`Request`:

    {
        "name": "joe",
        "birthday": "nov 10",
        "animal": "cat",
        "gender": "m",
        "breed": "grey"
    }

`Response`: Created pet for the user

**`PATCH /pets/:petId`** - updates an existing pet for a given user

`Request`:

    {
        "name": "new name",
        "birthday": "nov 10",
        "animal": "cat",
        "gender": "m",
        "breed": "white"
    }

`Response`: Updated pet for the user

**`DELETE /pets/:petId`** - deletes an existing pet for a given user

`Request`: No request body needed

`Response`: "Successfully deleted pet" if the pet is deleted

