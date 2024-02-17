import type {Instructor} from "./Instructor";
import type {Schedule} from "./Schedule";

export type Course = {
    _id: string;
    terms: string[];
    instructors: Instructor[];
    classUnit: string;
    prerequisites: string;
    crosslisted: string;
    session: string;
    subject: string;
    description: string;
    catalog: string;
    title: string;
    topicID: string;
    topicDescription: string;
    classStatus: string;
    career: string;
    departmentCode: string;
    departmentDescription: string;
    facultyCode: string;
    facultyDescription: string;
    schedules: Schedule[];
};
