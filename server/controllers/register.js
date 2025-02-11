import firebaseConnection from '../firebase.js'
import { getFirestore, collection, getDocs, query, where, addDoc } from 'firebase/firestore';

export async function registerUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);

        const userData = req.body;

        if (!userData.firstName || !userData.lastName 
            || !userData.emailAddress || !userData.password
            || !userData.userType || !userData.address) {
                return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        const usersCol = collection(db, 'users');
        const q = query(usersCol, where("emailAddress", "==", userData.emailAddress));
        const querySnapshot = await getDocs(q);

        if (!querySnapshot.empty) {
            return res.status(409).json({ message: "An account with this email address already exists! Try to login instead"});
        }

        await addDoc(usersCol, userData);

        const fetchedCreatedUser = await getDocs(q);
        const createdUser = fetchedCreatedUser.docs.map(doc => ({
            userId: doc.id,
            ...doc.data()
        }))[0];
        res.status(200).json(createdUser);
    } catch (error) {
        console.log(error.message)
        res.status(400).json({ message: error.message});
    }
};