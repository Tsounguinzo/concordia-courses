import type { Course } from './model/Course';
import type { GetCourseReviewsInteractionPayload } from './model/GetCourseReviewsInteractionsPayload';
import type { GetCourseWithReviewsPayload } from './model/GetCourseWithReviewsPayload';
import type { InteractionKind } from './model/Interaction';
import type { Notification } from './model/Notification';
import type { Review } from './model/Review';
import type { Subscription } from './model/Subscription';
import type {User, UserResponse} from './model/User';

const prefix = '/api/v1';

type Method = 'GET' | 'POST' | 'PUT' | 'DELETE';

const client = {
  async get(endpoint: string, init?: RequestInit): Promise<Response> {
    return fetch(prefix + endpoint, init);
  },

  async post(endpoint: string, init?: RequestInit): Promise<Response> {
    return fetch(prefix + endpoint, {
      method: 'POST',
      ...init,
    });
  },

  async put(endpoint: string, init?: RequestInit): Promise<Response> {
    return fetch(prefix + endpoint, {
      method: 'PUT',
      ...init,
    });
  },

  async delete(endpoint: string, init?: RequestInit): Promise<Response> {
    return fetch(prefix + endpoint, {
      method: 'DELETE',
      ...init,
    });
  },

  async deserialize<T>(
    method: Method,
    endpoint: string,
    init?: RequestInit
  ): Promise<T> {
    const run = async (
      fn: (endpoint: string, init?: RequestInit) => Promise<Response>
    ): Promise<T> => (await (await fn(endpoint, init)).json()) as T;

    switch (method) {
      case 'GET':
        return run(this.get);
      case 'POST':
        return run(this.post);
      case 'PUT':
        return run(this.put);
      case 'DELETE':
        return run(this.delete);
    }
  },
};

export const repo = {
  async getSubscription(courseId: string): Promise<Subscription | null> {
    return client.deserialize<Subscription | null>(
      'GET',
      `/subscriptions?courseId=${courseId}`,
      {
        headers: { 'Content-Type': 'application/json' },
      }
    );
  },

  async getSubscriptions(): Promise<Subscription[]> {
    return client.deserialize<Subscription[]>('GET', '/subscriptions', {
      headers: { 'Content-Type': 'application/json' },
    });
  },

  async addSubscription(courseId: string): Promise<Response> {
    return client.post('/subscriptions', {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ courseId: courseId }),
    });
  },

  async removeSubscription(courseId: string): Promise<Response> {
    return client.delete('/subscriptions', {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ courseId: courseId }),
    });
  },

  async getReviews(userId: string): Promise<Review[]> {
    return client.deserialize<Review[]>('GET', `/reviews?userId=${userId}`);
  },

  async addReview(courseId: string, values: any): Promise<Response> {
    return client.post(`/reviews`, {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        courseId: courseId,
        timestamp: new Date(),
        ...values,
      }),
    });
  },

  async updateReview(review: Review, values: any): Promise<Response> {
    return client.put(`/reviews`, {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
          _id: review._id,
          content: values.content,
          courseId: review.courseId,
          instructor: values.instructor,
          rating: values.rating,
          difficulty: values.difficulty,
          timestamp: new Date(),
          userId: review.userId,
          likes: review.likes
      }),
    });
  },

  async deleteReview(courseId: string): Promise<Response> {
    return client.delete('/reviews', {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ courseId: courseId }),
    });
  },

  async getInteractions(
    courseId: string,
    userId: string,
    referrer: string | undefined
  ): Promise<InteractionKind> {
    return client.deserialize<InteractionKind>(
      'GET',
      `/interactions?courseId=${courseId}&userId=${userId}&referrer=${referrer}`
    );
  },

  async getUserInteractionsForCourse(
    courseId: string,
    referrer: string
  ): Promise<GetCourseReviewsInteractionPayload> {
    return client.deserialize<GetCourseReviewsInteractionPayload>(
      'GET',
      `/interactions/${courseId}/referrer/${referrer}`
    );
  },

  async addInteraction(
    kind: InteractionKind,
    courseId: string,
    userId: string,
    referrer: string | undefined
  ): Promise<Response> {
    return client.post('/interactions', {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        kind,
        courseId: courseId,
        userId: userId,
        referrer,
      }),
    });
  },

  async removeInteraction(
    courseId: string,
    userId: string,
    referrer: string | undefined
  ): Promise<Response> {
    return client.delete('/interactions', {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        courseId: courseId,
        userId: userId,
        referrer,
      }),
    });
  },

  async getNotifications(): Promise<Notification[]> {
    return client.deserialize<Notification[]>('GET', '/notifications');
  },

  async updateNotification(
    courseId: string,
    creatorId: string,
    seen: boolean
  ): Promise<Response> {
    return client.put('/notifications', {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        courseId: courseId,
        creatorId: creatorId,
        seen: seen,
      }),
    });
  },

  async deleteNotification(courseId: string, creatorId: string): Promise<Response> {
    return client.delete('/notifications', {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        courseId: courseId,
        creatorId: creatorId
      }),
    });
  },

  async getCourseWithReviews(
    id: string | undefined
  ): Promise<GetCourseWithReviewsPayload | null> {
    return client.deserialize<GetCourseWithReviewsPayload | null>(
      'GET',
      `/courses/${id}?with_reviews=true`
    );
  },

  async getCourses(
    limit: number,
    offset: number,
    filters: any
  ): Promise<Course[]> {
    return client.deserialize<Course[]>(
      'POST',
      `/courses/filter?limit=${limit}&offset=${offset}`,
      {
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(filters),
      }
    );
  },

  async signUp(user: User): Promise<Response> {
    return client.post( '/auth/signup', {
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(user),
    });
  },

  async signIn(user: User): Promise<Response> {
    return client.post('/auth/signin', {
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(user),
    });
  },

  async getUser(fetch: { (input: RequestInfo | URL, init?: RequestInit | undefined): Promise<Response>; (arg0: string): any; }): Promise<string> {
    const backendURL = 'http://localhost:8080/api/v1/auth/user';
    const response = await fetch(backendURL);
    const body = await response.json()
    return body.payload;
  },
};
