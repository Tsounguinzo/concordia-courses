import type {Course} from './model/Course';
import type {GetCourseReviewsInteractionPayload} from './model/GetCourseReviewsInteractionsPayload';
import type {GetCourseWithReviewsPayload} from './model/GetCourseWithReviewsPayload';
import type {Interaction, InteractionKind} from './model/Interaction';
import type {Notification} from './model/Notification';
import type {Review, ReviewType} from './model/Review';
import type {Subscription} from './model/Subscription';
import type {User} from './model/User';
import {backendUrl} from "$lib/constants";
import type {CourseInstructor, Instructor} from "$lib/model/Instructor";
import type {GetInstructorWithReviewsPayload} from "$lib/model/GetInstructorWithReviewsPayload";
import type {GetInstructorReviewsInteractionPayload} from "$lib/model/GetInstructorReviewsInteractionPayload";
import {ISOFormattedDateUTC4} from "$lib/utils";
import type {SortFilterDto} from "./types";

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

    async getSharedReviews(id: string): Promise<{ review: Review, error: string }> {
        const backendURL = `${backendUrl}/api/v1/reviews/shared?id=${id}`;
        const response = await fetch(backendURL);
        const body = await response.json()
        return {
            review: body.payload,
            error: body.errors?.message
        };
    },

    async addReview(courseId: string, instructorId: string, type: ReviewType, values: any, visitorId: string | null): Promise<Response> {
        return await client.post('/reviews', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                type,
                userId: visitorId,
                content: values.content,
                timestamp: ISOFormattedDateUTC4(new Date()),
                difficulty: values.difficulty,
                courseId: values.course !== '' ? values.course?.replaceAll(/[^a-zA-Z0-9]/g, '').toUpperCase() : courseId,
                instructorId: values.instructor !== '' ? values.instructor?.replaceAll(/\s+/g, '-').toLowerCase() : instructorId,
                experience: values.experience,
                rating: values.rating,
                tags: values.tags,
                schoolId: values.school,
                resourceLinks: values.resourceLinks || [],
            }),
        });
    },

    async updateReview(review: Review, values: any): Promise<Response> {
        return await client.put('/reviews', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                _id: review._id,
                type: review.type,
                content: values.content,
                timestamp: ISOFormattedDateUTC4(new Date()),
                difficulty: values.difficulty || review.difficulty,
                courseId: values.course?.replaceAll(/[^a-zA-Z0-9]/g, '').toUpperCase() ?? review.courseId,
                instructorId: values.instructor?.replaceAll(/\s+/g, '-').toLowerCase() ?? review.instructorId,
                experience: values.experience || review.experience,
                rating: values.rating || review.rating,
                tags: values.tags,
                schoolId: values.school,
                userId: review.userId,
                likes: review.likes,
                resourceLinks: values.resourceLinks || review.resourceLinks || [], // Use new links, fallback to existing, then empty
            }),
        });
    },

    async deleteReview(id: string, type: string, courseId: string, instructorId: string, userId: string): Promise<Response> {
        return await client.delete('/reviews', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({id, type, courseId, instructorId, userId}),
        });
    },

    async getUserInteractionsForCourse(
        courseId: string,
        referrer: string
    ): Promise<GetCourseReviewsInteractionPayload> {
        return await client.deserialize<GetCourseReviewsInteractionPayload>(
            'GET',
            `/interactions/${courseId}/referrer/${referrer}?type=course`
        );
    },

    async getUserInteractionsForInstructor(
        instructorId: string,
        referrer: string
    ): Promise<GetInstructorReviewsInteractionPayload> {
        return await client.deserialize<GetInstructorReviewsInteractionPayload>(
            'GET',
            `/interactions/${instructorId}/referrer/${referrer}?type=instructor`
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
        instructorId: string,
        userId: string,
        referrer: string | undefined,
        type: ReviewType
    ): Promise<Response> {
        return await client.post('/interactions', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                kind,
                type,
                courseId,
                instructorId,
                userId,
                referrer,
            }),
        });
    },

    async removeInteraction(
        courseId: string,
        userId: string,
        instructorId: string,
        referrer: string | undefined,
        type: ReviewType
    ): Promise<Response> {
        return await client.delete('/interactions', {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                type,
                courseId,
                instructorId,
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
        id: string | undefined,
        limit: number,
        offset: number,
        userId: string,
        filters: SortFilterDto
    ): Promise<GetCourseWithReviewsPayload | null> {
        return await client.deserialize<GetCourseWithReviewsPayload | null>(
            'POST',
            `/courses/${id}?limit=${limit}&offset=${offset}&userId=${userId}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(filters),
            }
        );
    },

    async getInstructorWithReviews(
        id: string | undefined,
        limit: number,
        offset: number,
        userId: string,
        filters: SortFilterDto
    ): Promise<GetInstructorWithReviewsPayload | null> {
        return await client.deserialize<GetInstructorWithReviewsPayload | null>(
            'POST',
            `/instructors/${id}?limit=${limit}&offset=${offset}&userId=${userId}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(filters),
            }
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

    async getCourseInstructors(
        courseId: String,
    ): Promise<CourseInstructor[]> {
        return await client.deserialize<CourseInstructor[]>(
            'GET',
            `/courses/${courseId}/instructors`,
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
            error: body.errors?.message
        };
    },

    async forgotPassword(username: string): Promise<Response> {
        return await client.get(`/auth/forgot_password?username=${username}`);
    },

    async getReviewsByFTS(query: string, limit: number): Promise<Review[]> {
        return await client.deserialize<Review[]>(
            'GET',
            `/search?query=${query}&limit=${limit}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
        );
    },

    async updatePassword(newPassword: string, token: string): Promise<Response> {
        return await client.put(`/auth/update_password?token=${token}`, {
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({newPassword}),
        });
    },

    async getGradeDistribution(
        subject: string, catalog: string,
    ): Promise<Response> {
        return await client.get(`/grades/distribution?course=${subject}-${catalog}`);
    },

    async addComment(reviewId: string, content: string, userId?: string | null): Promise<Response> {
        return await client.post(`/reviews/${reviewId}/comments`, {
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                content,
                timestamp: ISOFormattedDateUTC4(new Date()),
                userId
            }),
        });
    },

    async deleteComment(reviewId: string, commentId: string, userId?: string | null): Promise<Response> {
        return await client.delete(`/reviews/${reviewId}/comments/${commentId}`, {
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId }),
        });
    },

    async updateComment(reviewId: string, commentId: string, content: string, userId?: string | null): Promise<Response> {
        return await client.put(`/reviews/${reviewId}/comments/${commentId}`, {
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                content,
                userId
            }),
        });
    },
};
