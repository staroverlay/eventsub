import cors from 'cors';
import express from 'express';
import morgan from 'morgan';

import Gateway from './gateway';

const app = express();
const gateway = new Gateway();

// Development middlewares.
if (process.env.NODE_ENV === 'development') {
  app.use(morgan('dev'));
}

// Middlewares.
app.use(
  cors({
    origin: [process.env['RENDERER_SERVER']],
  }),
);
app.use(express.json());

// API routes.
app.post('/api/trigger/:widgetId', (req, res) => {
  const { event, data } = req.body || {};
  const widgetId = req.params.widgetId;
  const secretKey = req.headers['x-eventsub-secret'];

  if (!event || !data || !widgetId) {
    return res.status(400).json({ error: 'Bad request' });
  } else if (!secretKey || secretKey !== process.env['SECRET_KEY']) {
    return res.status(401).json({ error: 'Invalid X-Eventsub-Secret header' });
  }

  gateway.debug(widgetId, event, data);
  return res.status(200).json({ status: 'ok' });
});

// Create and exports gateway attached to express server.
export default gateway.createServer(app);
