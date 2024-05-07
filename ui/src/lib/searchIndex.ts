import pkg from 'flexsearch';
import type { Index as typeIndex } from 'flexsearch';
const { Index } = pkg;

import data from './data/searchCourses.json';
import {searchResults} from "$lib/store";
import type {CourseData} from "$lib/types";
import uniq from "lodash/uniq";

let coursesIndex: typeIndex | null = null;
let instructorsIndex: typeIndex | null = null;

export const getSearchIndex = () => {
    const courses = data as CourseData[];
    const instructors: string[] = uniq(
        courses.flatMap((course: CourseData) => course.instructors)
    );

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

    if (instructorsIndex === null) {
        instructorsIndex = new Index({
            tokenize: 'forward',
        });

        instructors.forEach((instructor, i) =>
            instructorsIndex?.add(i, instructor)
        );
    }

    return { courses, instructors, coursesIndex, instructorsIndex };
};

export const updateSearchResults = (
    query: string,
    courses: CourseData[],
    coursesIndex: typeIndex,
    instructors: string[],
    instructorsIndex: typeIndex,
) => {
    const courseSearchResults = coursesIndex
        .search(query, 3)
        ?.map((id) => courses[id as number]);
    const instructorSearchResults = instructorsIndex
        .search(query, 3)
        ?.map((id) => instructors[id as number]);

    searchResults.set({
        query,
        courses: courseSearchResults,
        instructors: instructorSearchResults,
    });
};
