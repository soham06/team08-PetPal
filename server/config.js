import dotenv from 'dotenv';
import assert from 'assert';

dotenv.config({ path: './env/.env' });

const {
  PORT,
  HOST,
  HOST_URL,
} = process.env;

assert(PORT, 'Port is required');
assert(HOST, 'Host is required');

export default {
  port: PORT,
  host: HOST,
  hostUrl: HOST_URL,
};