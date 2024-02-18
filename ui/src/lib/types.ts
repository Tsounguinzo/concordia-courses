import type {Course} from "$lib/model/Course";

export type CourseData = Pick<
  Course,
  '_id' | 'subject' | 'title' | 'catalog' | 'instructors'
>;

export const sortByOptions = [
        '',
        'Highest Rating',
        'Lowest Rating',
        'Easiest',
        'Hardest',
        'Most Reviews',
        'Least Reviews',
    ] as const;
export const termsOptions = ["Summer","Fall","Fall/Winter","Winter","Spring"] as const;