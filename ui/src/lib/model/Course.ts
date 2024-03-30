import type {Schedule} from "./Schedule";

export type Course = {
    _id: string;
    terms: string[];
    prerequisites: string;
    subject: string;
    description: string;
    catalog: string;
    title: string;
    avgDifficulty: number;
    avgExperience: number;
    reviewCount: number;
    schedules: Schedule[];
};
