export type Instructor = {
    _id: string;
    firstName: string;
    lastName: string;
    externalLink?: string;
    departments: string[];
    courses: Course[];
    tags: string[];
    avgDifficulty: number;
    avgRating: number;
    difficultyDistribution: number[];
    ratingDistribution: number[];
    reviewCount: number;
    aiSummary: string;
    lastSummaryUpdate: Date;
}

export type Course = {
    subject: string;
    catalog: string;
}

export type CourseInstructor = {
    _id: string;
    firstName: string;
    lastName: string;
    avgDifficulty: number;
    avgRating: number;
    reviewCount: number;
}