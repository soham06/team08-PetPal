import admin from "firebase-admin";
import fs from 'fs';
import { initializeApp } from "firebase/app";
import config from './config.js'

const firebaseConnection = initializeApp(config.firebaseConfig);

export default firebaseConnection;