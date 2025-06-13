import type { Comment } from './Comment';
import type { ResourceLink } from './ResourceLink';

export type ReviewType = 'course' | 'instructor' | 'school';

export type Review = {
    _id: string;
    type: ReviewType;
    content: string;
    timestamp: string; // Changed from Date to string for consistency with DTOs
    likes: number;
    userId: string;

    // common fields for course and instructor
    difficulty: number; // 0-5
    courseId: string;
    instructorId: string;

    // course specific fields
    experience: number; // 0-5

    // instructor specific fields
    rating: number; // 0-5
    tags: string[];

    // School specific ratings
    schoolId: string;

    // New fields
    comments?: Comment[];
    resourceLinks?: ResourceLink[];
};
