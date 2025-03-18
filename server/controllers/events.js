import firebaseConnection from '../firebase.js'
import { DateTime } from 'luxon'
import { getFirestore, collection, getDoc, 
         getDocs, query, where, addDoc, doc,
         serverTimestamp, updateDoc, deleteDoc, orderBy, limit } from 'firebase/firestore';
import { getMessaging } from 'firebase-admin/messaging'


function convertStringToESTDate(date, time) {
    const [timePart, meridian] = time.split(" ");
    let [year, month, day] = date.split("-").map(Number);
    let [hours, minutes] = timePart.split(":").map(Number);

    if (meridian === "PM" && hours !== 12) {
        hours += 12;
    }
    else if (meridian === "AM" && hours === 12) {
        hours = 0
    }
    const dateObj = new Date(Date.UTC(year, month - 1, day, hours, minutes)).toISOString();
    return dateObj;
}

function convertTo12HourFormat(time24) {
    let [hours, minutes] = time24.split(":").map(Number);
    const ampm = hours >= 12 ? "PM" : "AM";
    hours = hours % 12 || 12;
    return `${hours}:${String(minutes).padStart(2, "0")} ${ampm}`;
}

function convertToEST(date) {
    const estDateTime = DateTime.fromJSDate(date, { zone: "utc" }).setZone("America/New_York");
    const formattedDate = estDateTime.toFormat("yyyy-MM-dd")
    const formattedTime = convertTo12HourFormat(estDateTime.toFormat("HH:mm"))
    const currentDate = convertStringToESTDate(formattedDate, formattedTime)
    return currentDate
}

function convertISOToDate(isoString) {
    const dateObj = new Date(isoString);
    const estDate = new Intl.DateTimeFormat("en-US", {
        timeZone: "UTC",
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
    }).formatToParts(dateObj);
    const month = estDate.find(part => part.type === "month").value;
    const day = estDate.find(part => part.type === "day").value;
    const year = estDate.find(part => part.type === "year").value;
    return `${month}-${day}-${year}`;
}

function convertISOToTime(isoString) {
    const dateObj = new Date(isoString);
    const estTime = new Intl.DateTimeFormat("en-US", {
        timeZone: "UTC",
        hour: "2-digit",
        minute: "2-digit",
        hour12: true,
    }).formatToParts(dateObj);
    const hours = estTime.find(part => part.type === "hour").value;
    const minutes = estTime.find(part => part.type === "minute").value;
    const ampm = estTime.find(part => part.type === "dayPeriod").value.toUpperCase();
    return `${hours}:${minutes} ${ampm}`;
}

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
        
        const currentDate = convertToEST(new Date());

        const q = query(eventsTable, 
            where("userId", "==", userId),
            where("startDateTime", ">=", currentDate), 
            orderBy("startDateTime"), 
            limit(6))
        const events = await getDocs(q)
        const eventsList = events.docs.map(event => ({
            eventId: event.id,
            userId: event.data().userId,
            description: event.data().description,
            startDate: convertISOToDate(event.data().startDateTime),
            startTime: convertISOToTime(event.data().startDateTime),
            endDate: convertISOToDate(event.data().endDateTime),
            endTime: convertISOToTime(event.data().endDateTime),
            location: event.data().location,
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

        const startDateTime = convertStringToESTDate(eventData.startDate, eventData.startTime);
        const endDateTime = convertStringToESTDate(eventData.endDate, eventData.endTime);

        const newEventData = {
            userId: userId,
            description: eventData.description,
            startDateTime: startDateTime,
            endDateTime: endDateTime,
            location: eventData.location,
            createdAt: serverTimestamp(),
            notificationSent: false,
            registrationToken: eventData.registrationToken
        }
        const eventsTable = collection(db, "events")
        const newEvent = await addDoc(eventsTable, newEventData)

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

        const startDateTime = convertStringToESTDate(eventData.startDate, eventData.startTime);
        const endDateTime = convertStringToESTDate(eventData.endDate, eventData.endTime);

        const updatedEventData = {
            description: eventData.description,
            startDateTime: startDateTime,
            endDateTime: endDateTime,
            location: eventData.location,
            createdAt: serverTimestamp(),
            notificationSent: false,
            registrationToken: eventData.registrationToken
        }

        await updateDoc(eventRef, updatedEventData)

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

export const sendNotifications = async () => {
    try {
        const db = getFirestore();

        const messaging = getMessaging();

        const eventsTable = collection(db, "events")
        const q = query(eventsTable, where("notificationSent", "==", false))
        const events = await getDocs(q)

        const timeNow = new Date()
        const oneHourLater = new Date(timeNow.getTime() + 60 * 60 * 1000);

        const estTimeNow = convertToEST(timeNow)
        const estOneHourLater = convertToEST(oneHourLater)

        if (events.empty) {
            return
        }


        events.forEach(async (eventDoc) => {
            const event = eventDoc.data()
            const { registrationToken, description, startDateTime } = event;

            const eventTime = (new Date(startDateTime)).toISOString()

            if (eventTime >= estTimeNow && eventTime <= estOneHourLater) {
                if (!registrationToken) {
                    return;
                }

                const message = {
                    notification: {
                        title: "Upcoming Event",
                        body: description,
                    },
                    token: registrationToken
                };

                try {
                    await messaging.send(message)
                    await updateDoc(doc(db, "events", eventDoc.id), { notificationSent: true });
                } catch (error) {
                    console.error("Error fetching events:", error);
                }
            }
        })
    } catch(error) {
        console.error("Error, sending notifications failed", error)
    }
}
