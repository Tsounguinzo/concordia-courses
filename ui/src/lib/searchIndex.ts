import pkg from 'flexsearch';
import type { Index as typeIndex } from 'flexsearch';
const { Index } = pkg;

import data from './data/searchCourses.json';
import {searchResults} from "$lib/store";
import type {CourseData} from "$lib/types";

let coursesIndex: typeIndex | null = null;

export const getSearchIndex = () => {
    const courses = data as CourseData[];

    if (coursesIndex === null) {
        coursesIndex = new Index({
            tokenize: 'forward',
        });

        courses.forEach((course, i) =>
            coursesIndex?.add(
                i,
                `${course._id} ${course.subject} ${course.title} ${course.catalog}`
            )
        );
    }

    return {courses, coursesIndex};
};

export const updateSearchResults = (
    query: string,
    courses: CourseData[],
    coursesIndex: typeIndex,
) => {
    const courseSearchResults = coursesIndex
        .search(query, 4)
        ?.map((id) => courses[id as number]);

    searchResults.set({
        query,
        courses: courseSearchResults,
    });
};
