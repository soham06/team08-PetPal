import express from 'express'
import { registerUser } from '../controllers/register.js'
import { loginUser } from '../controllers/login.js'
import { getTasksForUser,createTaskForUser, updateTaskForUser, deleteTaskForUser } from '../controllers/tasks.js'
import { getPetsForUser, createPetForUser, updatePetForUser, deletePetForUser } from '../controllers/pets.js'

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

//pet profile routes
router.get('/pets/:userId', getPetsForUser);
router.post('/pets/:userId', createPetForUser)
router.patch('/pets/:petId', updatePetForUser)
router.delete('/pets/:petId', deletePetForUser)

export default router;