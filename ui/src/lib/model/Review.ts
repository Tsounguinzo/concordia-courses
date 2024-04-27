export type ReviewType = 'course' | 'instructor' | 'school';

export type Review = {
    _id: string;
    type: ReviewType;
    content: string;
    timestamp: Date;
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
};
