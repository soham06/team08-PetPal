import admin from "firebase-admin";
import firebaseConnection from '../firebase.js'

export async function getTasksForUser (req, res) {
    try {
        const { userId } = req.params;

        if (!userId) {
            return res.status(400).json({ message: "Invalid request params, please provide a userId"});
        }

        const usersTable = firebaseConnection.collection('users')
        const usersQuerySnapshot = await usersTable.where(admin.firestore.FieldPath.documentId(), '==', userId).get()

        if (usersQuerySnapshot.empty) {
            return res.status(404).json({ message: "This user doesn't exist"});
        }

        const tasksTable = firebaseConnection.collection('tasks')
        const tasksQuerySnapshot = await tasksTable.where("userId", '==', userId).get()
        const tasksList = tasksQuerySnapshot.docs.map(task => ({
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

        const { userId } = req.params;

        if (!userId) {
            return res.status(400).json({ message: "Invalid request params, please provide a userId"});
        }

        const usersTable = firebaseConnection.collection('users')
        const usersQuerySnapshot = await usersTable.where(admin.firestore.FieldPath.documentId(), '==', userId).get()

        if (usersQuerySnapshot.empty) {
            return res.status(404).json({ message: "This user doesn't exist"});
        }

        const taskData = req.body;

        if (!taskData.description || !taskData.status) {
            return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        taskData["createdAt"] = admin.firestore.FieldValue.serverTimestamp();
        taskData["userId"] = userId;
        const tasksTable = firebaseConnection.collection('tasks')
        const createdTaskId = (await tasksTable.add(taskData)).id;

        const fetchedCreatedTask = (await tasksTable.where(admin.firestore.FieldPath.documentId(), "==", createdTaskId).get())
        const createdTask = fetchedCreatedTask.docs.map(task => ({
            taskId: task.id,
            ...task.data()
        }))[0];
        res.status(200).json(createdTask);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

// TODO: implement controllers to update and delete tasks