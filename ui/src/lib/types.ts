import type {Course} from "$lib/model/Course";

export type CourseData = Pick<
  Course,
  '_id' | 'subject' | 'title' | 'catalog'
>;

export interface RequestOptions {
        method: string;
        headers: { 'Content-Type': string };
        body?: string;
}

export const sortByOptions = [
        '',
        'Best Experience',
        'Worst Experience',
        'Easiest',
        'Hardest',
        'Most Reviews',
        'Least Reviews',
    ] as const;

export const feedSortByOptions = [
        '',
        'Best',
        'Hot',
        'New',
    ] as const;
export const termsOptions = ["Summer","Fall","Fall/Winter","Winter","Spring"] as const;