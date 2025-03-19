import firebaseConnection from '../firebase.js'
import { getFirestore, collection, getDoc,
         getDocs, query, where, addDoc, doc,
         serverTimestamp, updateDoc, deleteDoc, arrayUnion, arrayRemove } from 'firebase/firestore';

export async function getPostingsForUser (req, res) {
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

        const postingsTable = collection(db, "postings")
        const q = query(postingsTable, where("userId", "==", userId))
        const posts = await getDocs(q)
        const postsList = posts.docs.map(post => ({
            postId: post.id,
            ...post.data()
        }));
        res.status(200).json(postsList);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};


export async function getAllPostings(req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const postingsTable = collection(db, "postings");
        const postsSnapshot = await getDocs(postingsTable);
        const postsList = postsSnapshot.docs.map(post => ({
            postId: post.id,
            ...post.data()
        }));
        res.status(200).json(postsList);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

export async function createPostingForUser (req, res) {
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

        const postData = req.body;
        if (!postData.city || !postData.description || !postData.email || !postData.name || !postData.phone) {
            return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        postData["userId"] = userId;
        const postingsTable = collection(db, "postings")
        const newPost = await addDoc(postingsTable, postData)

        const fetchedCreatedPost = await getDoc(newPost)
        const createdPost = {
            postId: fetchedCreatedPost.id,
            ...fetchedCreatedPost.data(),
        };
        res.status(200).json(createdPost);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function updatePostForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { postId } = req.params;

        if (!postId) {
            return res.status(400).json({ message: "Invalid request params, please provide a postId"});
        }

        const postRef = doc(db, "postings", postId);
        const postDoc = await getDoc(postRef);

        if (!postDoc.exists()) {
            return res.status(404).json({ message: "This post doesn't exist"});
        }

        const postData = req.body;

        if (!postData.city || !postData.description || !postData.email || !postData.name || !postData.phone) {
            return res.status(400).json({ message: "Invalid request body, please ensure all required fields are present"});
        }

        await updateDoc(postRef, postData)

        const fetchedUpdatedPost = await getDoc(postRef)
        const fetchedPost = {
            postId: fetchedUpdatedPost.id,
            ...fetchedUpdatedPost.data(),
        };
        res.status(200).json(fetchedPost);
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};

export async function deletePostForUser (req, res) {
    try {
        const db = getFirestore(firebaseConnection);
        const { postId } = req.params;

        if (!postId) {
            return res.status(400).json({ message: "Invalid request params, please provide a postId"});
        }

        const postRef = doc(db, "postings", postId);
        const postDoc = await getDoc(postRef);

        if (!postDoc.exists()) {
            return res.status(404).json({ message: "This post doesn't exist"});
        }

        await deleteDoc(postRef)
        res.status(200).json("Successfully deleted post");
    } catch (error) {
        res.status(400).json({ message: error.message});
    }
};
