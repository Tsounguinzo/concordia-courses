export type Instructor = {
    _id: string;
    firstName: string;
    lastName: string;
    departments: string[];
    courses: InstructorCourse[];
    tags: string[];
    avgDifficulty: number;
    avgRating: number;
    reviewCount: number;
    aiSummary: string;
    lastSummaryUpdate: Date;
}

type InstructorCourse = {
    subject: string;
    catalog: string;
}