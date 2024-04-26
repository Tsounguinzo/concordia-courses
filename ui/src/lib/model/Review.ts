export type ReviewType = 'course' | 'instructor' | 'school';

export type Review = {
    _id: string;
    type: ReviewType;
    content: string;
    timestamp: Date;
    likes: number;
    thumbsDownTotal: number;
    thumbsUpTotal: number;

    // common fields for course and instructor
    difficulty: number; // 0-5
    userId: string;
    courseId: string;
    instructorId: string;

    // course specific fields
    experience: number; // 0-5

    // instructor specific fields
    rating: number; // 0-5
    tags: string[];

    // School specific ratings
    internetRating: number; // 0-5
    locationRating: number; // 0-5
    opportunitiesRating: number; // 0-5
    reputationRating: number; // 0-5
    safetyRating: number; // 0-5
    socialRating: number; // 0-5
    foodRating: number; // 0-5
    happinessRating: number; // 0-5
    facilitiesRating: number; // 0-5
    clubsRating: number; // 0-5
};
