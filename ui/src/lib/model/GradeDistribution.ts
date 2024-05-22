import type {Course} from "$lib/model/Instructor";

export type GradeDistribution = {
    course: Course;
    term: string;
    year: number;
    classAverage: number;
    classSize: number;
    grades: Grade[];
};

type Grade = {
    grade: string;
    count: number;
};