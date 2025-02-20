import firebaseConnection from '../firebase.js'
import { getFirestore, collection, getDoc,
         getDocs, query, where, addDoc, doc,
         serverTimestamp, updateDoc, deleteDoc } from 'firebase/firestore';

export async function getPetsForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { userId } = req.params;

        if (!userId) {
            return res.status(400).json({ message: "Invalid request params, please provide a userId"});
        }

        const userRef = doc(db, "users", userId);
        const userDoc = await getDoc(userRef);

        if (!userDoc.exists()) {
            return res.status(404).json({ message: "This user doesn't exist"});
        }

        const petsTable = collection(db, "pets")
        const q = query(petsTable, where("userId", "==", userId))
        const pets = await getDocs(q)
        const petsList = pets.docs.map(pet => ({
            petId: pet.id,
            ...pet.data()
        }));
        res.status(200).json(petsList);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function createPetForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { userId } = req.params;

        if (!userId) {
            return res.status(400).json({ message: "Invalid request params, please provide a userId"});
        }

        const userRef = doc(db, "users", userId);
        const userDoc = await getDoc(userRef);

        if (!userDoc.exists()) {
            return res.status(404).json({ message: "This user doesn't exist"});
        }

        const petData = req.body;

        if (!petData.name || !petData.birthday || !petData.animal || !petData.gender || !petData.breed) {
            return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        petData["createdAt"] = serverTimestamp();
        petData["userId"] = userId;
        const petsTable = collection(db, "pets")
        const newPet = await addDoc(petsTable, petData)

        const fetchedCreatedPet = await getDoc(newPet)
        const createdPet = {
            petId: fetchedCreatedPet.id,
            ...fetchedCreatedPet.data(),
        };
        res.status(200).json(createdPet);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function updatePetForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { petId } = req.params;

        if (!petId) {
            return res.status(400).json({ message: "Invalid request params, please provide a petId"});
        }

        const petRef = doc(db, "pets", petId);
        const petDoc = await getDoc(petRef);

        if (!petDoc.exists()) {
            return res.status(404).json({ message: "This pet doesn't exist"});
        }

        const petData = req.body;

        if (!petData.name || !petData.birthday || !petData.animal || !petData.gender || !petData.breed) {
            return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        await updateDoc(petRef, petData)

        const fetchedUpdatedPet = await getDoc(petRef)
        const fetchedPet = {
            petId: fetchedUpdatedPet.id,
            ...fetchedUpdatedPet.data(),
        };
        res.status(200).json(fetchedPet);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function deletePetForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { petId } = req.params;

        if (!petId) {
            return res.status(400).json({ message: "Invalid request params, please provide a petId"});
        }

        const petRef = doc(db, "pets", petId);
        const petDoc = await getDoc(petRef);

        if (!petDoc.exists()) {
            return res.status(404).json({ message: "This pet doesn't exist"});
        }

        await deleteDoc(petRef)
        res.status(200).json("Successfully deleted pet");
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};