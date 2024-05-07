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
}

type InstructorCourse = {
    subject: string;
    catalog: string;
}