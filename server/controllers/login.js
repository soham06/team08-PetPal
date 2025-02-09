import { getFirestore, collection, getDocs, query, where } from 'firebase/firestore';
import firebaseConnection from '../firebase.js'

export async function loginUser (req, res) {
    const db = getFirestore(firebaseConnection);

    const credentials = req.body;

    const usersRef = collection(db, "users");
    const q = query(usersRef, where("emailAddress", "==", credentials.emailAddress));
    const users = await getDocs(q);

    if (users.empty) {
        return res.status(404).json({ message: "No account found with this email."});
    }

    const userDoc = users.docs[0];
    const userData = userDoc.data();

    if (credentials.password != userData.password) {
        return res.status(400).json({ message: "Incorrect password."});
    }

    try {
        res.status(200).json(userData);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};