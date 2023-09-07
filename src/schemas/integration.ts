import { Schema, model, Types } from 'mongoose';

export type Integration = {
  _id: Types.ObjectId;
  accessToken: string;
  refreshToken: string;
  ownerId: string;
  avatar: string;
  integrationId: string;
  username: string;
  type: string;
  expires: number;
};

export const IntegrationModel = model(
  'Integration',
  new Schema({
    accessToken: String,
    refreshToken: String,
    ownerId: String,
    avatar: String,
    integrationId: String,
    username: String,
    type: String,
    expires: Number,
  }),
);
