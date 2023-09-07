import Template from '../interfaces/template';
import { Integration, IntegrationModel } from '../schemas/integration';
import { Widget } from '../schemas/widget';

export async function getIntegrationByUserAndType(
  userId: string,
  type: string,
): Promise<Integration | null> {
  const integration = await IntegrationModel.findOne({ ownerId: userId, type });
  return integration as Integration;
}

export function getIntegrationByWidget(
  widget: Widget,
): Promise<Integration | null> {
  const template: Template = JSON.parse(widget.templateRaw);
  return getIntegrationByUserAndType(widget.userId, template.service);
}
