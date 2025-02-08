import express from 'express'
import { registerUser } from '../controllers/register.js'
import { loginUser } from '../controllers/login.js'
import { getTasksForUser, createTaskForUser } from '../controllers/tasks.js'

const router = express.Router()

router.get('/', (req, res) => {
    res.send('Welcome to PetPal API');
});

// user routes
router.get('/register', registerUser);
router.get('/login', loginUser);

// task routes
router.get('/tasks/:userId', getTasksForUser);
router.post('/tasks/:userId', createTaskForUser);
// TODO: implement routes to update and delete tasks

export default router;