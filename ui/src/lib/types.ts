import type {Course} from "$lib/model/Course";

export type CourseData = Pick<
  Course,
  '_id' | 'subject' | 'title' | 'code' | 'instructors'
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
export type SortByType = (typeof sortByOptions)[number];

export const termsOptions = ['Fall', 'Winter', 'Summer'] as const;
export type CourseTerm = (typeof termsOptions)[number];
export const levelsOptions = ['1XX', '2XX', '3XX', '4XX', '5XX', '6XX', '7XX'];
export const termColorMap: Record<string, string> = {
        fall: 'bg-red-100 text-red-900',
        winter: 'bg-sky-100 text-sky-900',
        summer: 'bg-yellow-100 text-yellow-900',
    };