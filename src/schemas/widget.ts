import Topic from '../interfaces/topic';
import { Schema, model, Types } from 'mongoose';

export type Widget = {
  _id: Types.ObjectId;
  displayName: string;
  userId: string;
  enabled: boolean;
  token: string;
  template: string;
  templateId: string;
  templateRaw: string;
  settings: string;
  scopes: Topic[];
};

export const WidgetModel = model(
  'Widget',
  new Schema({
    displayName: String,
    userId: String,
    enabled: Boolean,
    token: String,
    template: String,
    templateId: String,
    templateRaw: String,
    settings: String,
    scopes: [String],
  }),
);
