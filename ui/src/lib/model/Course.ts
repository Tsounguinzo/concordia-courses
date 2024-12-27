import type {Schedule} from "./Schedule";

export type Course = {
    _id: string;
    terms: string[];
    instructors: string[];
    prerequisites: string;
    subject: string;
    description: string;
    catalog: string;
    title: string;
    avgDifficulty: number;
    avgExperience: number;
    difficultyDistribution: number[];
    experienceDistribution: number[];
    reviewCount: number;
    classUnit: number;
    ConUCourseID: string;
    notes: string;
    schedules: Schedule[];
};
