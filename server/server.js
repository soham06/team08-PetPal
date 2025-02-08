import express from 'express';
import bodyParser from 'body-parser';
import cors from 'cors';
import routes from './routes/index.js';
import config from './config.js';
import firebaseConnection from './firebase.js'

const app = express();

app.use(bodyParser.json({limit: "30mb", extended: true}));
app.use(bodyParser.urlencoded({limit: "30mb", extended: true}));
app.use(cors());
app.use(express.json())


app.use('/api', routes);

app.listen(config.port, () =>
  console.log(`Server is running on: ${config.hostUrl}`),
);

