import express from 'express'
import { registerUser } from '../controllers/register.js'
import { loginUser } from '../controllers/login.js'

const router = express.Router()

router.get('/', (req, res) => {
    res.send('Welcome to PetPal API');
});

router.post('/register', registerUser);
router.get('/login', loginUser);

export default router;