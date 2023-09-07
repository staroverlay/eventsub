import { Types } from 'mongoose';

export default interface Template {
  _id: Types.ObjectId;
  author: string;
  name: string;
  description?: string;
  scopes?: string[];
  service?: string;
  html: string;
  fields?: string[];
  visibility: string;
  version: number;
}
