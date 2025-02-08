import firebaseConnection from '../firebase.js'

export async function registerUser (req, res) {
    try {
        const userData = req.body;

        if (!userData.firstName || !userData.lastName 
            || !userData.lastName || !userData.password 
            || !userData.userType) {
                return res.status(401).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        const usersTable = firebaseConnection.collection('users')
        const querySnapshot = await usersTable.where("emailAddress", "==", userData.emailAddress).get()

        if (!querySnapshot.empty) {
            return res.status(409).json({ message: "An account with this email address already exists! Try to login instead"});
        }

        await usersTable.add(userData);

        const fetchedCreatedUser = (await usersTable.where("emailAddress", "==", userData.emailAddress).get())
        const createdUser = fetchedCreatedUser.docs.map(doc => ({
            userId: doc.id,
            ...doc.data()
        }));
        res.status(200).json(createdUser);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};