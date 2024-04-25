import type {Course} from './model/Course';
import type {GetCourseReviewsInteractionPayload} from './model/GetCourseReviewsInteractionsPayload';
import type {GetCourseWithReviewsPayload} from './model/GetCourseWithReviewsPayload';
import type {Interaction, InteractionKind} from './model/Interaction';
import type {Notification} from './model/Notification';
import type {Review} from './model/Review';
import type {Subscription} from './model/Subscription';
import type {User} from './model/User';
import {backendUrl} from "$lib/constants";
import type {Instructor} from "$lib/model/Instructor";

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
            default:
                throw new Error(`Unsupported method: ${method}`);
        }
    },
};

export const repo = {
    async getSubscription(courseId: string): Promise<Subscription | null> {
        return await client.deserialize<Subscription | null>(
            'GET',
            `/subscriptions?courseId=${courseId}`,
            {
                headers: {'Content-Type': 'application/json'},
            }
        );
    },

    async getSubscriptions(): Promise<Subscription[]> {
        return await client.deserialize<Subscription[]>('GET', '/subscriptions', {
            headers: {'Content-Type': 'application/json'},
        });
    },

    async addSubscription(courseId: string): Promise<Response> {
        return await client.post('/subscriptions', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({courseId}),
        });
    },

    async removeSubscription(courseId: string): Promise<Response> {
        return await client.delete('/subscriptions', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({courseId}),
        });
    },

    async getReviews(userId: string): Promise<Review[]> {
        return await client.deserialize<Review[]>('GET', `/reviews?userId=${userId}`);
    },

    async addReview(courseId: string, values: any): Promise<Response> {
        return await client.post('/reviews', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                courseId,
                timestamp: new Date(),
                ...values,
            }),
        });
    },

    async updateReview(review: Review, values: any): Promise<Response> {
        return await client.put('/reviews', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                _id: review._id,
                content: values.content,
                courseId: review.courseId,
                instructor: values.instructor,
                experience: values.experience,
                difficulty: values.difficulty,
                timestamp: new Date(),
                userId: review.userId,
                likes: review.likes
            }),
        });
    },

    async deleteReview(courseId: string): Promise<Response> {
        return await client.delete('/reviews', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({courseId}),
        });
    },

    async getInteractions(
        courseId: string,
        userId: string,
        referrer: string | undefined
    ): Promise<InteractionKind> {
        return await client.deserialize<InteractionKind>(
            'GET',
            `/interactions?courseId=${courseId}&userId=${userId}&referrer=${referrer}`
        );
    },

    async getUserInteractionsForCourse(
        courseId: string,
        referrer: string
    ): Promise<GetCourseReviewsInteractionPayload> {
        return await client.deserialize<GetCourseReviewsInteractionPayload>(
            'GET',
            `/interactions/${courseId}/referrer/${referrer}`
        );
    },

    async getUserInteractions(
        referrer: string
    ): Promise<Interaction[]> {
        return await client.deserialize<Interaction[]>(
            'GET',
            `/interactions/referrer/${referrer}`
        );
    },

    async addOrUpdateInteraction(
        kind: InteractionKind,
        courseId: string,
        userId: string,
        referrer: string | undefined
    ): Promise<Response> {
        return await client.post('/interactions', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                kind,
                courseId,
                userId,
                referrer,
            }),
        });
    },

    async removeInteraction(
        courseId: string,
        userId: string,
        referrer: string | undefined
    ): Promise<Response> {
        return await client.delete('/interactions', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                courseId,
                userId,
                referrer,
            }),
        });
    },

    async getNotifications(): Promise<Notification[]> {
        return await client.deserialize<Notification[]>('GET', '/notifications');
    },

    async updateNotification(
        courseId: string,
        creatorId: string,
        seen: boolean
    ): Promise<Response> {
        return await client.put('/notifications', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                courseId,
                creatorId,
                seen,
            }),
        });
    },

    async deleteNotification(courseId: string, creatorId: string): Promise<Response> {
        return await client.delete('/notifications', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                courseId,
                creatorId
            }),
        });
    },

    async getCourseWithReviews(
        id: string | undefined
    ): Promise<GetCourseWithReviewsPayload | null> {
        return await client.deserialize<GetCourseWithReviewsPayload | null>(
            'GET',
            `/courses/${id}?with_reviews=true`
        );
    },

    async getCourses(
        limit: number,
        offset: number,
        filters: any
    ): Promise<Course[]> {
        return await client.deserialize<Course[]>(
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

    async getInstructors(
        limit: number,
        offset: number,
        filters: any
    ): Promise<Instructor[]> {
        return await client.deserialize<Instructor[]>(
            'POST',
            `/instructors/filter?limit=${limit}&offset=${offset}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(filters),
            }
        );
    },

    async getReviewsFeed(
        limit: number,
        offset: number,
        filters: any
    ): Promise<Review[]> {
        return await client.deserialize<Review[]>(
            'POST',
            `/reviews/filter?limit=${limit}&offset=${offset}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(filters),
            }
        );
    },

    async signUp(user: User): Promise<Response> {
        return await client.post('/auth/signup', {
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user),
        });
    },

    async signIn(user: User): Promise<Response> {
        return await client.post('/auth/signin', {
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user),
        });
    },

    async signOut(): Promise<Response> {
        return await client.get('/auth/signout');
    },

    async getNewToken(): Promise<Response> {
        return await client.get('/auth/resend_token');
    },

    async verifyToken(token: string): Promise<Response> {
        return await client.post('/auth/authorized', {
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(token),
        });
    },

    async getUser(fetch: {
        (input: RequestInfo | URL, init?: RequestInit | undefined): Promise<Response>;
        (arg0: string): any;
    }, cookies: string): Promise<string|null> {
        const backendURL = `${backendUrl}/api/v1/auth/user`;
        const response = await fetch(backendURL, {
            credentials: 'include',
            headers: {
                'Cookie': cookies,
            },
        });
        if(response.ok){
            const body = await response.json();
            return body.payload;
        }
        return null;
    },

    async resetPassword(token: string): Promise<{ username: string, error: string }> {
        const backendURL = `${backendUrl}/api/v1/auth/reset_password?token=${token}`;
        const response = await fetch(backendURL);
        const body = await response.json()
        return {
            username: body.payload,
            error: body.errors
        };
    },

    async forgotPassword(username: string): Promise<Response> {
        return await client.get(`/auth/forgot_password?username=${username}`);
    },

    async updatePassword(newPassword: string, token: string): Promise<Response> {
        return await client.put(`/auth/update_password?token=${token}`, {
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({newPassword}),
        });
    }
};
