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
export const termsOptions = ["Summer","Fall","Fall/Winter","Winter","Spring"] as const;