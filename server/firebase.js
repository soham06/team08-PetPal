import admin from "firebase-admin";
import fs from 'fs';

const serviceAccount = JSON.parse(
  await fs.readFileSync(new URL("./env/petpal-5860b-firebase-adminsdk-fbsvc-36d2aaf584.json", import.meta.url))
);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const firebaseConnection = admin.firestore();

export default firebaseConnection;