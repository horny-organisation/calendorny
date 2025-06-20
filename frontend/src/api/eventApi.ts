import { http } from './authApi';

export interface CreateEventRequest {
  title: string;
  description?: string;
  location?: string;
  start: string;
  end: string;
  rrule?: RruleDto;
  labels?: number[];
  isMeeting?: boolean;
  meetingType?: MeetingType;
  participants?: ParticipantDto[];
  reminder?: ReminderDto;
}

export interface UpdateEventInfoRequest {
  title: string;
  description?: string;
  location?: string;
  start: string;
  end: string;
  rrule?: RruleDto;
  labels?: number[];
  isMeeting?: boolean;
  meetingType?: MeetingType;
  participants?: ParticipantDto[];
}

export interface UpdateEventReminderRequest {
  reminder: ReminderDto;
}

export interface EventDetailedResponse {
  id: number;
  title: string;
  description?: string;
  location?: string;
  startTime: string;
  endTime: string;
  rrule?: RruleDto;
  labels?: LabelDto[];
  isMeeting?: boolean;
  meetingType?: MeetingType;
  videoMeetingUrl?: string;
  organizerId: string;
  participants?: ParticipantDto[];
  reminder?: ReminderDto;
}

export interface EventShortResponse {
  id: number;
  title: string;
  location?: string;
  startTime: string;
  endTime: string;
  labels?: LabelDto[];
}

export interface LabelDto {
  id: number;
  name: string;
  color: string;
}

export interface ParticipantDto {
  userId: string;
  email: string;
  status: ParticipantStatus;
}

export interface ReminderDto {
  minutesBefore: number[];
}

export interface RruleDto {
  frequency?: EventFrequency;
  dayOfWeek?: DayOfWeek;
  dayOfMonth?: number;
}

export type EventFrequency = 'WEEKLY' | 'MONTHLY';
export type MeetingType = 'NONE' | 'GOOGLE' | 'ZOOM';
export type ParticipantStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';
export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export async function createEvent(payload: CreateEventRequest): Promise<EventDetailedResponse> {
  const resp = await http.post<EventDetailedResponse>('/events', payload);
  return resp.data;
}

export async function getEvents(from: string, to: string): Promise<EventShortResponse[]> {
  const resp = await http.get<EventShortResponse[]>('/events', { params: { from, to } });
  return resp.data;
}

export async function getEventById(eventId: number): Promise<EventDetailedResponse> {
  const resp = await http.get<EventDetailedResponse>(`/events/${eventId}`);
  return resp.data;
}

export async function deleteEvent(eventId: number): Promise<void> {
  await http.delete(`/events/${eventId}`);
}

export async function updateEventInfo(eventId: number, payload: UpdateEventInfoRequest): Promise<void> {
  await http.put(`/events/${eventId}/info`, payload);
}

export async function updateEventReminder(eventId: number, payload: UpdateEventReminderRequest): Promise<void> {
  await http.patch(`/events/${eventId}/reminder`, payload);
}

export async function getInvitations(): Promise<EventDetailedResponse[]> {
  const resp = await http.get<EventDetailedResponse[]>('/events/invitations');
  return resp.data;
}

export async function answerInvitation(eventId: number, answer: ParticipantStatus): Promise<EventDetailedResponse> {
  const resp = await http.post<EventDetailedResponse>(`/events/invitations/${eventId}`, null, { params: { answer } });
  return resp.data;
}

export async function getEventLabels(): Promise<LabelDto[]> {
  const resp = await http.get<LabelDto[]>('/events/labels');
  return resp.data;
} 