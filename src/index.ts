require('dotenv').config();

import app from './app';
import mongoose from 'mongoose';

async function asyncListen(): Promise<number> {
  return new Promise((resolve, reject) => {
    const rawPort = process.env.PORT;
    const port = rawPort ? Number(rawPort) : 3000;

    try {
      app.listen(port, () => {
        resolve(port);
      });
    } catch (e) {
      reject(e);
    }
  });
}

async function start(): Promise<void> {
  await mongoose.connect(process.env.MONGODB_URI);
  console.log('Database connected');

  const port = await asyncListen();
  console.log(`Listening on port ${port}`);
}

start();
