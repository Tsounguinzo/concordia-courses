export type Instructor = {
    _id: string;
    firstName: string;
    lastName: string;
    department: string;
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