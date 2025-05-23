import firebaseConnection from '../firebase.js'
import { getFirestore, collection, getDoc, 
         getDocs, query, where, addDoc, doc,
         serverTimestamp, updateDoc, deleteDoc } from 'firebase/firestore';

export async function getTasksForUser (req, res) {
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

        const tasksTable = collection(db, "tasks")
        const q = query(tasksTable, where("userId", "==", userId), where("status", "==", "OPEN"))
        const tasks = await getDocs(q)
        const tasksList = tasks.docs.map(task => ({
            taskId: task.id,
            ...task.data()
        }));
        res.status(200).json(tasksList);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function createTaskForUser (req, res) {
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

        const taskData = req.body;

        if (!taskData.description || !taskData.status) {
            return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        taskData["createdAt"] = serverTimestamp();
        taskData["userId"] = userId;
        const tasksTable = collection(db, "tasks")
        const newTask = await addDoc(tasksTable, taskData)

        const fetchedCreatedTask = await getDoc(newTask)
        const createdTask = {
            taskId: fetchedCreatedTask.id,
            ...fetchedCreatedTask.data(),
        };
        res.status(200).json(createdTask);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function updateTaskForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { taskId } = req.params;

        if (!taskId) {
            return res.status(400).json({ message: "Invalid request params, please provide a taskId"});
        }

        const taskRef = doc(db, "tasks", taskId);
        const taskDoc = await getDoc(taskRef);

        if (!taskDoc.exists()) {
            return res.status(404).json({ message: "This task doesn't exist"});
        }

        const taskData = req.body;

        if (!taskData.description || !taskData.status) {
            return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        await updateDoc(taskRef, taskData)

        const fetchedUpdatedTask = await getDoc(taskRef)
        const fetchedTask = {
            taskId: fetchedUpdatedTask.id,
            ...fetchedUpdatedTask.data(),
        };
        res.status(200).json(fetchedTask);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function deleteTaskForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { taskId } = req.params;

        if (!taskId) {
            return res.status(400).json({ message: "Invalid request params, please provide a taskId"});
        }

        const taskRef = doc(db, "tasks", taskId);
        const taskDoc = await getDoc(taskRef);

        if (!taskDoc.exists()) {
            return res.status(404).json({ message: "This task doesn't exist"});
        }

        await deleteDoc(taskRef)
        res.status(200).json("Successfully deleted task");
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};