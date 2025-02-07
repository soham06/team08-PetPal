import { getFirestore, collection, getDocs } from 'firebase/firestore';
import firebaseConnection from '../firebase.js'

export async function loginUser (req, res) {
    const usersTable = firebaseConnection.collection('users')
    const querySnapshot = await usersTable.get()

    let userData = {};
    querySnapshot.forEach((doc) => {
      userData = { id: doc.id, ...doc.data() };
    });

    console.log(userData)

    try {
        const reqBody = req.body;
        console.log(reqBody);
        res.status(200).json(reqBody);
    } catch (error) {
        res.status(404).json({ message: error.message});
    }
};