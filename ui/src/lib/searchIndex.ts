import pkg from 'flexsearch';
import type { Index as typeIndex } from 'flexsearch';
const { Index } = pkg;
import _ from 'lodash';

import data from './data/searchCourses.json';
import type {Instructor} from './model/Instructor';
import {searchResults} from "$lib/store";
import type {CourseData} from "$lib/types";

let coursesIndex: typeIndex | null = null;
let instructorsIndex: typeIndex | null = null;

export const getSearchIndex = () => {
    const courses = data as CourseData[];
    const instructors: Instructor[] = _.uniqBy(
        courses.flatMap((course: CourseData) => course.instructors),
        (instructor: Instructor) => instructor.name
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
            instructorsIndex?.add(i, instructor.name)
        );
    }

    return {courses, instructors, coursesIndex, instructorsIndex};
};

export const updateSearchResults = (
    query: string,
    courses: CourseData[],
    instructors: Instructor[],
    coursesIndex: typeIndex,
    instructorsIndex: typeIndex,
) => {
    const courseSearchResults = coursesIndex
        .search(query, 4)
        ?.map((id) => courses[id as number]);
    const instructorSearchResults = instructorsIndex
        .search(query, 2)
        ?.map((id) => instructors[id as number]);

    searchResults.set({
        query: query,
        courses: courseSearchResults,
        instructors: instructorSearchResults,
    });
};
