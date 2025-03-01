import firebaseConnection from '../firebase.js'
import { getFirestore, collection, getDoc, 
         getDocs, query, where, addDoc, doc,
         serverTimestamp, updateDoc, deleteDoc, orderBy, limit } from 'firebase/firestore';

export async function getEventsForUser (req, res) {
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

        const eventsTable = collection(db, "events")
        
        const date = new Date();
        const options = { year: "numeric", month: "long", day: "numeric", timeZone: "America/New_York" };
        const curentDate = new Intl.DateTimeFormat("en-US", options).format(date);
        console.log(curentDate);

        const q = query(eventsTable, 
            where("userId", "==", userId),
            where("startDate", ">=", curentDate), 
            orderBy("startDate"), 
            orderBy("startTime"), 
            limit(6))
        const events = await getDocs(q)
        const eventsList = events.docs.map(event => ({
            eventId: event.id,
            ...event.data()
        }));
        res.status(200).json(eventsList);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function createEventForUser (req, res) {
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

        const eventData = req.body;

        if (!eventData.description || !eventData.startDate || !eventData.endDate || !eventData.startTime || !eventData.endTime || !eventData.location) {
            return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        eventData["createdAt"] = serverTimestamp();
        eventData["userId"] = userId;
        const eventsTable = collection(db, "events")
        const newEvent = await addDoc(eventsTable, eventData)

        const fetchedCreatedEvent = await getDoc(newEvent)
        const createdEvent = {
            eventId: fetchedCreatedEvent.id,
            ...fetchedCreatedEvent.data(),
        };
        res.status(200).json(createdEvent);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function updateEventForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { eventId } = req.params;

        if (!eventId) {
            return res.status(400).json({ message: "Invalid request params, please provide a eventId"});
        }

        const eventRef = doc(db, "events", eventId);
        const eventDoc = await getDoc(eventRef);

        if (!eventDoc.exists()) {
            return res.status(404).json({ message: "This event doesn't exist"});
        }

        const eventData = req.body;

        if (!eventData.description || !eventData.startDate || !eventData.endDate || !eventData.startTime || !eventData.endTime || !eventData.location) {
            return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        await updateDoc(eventRef, eventData)

        const fetchedUpdatedEvent = await getDoc(eventRef)
        const fetchedEvent = {
            eventId: fetchedUpdatedEvent.id,
            ...fetchedUpdatedEvent.data(),
        };
        res.status(200).json(fetchedEvent);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function deleteEventForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { eventId } = req.params;

        if (!eventId) {
            return res.status(400).json({ message: "Invalid request params, please provide a eventId"});
        }

        const eventRef = doc(db, "events", eventId);
        const eventDoc = await getDoc(eventRef);

        if (!eventDoc.exists()) {
            return res.status(404).json({ message: "This event doesn't exist"});
        }

        await deleteDoc(eventRef)
        res.status(200).json("Successfully deleted event");
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};