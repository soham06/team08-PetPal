import { getFirestore, collection, getDocs, query, where } from 'firebase/firestore';
import firebaseConnection from '../firebase.js'

export async function loginUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);

        const credentials = req.body;

        const usersRef = collection(db, "users");
        const q = query(usersRef, where("emailAddress", "==", credentials.emailAddress));
        const users = await getDocs(q);
        const userData = users.docs.map(doc => ({
            userId: doc.id,
            ...doc.data()
        }))[0];

        if (userData.empty) {
            return res.status(404).json({ message: "No account found with this email."});
        }

        if (credentials.password != userData.password) {
            return res.status(400).json({ message: "Incorrect password."});
        }

        res.status(200).json(userData);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};