import express from 'express'
import { registerUser } from '../controllers/register.js'
import { loginUser } from '../controllers/login.js'
import { getTasksForUser,createTaskForUser, updateTaskForUser, deleteTaskForUser } from '../controllers/tasks.js'
import { getEventsForUser, createEventForUser, updateEventForUser, deleteEventForUser } from '../controllers/events.js'
import { getPetsForUser, createPetForUser, updatePetForUser, deletePetForUser, sharePetWithUser, unsharePetWithUser, getSharedPetsForUser } from '../controllers/pets.js'
import { getPostingsForUser, createPostingForUser, updatePostForUser, deletePostForUser } from '../controllers/postings.js'

const router = express.Router()

router.get('/', (req, res) => {
    res.send('Welcome to PetPal API');
});

// user routes
router.post('/register', registerUser);
router.post('/login', loginUser);

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

// pet postings routes
router.get('/postings/:userId', getPostingsForUser)
router.post('/postings/:userId', createPostingForUser)
router.patch('/postings/:postId', updatePostForUser)
router.delete('/postings/:postId', deletePostForUser)

export default router;