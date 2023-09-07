import Topic from './topic';

export default interface Subscriber {
  start(): void;
  stop(): void;
  subscribe: (topics: Topic[]) => Promise<void>;
  onEvent(callback: (eventName: string, eventData: any) => void): void;
}
