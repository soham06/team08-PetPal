import { getFirestore, collection, getDocs } from 'firebase/firestore';
import firebaseConnection from '../firebase.js'

export async function registerUser (req, res) {
    try {
        const reqBody = req.body;
        console.log(reqBody);
        res.status(200).json(reqBody);
    } catch (error) {
        res.status(404).json({ message: error.message});
    }
};