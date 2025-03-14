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

**`GET /tasks/:userId`** - gets a list of OPEN tasks for a given user

`Request`: `/tasks/akhsjfbkaaacjn`

`Response`: List of all OPEN tasks for the given user

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

**`GET /events/:userId`** - gets a list of events for a given user

`Request`: `/events/akhsjfbkaaacjn`

`Response`: List of all events for the given user

**`POST /events/:userId`** - creates a new event for a given user

`Request`: 

    {
        "description": "test event",
        "startDate": "March 1, 2025",
        "endDate": "March 1, 2025",
        "startTime": "2:00PM",
        "endTime": "5:00PM",
        "location": "123 Test Ave"
    }

`Response`: Created event for the user

**`PATCH /events/:eventId`** - updates an existing event for a given user

`Request`: 

    {
        "description": "test patching event",
        "startDate": "March 1, 2025",
        "endDate": "March 1, 2025",
        "startTime": "2:00PM",
        "endTime": "5:00PM",
        "location": "123 Test Ave"
    }

`Response`: Updated event for the user

**`DELETE /events/:eventId`** - deletes an existing event for a given user

`Request`: No request body needed

`Response`: "Successfully deleted event" if the event is deleted

**`GET /pets/:userId`** - gets a list of pets for a given user

`Request`: `/pets/akhsjfbkaaacjn`

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

**`GET /pets/share/:userId`** - gets a list of shared pets for a given user

`Request`: `/pets/share/akhsjfbkaaacjn`

`Response`: List of all shared pets for the given user

**`PATCH /pets/share/:petId`** - shared an existing pet profile to another user

`Request`:

    {
        "emailAddress": "test@gmail.com"
    }

`Response`: Updated pet for the user

**`DELETE /pets/share/:petId`** - unshared an existing pet with a given user

`Request`:

    {
        "emailAddress": "test@gmail.com"
    }

`Response`: "Successfully unshared pet" if the pet is unshared with users

**`GET /postings/:userId`** - gets a list of pet postings for a given user

`Request`: `/postings/akhsjfbkaaacjn`

`Response`: List of pet postings for a given user

**`POST /postings/:userId`** - creates a new pet posting from a given user

`Request`:

    {
        "city": "Waterloo",
        "description": "this is a new post",
        "email": "test@gmail.com",
        "name": "joe",
        "phone": "123-456-7890"
    }

`Response`: Created pet posting for the user

**`PATCH /postings/:postId`** - updates an existing posting for a given user

`Request`:

    {
        "city": "Waterloo",
        "description": "this is a patch test",
        "email": "test@gmail.com",
        "name": "joe",
        "phone": "123-456-7890"
    }

`Response`: Updated posting for the user

**`DELETE /postings/:postId`** - deletes an existing post for a given user

`Request`: No request body needed

`Response`: "Successfully deleted post" if the task is deleted
