import express from 'express';
import bodyParser from 'body-parser';
import cors from 'cors';
import routes from './routes/index.js';
import config from './config.js';
import firebaseConnection from './firebase.js'
import {initializeApp} from "firebase-admin/app";
import { getMessaging } from 'firebase-admin/messaging'
import admin from 'firebase-admin'
import { readFile } from "fs/promises";
import cron from 'node-cron'
import { sendNotifications } from './controllers/events.js'

const app = express();

app.use(bodyParser.json({limit: "30mb", extended: true}));
app.use(bodyParser.urlencoded({limit: "30mb", extended: true}));
app.use(cors());
app.use(express.json())


app.use('/api', routes);

const serviceAccount = JSON.parse(
  await readFile(new URL("./env/petpal-5860b-firebase-adminsdk-fbsvc-db425dd505.json", import.meta.url), "utf-8")
);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

cron.schedule("* * * * *", () => {
    console.log("Sending notifications");
    sendNotifications();
});

app.listen(config.port, () =>
  console.log(`Server is running on: ${config.hostUrl}`),
);
