import { http } from './authApi';

export interface CreateTaskRequest {
  title: string;
  description?: string;
  dueDate: string;
  rrule?: RruleDto;
}

export interface UpdateTaskRequest {
  title: string;
  description?: string;
  dueDate: string;
  status: TaskStatus;
  rrule?: RruleDto;
}

export interface UpdateTaskStatusRequest {
  status: TaskStatus;
}

export interface TaskResponse {
  id: string;
  userId: string;
  title: string;
  description?: string;
  dueDate: string;
  status: TaskStatus;
  recurrenceRule?: RruleDto;
}

export interface RruleDto {
  frequency?: TaskFrequency;
  dayOfWeek?: DayOfWeek;
  dayOfMonth?: number;
}

export type TaskStatus = 'PENDING' | 'COMPLETED' | 'CANCELLED';
export type TaskFrequency = 'WEEKLY' | 'MONTHLY';
export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export async function createTask(payload: CreateTaskRequest): Promise<TaskResponse> {
  const resp = await http.post<TaskResponse>('/tasks', payload);
  return resp.data;
}

export async function getTasks(from: string, to: string): Promise<TaskResponse[]> {
  const resp = await http.get<TaskResponse[]>('/tasks', { params: { from, to } });
  return resp.data;
}

export async function getTaskById(taskId: string): Promise<TaskResponse> {
  const resp = await http.get<TaskResponse>(`/tasks/${taskId}`);
  return resp.data;
}

export async function updateTask(taskId: string, payload: UpdateTaskRequest): Promise<TaskResponse> {
  const resp = await http.put<TaskResponse>(`/tasks/${taskId}`, payload);
  return resp.data;
}

export async function deleteTask(taskId: string): Promise<void> {
  await http.delete(`/tasks/${taskId}`);
}

export async function updateTaskStatus(taskId: string, payload: UpdateTaskStatusRequest): Promise<TaskResponse> {
  const resp = await http.patch<TaskResponse>(`/tasks/${taskId}/status`, payload);
  return resp.data;
} 