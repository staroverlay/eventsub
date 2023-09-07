import { Widget, WidgetModel } from '../schemas/widget';

export async function getWidgetByToken(token: string): Promise<Widget | null> {
  const widget = await WidgetModel.findOne({ token });
  return widget as Widget;
}
