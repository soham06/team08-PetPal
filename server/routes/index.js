import express from 'express'
import { registerUser } from '../controllers/register.js'
import { loginUser, getUserDetailsById } from '../controllers/login.js'
import { getTasksForUser,createTaskForUser, updateTaskForUser, deleteTaskForUser } from '../controllers/tasks.js'
import { getEventsForUser, createEventForUser, updateEventForUser, deleteEventForUser } from '../controllers/events.js'
import { getPetsForUser, createPetForUser, updatePetForUser, deletePetForUser, sharePetWithUser, unsharePetWithUser, getSharedPetsForUser } from '../controllers/pets.js'

const router = express.Router()

router.get('/', (req, res) => {
    res.send('Welcome to PetPal API');
});

// user routes
router.post('/register', registerUser);
router.post('/login', loginUser);
router.get('/users/:userId', getUserDetailsById);

// task routes
router.get('/tasks/:userId', getTasksForUser);
router.post('/tasks/:userId', createTaskForUser);
router.patch('/tasks/:taskId', updateTaskForUser);
router.delete('/tasks/:taskId', deleteTaskForUser);

// events routes
router.get('/events/:userId', getEventsForUser);
router.post('/events/:userId', createEventForUser);
router.patch('/events/:eventId', updateEventForUser);
router.delete('/events/:eventId', deleteEventForUser);

//pet profile routes
router.get('/pets/:userId', getPetsForUser);
router.post('/pets/:userId', createPetForUser)
router.patch('/pets/:petId', updatePetForUser)
router.delete('/pets/:petId', deletePetForUser)

// share pet profiles routes
router.get('/pets/share/:userId', getSharedPetsForUser)
router.patch('/pets/share/:petId', sharePetWithUser)
router.delete('/pets/share/:petId', unsharePetWithUser)

export default router;