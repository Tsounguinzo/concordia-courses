import type {Instructor} from "./Instructor";
import type {Schedule} from "./Schedule";

export type Course = {
    _id: string;
    terms: string[];
    instructors: Instructor[];
    prerequisites: string;
    subject: string;
    description: string;
    catalog: string;
    title: string;
    schedules: Schedule[];
};
