import type {Course} from "$lib/model/Course";

export type CourseData = Pick<
  Course,
  '_id' | 'subject' | 'title' | 'catalog' | 'instructors'
>;

export interface RequestOptions {
        method: string;
        headers: { 'Content-Type': string };
        body?: string;
}

export interface SortFilterDto {
        sortType: "recent" | "rating" | "experience" | "difficulty" | "likes";
        reverse: boolean;            // ascending/descending
        selectedInstructor: string;  // e.g. "john-doe"
        selectedCourse: string;      // e.g. "COMP228"
}

export const allSortByOptions = [
        '',
        'Best Experience',
        'Best Rating',
        'Worst Experience',
        'Worst Rating',
        'Easiest',
        'Hardest',
        'Most Reviews',
        'Least Reviews',
    ] as const;

export const getSortByOptions = (instructorsModeOn: boolean) => {
        const sortOptions: { [key: string]: boolean } = {
                'Best Rating': instructorsModeOn,
                'Worst Rating': instructorsModeOn,
                'Best Experience': !instructorsModeOn,
                'Worst Experience': !instructorsModeOn,
                'Easiest': true,
                'Hardest': true,
                'Most Reviews': true,
                'Least Reviews': true,
        };

        return ['', ...Object.keys(sortOptions).filter(option => sortOptions[option])];
};

export const feedSortByOptions = [
        '',
        'Best',
        'Hot',
        'New',
    ] as const;
export const termsOptions = ["Summer 2025","Fall 2025","Fall/Winter 2025-2026","Winter 2026","Spring 2026","Summer 2026"] as const;