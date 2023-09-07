import { ApiClient } from '@twurple/api';
import { StaticAuthProvider } from '@twurple/auth';
import { EventSubWsListener } from '@twurple/eventsub-ws';
import { TwitchOAuth } from 'twitch-oauth';

import Subscriber from '../interfaces/subscriber';
import Topic from '../interfaces/topic';

const CLIENT_ID = process.env.TWITCH_CLIENT_ID;
const CLIENT_SECRET = process.env.TWITCH_CLIENT_SECRET;

const client = new TwitchOAuth({
  clientId: CLIENT_ID,
  clientSecret: CLIENT_SECRET,
  redirectUri: '',
  scope: [],
  method: 'code',
});

async function getAccessTokenByRefresh(refreshToken: string): Promise<string> {
  const oauth = await client.refresh(refreshToken);
  return oauth.access_token;
}

async function createListener(
  refreshToken: string,
): Promise<EventSubWsListener> {
  const accessToken = await getAccessTokenByRefresh(refreshToken);
  const authProvider = new StaticAuthProvider(CLIENT_ID, accessToken);
  const apiClient = new ApiClient({ authProvider });
  const listener = new EventSubWsListener({ apiClient });
  return listener;
}

export async function createTwitchSubscriber(
  userId: string,
  refreshToken: string,
): Promise<Subscriber> {
  const listener = await createListener(refreshToken);
  let callback: (topic: Topic, e: any) => void | null = null;

  const craftHandler = (topic: Topic) => {
    return (event: any) => {
      const symbols = Object.getOwnPropertySymbols(event);
      const data = event[symbols[0]];
      callback(topic, data);
    };
  };

  return {
    start: () => {
      listener.start();
    },
    stop: () => {
      listener.stop();
    },
    onEvent: (newCallback) => {
      callback = newCallback;
    },
    subscribe: async (topics) => {
      for (const topic of topics) {
        const handler = craftHandler(topic);

        if (topic === 'twitch:ban') {
          listener.onChannelBan(userId, handler);
        } else if (topic === 'twitch:unban') {
          listener.onChannelUnban(userId, handler);
        } else if (topic === 'twitch:charity') {
          listener.onChannelCharityCampaignProgress(userId, handler);
          listener.onChannelCharityCampaignStart(userId, handler);
          listener.onChannelCharityCampaignStop(userId, handler);
          listener.onChannelCharityDonation(userId, handler);
        } else if (topic === 'twitch:cheer') {
          listener.onChannelCheer(userId, handler);
        } else if (topic === 'twitch:follow') {
          listener.onChannelFollow(userId, userId, handler);
        } else if (topic === 'twitch:goal') {
          listener.onChannelGoalBegin(userId, handler);
          listener.onChannelGoalEnd(userId, handler);
          listener.onChannelGoalProgress(userId, handler);
        } else if (topic === 'twitch:hype_train') {
          listener.onChannelHypeTrainBegin(userId, handler);
          listener.onChannelHypeTrainEnd(userId, handler);
          listener.onChannelHypeTrainProgress(userId, handler);
        } else if (topic === 'twitch:moderator') {
          listener.onChannelModeratorAdd(userId, handler);
          listener.onChannelModeratorRemove(userId, handler);
        } else if (topic === 'twitch:poll') {
          listener.onChannelPollBegin(userId, handler);
          listener.onChannelPollEnd(userId, handler);
          listener.onChannelPollProgress(userId, handler);
        } else if (topic === 'twitch:prediction') {
          listener.onChannelPredictionBegin(userId, handler);
          listener.onChannelPredictionEnd(userId, handler);
          listener.onChannelPredictionLock(userId, handler);
          listener.onChannelPredictionProgress(userId, handler);
        } else if (topic === 'twitch:raid') {
          listener.onChannelRaidFrom(userId, handler);
        } else if (topic === 'twitch:raid_to') {
          listener.onChannelRaidTo(userId, handler);
        } else if (topic === 'twitch:redemption') {
          listener.onChannelRedemptionAdd(userId, handler);
        } else if (topic === 'twitch:reward') {
          listener.onChannelRewardAdd(userId, handler);
          listener.onChannelRewardUpdate(userId, handler);
          listener.onChannelRewardRemove(userId, handler);
        } else if (topic === 'twitch:shield_mode') {
          listener.onChannelShieldModeBegin(userId, userId, handler);
          listener.onChannelShieldModeEnd(userId, userId, handler);
        } else if (topic === 'twitch:shoutout') {
          listener.onChannelShoutoutCreate(userId, userId, handler);
        } else if (topic === 'twitch:shoutout_receive') {
          listener.onChannelShoutoutReceive(userId, userId, handler);
        } else if (topic === 'twitch:stream-off') {
          listener.onStreamOffline(userId, handler);
        } else if (topic === 'twitch:stream-up') {
          listener.onStreamOnline(userId, handler);
        } else if (topic == 'twitch:subscription') {
          listener.onChannelSubscription(userId, handler);
          listener.onChannelSubscriptionGift(userId, handler);
        } else if (topic == 'twitch:update') {
          listener.onChannelUpdate(userId, handler);
        }
      }
    },
  };
}
